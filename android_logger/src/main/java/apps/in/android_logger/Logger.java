package apps.in.android_logger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Logger object.
 */
public class Logger {

    private static final String LOG_PATH = "logs";
    private static final String LOG_FILE_NAME_CURRENT = "current.log";
    private static final String LOG_FILE_NAME_PREVIOUS = "previous.log";
    private static final String LOG_FILE_NAME_ZIP = "log.zip";
    private static Logger logger;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS", Locale.US);

    /**
     * Initializer for Logger.
     */
    public static class Initializer {

        private final Logger instance;
        private final Context context;

        /**
         * Instantiates a new Initializer.
         *
         * @param context application context
         */
        public Initializer(Context context) {
            instance = new Logger();
            this.context = context;
        }

        /**
         * Enables writing logs to console.
         *
         * @param appTag the tag for console logs
         * @return current Initializer
         */
        public Initializer writeToConsole(String appTag) {
            instance.writeToConsole = true;
            instance.appTag = appTag;
            return this;
        }

        /**
         * Enables writing logs to file.
         *
         * @return current Initializer
         */
        public Initializer writeToFile() {
            instance.setWriteToFile(context);
            return this;
        }

        /**
         * Setup Application id.
         *
         * @param appId Application id
         * @return current Initializer
         */
        public Initializer setAppId(String appId) {
            instance.appId = appId;
            return this;
        }

        /**
         * Setup Application version name.
         *
         * @param appVersion Application version name
         * @return current Initializer
         */
        public Initializer setAppVersion(String appVersion) {
            instance.appVersion = appVersion;
            return this;
        }

        /**
         * Finishes Logger initializing.
         */
        public void initialize() {
            instance.startLogging();
        }

    }

    private final Semaphore fileSemaphore = new Semaphore(1, true);

    private File logFile;
    private String appTag;
    private String appId;
    private String appVersion;
    private boolean writeToConsole;
    private boolean writeToFile;
    private String previousLogPath;
    private String currentLogPath;
    private String zipLogPath;

    /**
     * Private constructor for Logger object.
     */
    private Logger() {

    }

    /**
     * Creates new Initializer for logger.
     *
     * @param context app context
     * @return initializer instance
     */
    public static Initializer initializeLogger(Context context) {
        return new Initializer(context);
    }

    /**
     * Log simple message.
     *
     * @param message the message
     */
    public static void log(String message) {
        getLogger().logMessage(message);
    }

    /**
     * Log message with object context.
     *
     * @param context object context
     * @param message the message
     */
    public static void log(Object context, String message) {
        getLogger().logMessage(context, message);
    }

    /**
     * Log content of bundle object.
     *
     * @param description the description of bundle
     * @param bundle      the bundle
     */
    public static void log(Object context, String description, Bundle bundle) {
        getLogger().logMessage(description, bundle);
    }

    /**
     * Log content of intent .
     *
     * @param description the description of intent
     * @param intent      the intent
     */
    public static void log(Object context, String description, Intent intent) {
        getLogger().logMessage(description, intent);
    }

    /**
     * Log exception.
     *
     * @param description the description of exception
     * @param t           the exception
     */
    public static void log(Object context, String description, Throwable t) {
        getLogger().logMessage(description, t);
    }

    /**
     * Share logs.
     *
     * @param context app context
     * @param title   the title of share method selection dialog
     */
    public static void shareLog(Activity context, String title) {
        shareLog(context, title, true);
    }

    /**
     * Share logs.
     *
     * @param context app context
     * @param title   the title of share method selection dialog
     * @param zip     should logs be packed to zip-archive
     */
    public static void shareLog(Activity context, String title, boolean zip) {
        Intent intent;
        if (zip) {
            intent = shareZippedLog(context);
        } else {
            intent = shareRawLogs(context);
        }
        if (intent != null) {
            Intent chooserIntent = Intent.createChooser(intent, title);
            if (chooserIntent != null) {
                context.startActivityForResult(chooserIntent, 123);
                return;
            }
        }
        Toast.makeText(context, R.string.log_sharing_failed, Toast.LENGTH_LONG).show();
    }

    /**
     * Gets current log file name.
     *
     * @return the current log file name
     */
    public static String getCurrentLogFileName() {
        return getLogger().currentLogPath;
    }

