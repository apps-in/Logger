package apps.in.android_logger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
    private static final int LOG_TO_FILE_MAX_DAYS_DEFAULT_VALUE = 1;
    private static final int LOG_TO_FILE_MIN_COUNT_DEFAULT_VALUE = 2;
    private static final String LOG_FILE_NAME_SUFFIX = ".log";
    private static final String LOG_FILE_NAME_ZIP = "log.zip";
    private static final String PREFERENCES_FILE = "logger.pref";
    private static final String CRASH_PREF_KEY = "WAS_CRASH";
    private static Logger logger;
    private static final SimpleDateFormat fileNameDateTimeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.US);
    private static final SimpleDateFormat logDateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS", Locale.US);

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
            instance = new Logger(context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE));
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
            return writeToFile(LOG_TO_FILE_MAX_DAYS_DEFAULT_VALUE, LOG_TO_FILE_MIN_COUNT_DEFAULT_VALUE);
        }

        /**
         * Enables writing logs to file.
         *
         * @param maxDays  maximum days before current date to keep log files
         * @param minCount minimum number of log files to keep
         * @return current Initializer
         */
        public Initializer writeToFile(int maxDays, int minCount) {
            instance.setWriteToFile(context, Math.max(maxDays, LOG_TO_FILE_MAX_DAYS_DEFAULT_VALUE), Math.max(minCount, LOG_TO_FILE_MIN_COUNT_DEFAULT_VALUE));
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
         * Enables writing logs to external storage
         *
         * @param externalLogger External logging action
         * @return current Initializer
         */
        public Initializer setExternalLogger(ExternalLogger externalLogger) {
            instance.externalLogger = externalLogger;
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

    private final SharedPreferences sharedPreferences;
    private ExternalLogger externalLogger;
    private File logsDirectory;
    private File logFile;
    private String appTag;
    private String appId;
    private String appVersion;
    private boolean writeToConsole;
    private boolean writeToFile;
    private String zipLogPath;

    /**
     * Private constructor for Logger object.
     *
     * @param sharedPreferences logger preferences
     */
    private Logger(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
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
     * Checks previous unreported crashes.
     */
    public static boolean hasUncheckedCrashes() {
        SharedPreferences sharedPreferences = getLogger().sharedPreferences;
        if (sharedPreferences.contains(CRASH_PREF_KEY)) {
            boolean result = sharedPreferences.getBoolean(CRASH_PREF_KEY, false);
            sharedPreferences.edit().remove(CRASH_PREF_KEY).apply();
            return result;
        }
        return false;
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
        getLogger().logMessage(getComponentName(context), message);
    }

    /**
     * Log message with object context.
     *
     * @param context context description
     * @param message the message
     */
    public static void log(String context, String message) {
        getLogger().logMessage(context, message);
    }

    /**
     * Log content of bundle object.
     *
     * @param context     object context
     * @param description the description of bundle
     * @param bundle      the bundle
     */
    public static void log(Object context, String description, Bundle bundle) {
        log(getComponentName(context), description, bundle);
    }

    /**
     * Log content of bundle object.
     *
     * @param context     context description
     * @param description the description of bundle
     * @param bundle      the bundle
     */
    public static void log(String context, String description, Bundle bundle) {
        log(context, getBundleString(description, bundle));
    }

    /**
     * Log content of intent .
     *
     * @param context     object context
     * @param description the description of intent
     * @param intent      the intent
     */
    public static void log(Object context, String description, Intent intent) {
        log(getComponentName(context), description, intent);
    }

    /**
     * Log content of intent .
     *
     * @param context     context description
     * @param description the description of intent
     * @param intent      the intent
     */
    public static void log(String context, String description, Intent intent) {
        log(context, getIntentString(description, intent));
    }

    /**
     * Log exception.
     *
     * @param context     object context
     * @param description the description of exception
     * @param t           the exception
     */
    public static void log(Object context, String description, Throwable t) {
        log(getComponentName(context), description, t);
    }

    /**
     * Log exception.
     *
     * @param context     context description
     * @param description the description of exception
     * @param t           the exception
     */
    public static void log(String context, String description, Throwable t) {
        log(context, getExceptionString(description, t));
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
        List<String> files = new LinkedList<>();
        File directory = getLogger().logsDirectory;
        if (directory != null && directory.exists()) {
            File[] directoryFiles = directory.listFiles();
            if (directoryFiles != null) {
                for (File directoryFile : directoryFiles) {
                    if (directoryFile.getName().endsWith(LOG_FILE_NAME_SUFFIX)) {
                        files.add(directoryFile.getAbsolutePath());
                    }
                }
            }
        }
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
    public static Uri getFileUri(Context context, String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                return FileProvider.getUriForFile(context, String.format("%s.android_logger", context.getPackageName()), file);
            }
        }
        return null;
    }

    /**
     * Returns the string representation of context object class
     *
     * @param component context object
     * @return string representation of the context object
     */
    private static String getComponentName(Object component) {
        if (component != null) {
            return component.getClass().getSimpleName();
        }
        return "null";
    }

    /**
     * Returns the string representation of the given bundle object
     *
     * @param description bundle object description
     * @param bundle      bundle object
     * @return string representation of the bundle object
     */
    private static String getBundleString(String description, Bundle bundle) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            stringBuilder.append(String.format(Locale.US, "\t%s (%d items)", description, keys.size()));
            for (String key : keys) {
                stringBuilder.append(String.format("\n\t%s = %s", key, bundle.get(key)));
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
    private static String getIntentString(String description, Intent intent) {
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
     * Returns the string representation of exception
     *
     * @param description exception description
     * @param e           exception
     * @return string representation of exception
     */
    private static String getExceptionString(String description, Throwable e) {
        return String.format("%s:\n%s", description, getExceptionString(e));
    }

    /**
     * Returns the string representation of exception
     *
     * @param e exception
     * @return string representation of exception
     */
    private static String getExceptionString(Throwable e) {
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

    /**
     * Setup file logging
     *
     * @param context  app context
     * @param maxDays  maximum days before current date to keep log files
     * @param minCount minimum number of log files to keep
     */
    private void setWriteToFile(Context context, int maxDays, int minCount) {
        this.writeToFile = true;
        try {
            logsDirectory = new File(context.getFilesDir(), LOG_PATH);
            if (!logsDirectory.exists()) {
                logsDirectory.mkdir();
            }
            File zip = new File(logsDirectory, LOG_FILE_NAME_ZIP);
            if (zip.exists()) {
                zip.delete();
            }
            zipLogPath = zip.getAbsolutePath();
            Calendar calendar = Calendar.getInstance();
            String currentLogFileName = fileNameDateTimeFormat.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, -maxDays);
            Date min = calendar.getTime();
            File[] files = logsDirectory.listFiles();
            LinkedList<File> logFiles = new LinkedList<>();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    if (name.endsWith(LOG_FILE_NAME_SUFFIX)) {
                        logFiles.add(file);
                    } else {
                        try {
                            file.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Collections.sort(logFiles, (o1, o2) -> o1.getName().compareTo(o2.getName()));
            while (logFiles.size() > minCount) {
                File file = logFiles.getFirst();
                String name = file.getName();
                String date = name.substring(0, name.length() - LOG_FILE_NAME_SUFFIX.length());
                try {
                    Date fileDate = fileNameDateTimeFormat.parse(date);
                    if (fileDate.before(min)) {
                        try {
                            file.delete();
                            logFiles.removeFirst();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            logFile = new File(logsDirectory, currentLogFileName + ".log");
            if (logFile.exists()) {
                logFile.delete();
            }
            logFile.createNewFile();
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
                logMessage(getExceptionString("Uncaught exception", e));
                sharedPreferences.edit().putBoolean(CRASH_PREF_KEY, true).apply();
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
            if (logsDirectory != null && logsDirectory.exists()) {
                File[] files = logsDirectory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().endsWith(LOG_FILE_NAME_SUFFIX)) {
                            addLogFileToZip(zipOutputStream, file.getAbsolutePath(), file.getName());
                        }
                    }
                }
            }
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
     * Logs message with object context
     *
     * @param context context description
     * @param message log message
     */
    private void logMessage(String context, String message) {
        logMessage(context, message, false);
    }

    /**
     * Logs message with object context
     *
     * @param context   context description
     * @param message   log message
     * @param external  should log to external storage
     */
    private void logMessage(String context, String message, boolean external) {
        String m = String.format("%s: %s", context, message);
        logMessage(m, external);
    }

    /**
     * Logs given message in according with logger settings
     *
     * @param message message to log
     */
    private void logMessage(String message) {
        logMessage(message, false);
    }

    /**
     * Logs given message in according with logger settings
     *
     * @param message   message to log
     * @param external  should log to external storage
     */
    private void logMessage(String message, boolean external) {
        if (writeToConsole) {
            logToConsole(message);
        }
        if (writeToFile) {
            logToFile(message);
        }
        if (external && externalLogger != null){
            externalLogger.log(message);
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
        final String text = String.format("%s - %s", logDateTimeFormat.format(new Date()), message);
        new Thread(() -> {
            try {
                fileSemaphore.acquire();
                appendToFile(text);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fileSemaphore.release();
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

}
