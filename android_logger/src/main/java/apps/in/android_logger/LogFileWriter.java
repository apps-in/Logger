package apps.in.android_logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class LogFileWriter {

    private final int MAX_BUFFER_SIZE = 1000;

    private final Semaphore bufferSemaphore = new Semaphore(1, true);
    private final Semaphore fileSemaphore;
    private final File logFile;

    private volatile boolean isWorking = true;
    private Thread writerThread;
    private LinkedList<String> buffer = new LinkedList<>();

    public LogFileWriter(File logFile) {
        this(logFile, new Semaphore(1, true));
    }

    public LogFileWriter(File logFile, Semaphore fileSemaphore) {
        this.logFile = logFile;
        this.fileSemaphore = fileSemaphore != null ? fileSemaphore : new Semaphore(1, true);
        if (logFile != null) {
            writerThread = new Thread(() -> {
                while (isWorking) {
                    writeBufferToFile();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                writeBufferToFile();
            });
            writerThread.start();
        }
    }

    /**
     * Appends given message to buffer
     *
     * @param message message to log
     */
    public void logToFile(String message) {
        try {
            bufferSemaphore.acquire();
            if (buffer.size() < MAX_BUFFER_SIZE) {
                buffer.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bufferSemaphore.release();
        }
    }

    /**
     * Writes any pending buffer entries to disk without stopping the writer.
     */
    public void drain() {
        writeBufferToFile();
    }

    /**
     * Appends buffer to log file
     */
    private void writeBufferToFile() {
        LinkedList<String> localBuffer = null;
        try {
            bufferSemaphore.acquire();
            if (!buffer.isEmpty()) {
                localBuffer = buffer;
                buffer = new LinkedList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bufferSemaphore.release();
        }
        if (localBuffer != null) {
            boolean acquired = false;
            try {
                fileSemaphore.acquire();
                acquired = true;
                try (
                        FileOutputStream fos = new FileOutputStream(logFile, true);
                        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                        BufferedWriter bw = new BufferedWriter(osw)) {
                    for (String message : localBuffer) {
                        bw.newLine();
                        bw.append(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (acquired) {
                    fileSemaphore.release();
                }
            }
        }
    }

    public void flush() {
        try {
            isWorking = false;
            if (writerThread != null) {
                writerThread.interrupt();
                writerThread.join(10000);
            }
            writeBufferToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