    /**
     * Gets previous log file name.
     *
     * @return the previous log file name
     */
    public static String getPreviousLogFileName() {
        return getLogger().previousLogPath;
    }

    /**
     * Zips log files to single zip-archive.
     *
     * @return path to zip-archive with log files
     */
    public static String getLogZip() {
        return getLogger().zipLog();
    }

    /**
     * Safe getter of logger instance
     *
     * @return
     */
    private static Logger getLogger() {
        if (logger != null) {
            return logger;
        } else {
            throw new LoggerNotInitializedException();
        }
    }

    /**
     * Zips log files to single archive and prepare intent for sharing
     *
     * @param context app context
     * @return intent for logs sharing
     */
    private static Intent shareZippedLog(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String filePath = getLogger().zipLog();
        if (filePath != null) {
            Uri uri = getFileUri(context, filePath);
            if (uri != null) {
                intent.setType("application/zip");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(Intent.EXTRA_SUBJECT, String.format("Logs of %s(%s)", logger.appId, logger.appVersion));
            }
            return intent;
        }
        return null;
    }

    /**
     * Prepares intent for sharing raw log files
     *
     * @param context app context
     * @return intent for logs sharing
     */
    private static Intent shareRawLogs(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        Logger logger = getLogger();
        ArrayList<Uri> uris = new ArrayList<>();
        String[] files = new String[]{logger.previousLogPath, logger.currentLogPath};
        for (String filePath : files) {
            Uri uri = getFileUri(context, filePath);
            if (uri != null) {
                uris.add(uri);
            }
        }
        if (uris.size() > 0) {
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_STREAM, uris);
            intent.putExtra(Intent.EXTRA_SUBJECT, String.format("Logs of %s(%s)", logger.appId, logger.appVersion));
            return intent;
        }
        return null;
    }

    /**
     * Prepares uri for given log file
     *
     * @param context app context
     * @param path    path to log file
     * @return uri for given log file
     */
    private static Uri getFileUri(Context context, String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                return FileProvider.getUriForFile(context, String.format("%s.android_logger", context.getPackageName()), file);
            }
        }
        return null;
    }

    /**
     * Setup file logging
     *
     * @param context app context
     */
    private void setWriteToFile(Context context) {
        this.writeToFile = true;
        try {
            File directory = new File(context.getFilesDir(), LOG_PATH);
            if (!directory.exists()) {
                directory.mkdir();
            }
            File zip = new File(directory, LOG_FILE_NAME_ZIP);
            if (zip.exists()) {
                zip.delete();
            }
            zipLogPath = zip.getAbsolutePath();
            File previous = new File(directory, LOG_FILE_NAME_PREVIOUS);
            if (previous.exists()) {
                previous.delete();
            }
            logFile = new File(directory, LOG_FILE_NAME_CURRENT);
            if (logFile.exists()) {
                logFile.renameTo(previous);
                previousLogPath = previous.getAbsolutePath();
                logFile.delete();
            }
            logFile.createNewFile();
            currentLogPath = logFile.getPath();
        } catch (IOException e) {
            this.writeToFile = false;
        }
    }

    /**
     * Starts logging
     */
    private void startLogging() {
        logger = this;
        final Thread.UncaughtExceptionHandler regularHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.UncaughtExceptionHandler logHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logMessage("Uncaught exception", e);
                if (regularHandler != null) {
                    regularHandler.uncaughtException(t, e);
                }
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(logHandler);
        log(String.format("Logger started. Version: %s", BuildConfig.VERSION));
        log(String.format("Application ID: %s. Version: %s", appId, appVersion));
        log(String.format(Locale.US, "%s (SDK %d)", Build.MODEL, Build.VERSION.SDK_INT));
    }

    /**
     * Zips log files to single zip-archive
     *
     * @return path to zip-archive
     */
    private String zipLog() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(zipLogPath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            addLogFileToZip(zipOutputStream, previousLogPath, LOG_FILE_NAME_PREVIOUS);
            addLogFileToZip(zipOutputStream, currentLogPath, LOG_FILE_NAME_CURRENT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return zipLogPath;
    }

    /**
     * Adds given log file to zipOutputStream object
     *
     * @param zipOutputStream stream to which log file should be added
     * @param logFilePath     path to log file which should be added to zip-archive
     * @param fileName        name of log file in zip-archive
     */
    private void addLogFileToZip(ZipOutputStream zipOutputStream, String logFilePath, String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(logFilePath);
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            byte[] buffer = new byte[65545];
            int count;
            while ((count = fileInputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, count);
            }
            zipOutputStream.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs content of the bundle object
     *
     * @param description description of the bundle object
     * @param bundle      bundle object
     */
    private void logMessage(String description, Bundle bundle) {
        log(getBundleString(description, bundle));
    }


    /**
     * Logs content of the intent object
     *
     * @param description description of the intent object
     * @param intent      intent object
     */
    private void logMessage(String description, Intent intent) {
        log(getIntentString(description, intent));
    }

    /**
     * Logs message with object context
     *
     * @param context object context
     * @param message log message
     */
    private void logMessage(Object context, String message) {
        String m = String.format("%s: %s", getComponentName(context), message);
        logMessage(m);
    }

    /**
     * Logs exception
     *
     * @param description description of the exception
     * @param e           exception
     */
    private void logMessage(String description, Throwable e) {
        logMessage(String.format("%s:\n%s", description, getExceptionString(e)));
    }

    /**
     * Logs given message in according with logger settings
     *
     * @param message message to log
     */
    private void logMessage(String message) {
        if (writeToConsole) {
            logToConsole(message);
        }
        if (writeToFile) {
            logToFile(message);
        }
    }

    /**
     * Writes given message to console
     *
     * @param message message to log
     */
    private void logToConsole(String message) {
        Log.i(appTag, message);
    }

    /**
     * Writes given message to file
     *
     * @param message message to log
     */
    private void logToFile(final String message) {
        final String text = String.format("%s - %s", dateFormat.format(new Date()), message);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fileSemaphore.acquire();
                    appendToFile(text);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    fileSemaphore.release();
                }
            }
        }).start();
    }

    /**
     * Appends given message to log file
     *
     * @param message message to log
     */
    private void appendToFile(String message) {
        try (
                FileOutputStream fos = new FileOutputStream(logFile, true);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                BufferedWriter bw = new BufferedWriter(osw)) {
            bw.newLine();
            bw.append(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the string representation of the given bundle object
     *
     * @param description bundle object description
     * @param bundle      bundle object
     * @return string representation of the bundle object
     */
    private String getBundleString(String description, Bundle bundle) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            stringBuilder.append(String.format(Locale.US, "\t%s (%d items)", description, keys.size()));
            for (String key : keys) {
                stringBuilder.append(String.format("\n\t%s = %s", key, String.valueOf(bundle.get(key))));
            }
        } else {
            stringBuilder.append(String.format("\t%s: null", description));
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the string representation of the given intent object
     *
     * @param description description of the intent object
     * @param intent      intent object
     * @return string representation of the intent object
     */
    private String getIntentString(String description, Intent intent) {
        StringBuilder stringBuilder = new StringBuilder();
        if (intent != null) {
            stringBuilder.append(description);
            stringBuilder.append(String.format("\nACTION: %s", intent.getAction()));
            stringBuilder.append(String.format("\nCOMPONENT NAME: %s", getComponentName(intent.getComponent())));
            stringBuilder.append(String.format("\n%s", getBundleString("EXTRAS", intent.getExtras())));
        } else {
            stringBuilder.append(String.format("\t%s: null", description));
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the string representation of context object class
     *
     * @param component context object
     * @return string representation of the context object
     */
    private String getComponentName(Object component) {
        if (component != null) {
            return component.getClass().getSimpleName();
        }
        return "null";
    }

    /**
     * Returns the string representation of exception
     *
     * @param e exception
     * @return string representation of exception
     */
    private String getExceptionString(Throwable e) {
        if (e != null) {
            Throwable throwable = e;
            StringBuilder stringBuilder = new StringBuilder();
            do {
                stringBuilder.append(String.format("%s: %s", getComponentName(throwable), throwable.getMessage()));
                for (StackTraceElement element : throwable.getStackTrace()) {
                    stringBuilder.append("\n\t");
                    stringBuilder.append(element.toString());
                }
                throwable = throwable.getCause();
                if (throwable != null) {
                    stringBuilder.append("\nCaused by: ");
                }
            } while (throwable != null);
            return stringBuilder.toString();
        } else return "";
    }

}
