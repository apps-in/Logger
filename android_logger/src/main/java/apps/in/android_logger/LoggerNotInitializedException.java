package apps.in.android_logger;

public class LoggerNotInitializedException extends RuntimeException {

    public LoggerNotInitializedException() {
        super("Logger not initialized. Use Logger.Initializer class to setup Logger and then call initialize method to start logging.");
    }
}
