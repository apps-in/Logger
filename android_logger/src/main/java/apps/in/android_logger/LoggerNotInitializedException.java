package apps.in.android_logger;

public class LoggerNotInitializedException extends RuntimeException {

    public LoggerNotInitializedException() {
        super("Logger not initialized. Use InLogger.Initializer class to setup InLogger and then call initialize method to start logging.");
    }
}
