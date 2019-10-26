package apps.in.loggerapp;

import android.app.Application;

import apps.in.android_logger.Logger;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.initializeLogger(this)
                .writeToConsole("TEST")
                .writeToFile()
                .initialize();
    }
}
