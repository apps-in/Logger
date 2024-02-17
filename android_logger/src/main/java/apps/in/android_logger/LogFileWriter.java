package apps.in.android_logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class LogFileWriter {

    private final int MAX_BUFFER_SIZE = 1000;

    private final Semaphore bufferSemaphore = new Semaphore(1, true);
    private final File logFile;

    private boolean isWorking = true;
    private Thread writerThread;
    private LinkedList<String> buffer = new LinkedList<>();

    public LogFileWriter(File logFile) {
        this.logFile = logFile;
        if (logFile != null) {
            writerThread = new Thread(() -> {
                try {
                    while (isWorking) {
                        writeBufferToFile();
                        Thread.sleep(500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            try (
                    FileOutputStream fos = new FileOutputStream(logFile, true);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    BufferedWriter bw = new BufferedWriter(osw)) {
                for (String message : localBuffer) {
                    bw.newLine();
                    bw.append(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void flush(){
        try {
            Thread.sleep(500);
            isWorking = false;
            writerThread.join(10000);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
