package apps.in.android_logger;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;

import java.util.Locale;

public abstract class LogService extends Service {

    @Override
    public void onCreate() {
        Logger.log(this, "Lifecycle: onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.log(this, String.format(Locale.US, "Lifecycle: onStartCommand, flags: %d, startId: %d", flags, startId));
        Logger.log(this,"INTENT", intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Logger.log(this, "Lifecycle: onDestroy");
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Logger.log(this, "Lifecycle: onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        Logger.log(this, "Lifecycle: onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Logger.log(this, "Lifecycle: onTrimMemory");
        super.onTrimMemory(level);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Logger.log(this, "Lifecycle: onTaskRemoved");
        Logger.log(this,"INTENT", rootIntent);
        super.onTaskRemoved(rootIntent);
    }

}
