package apps.in.android_logger;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;

import java.util.Locale;

public abstract class LogService extends Service {

    @Override
    public void onCreate() {
        InLogger.log(this, "Lifecycle: onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        InLogger.log(this, String.format(Locale.US, "Lifecycle: onStartCommand, flags: %d, startId: %d", flags, startId));
        InLogger.log(this,"INTENT", intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        InLogger.log(this, "Lifecycle: onDestroy");
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        InLogger.log(this, "Lifecycle: onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        InLogger.log(this, "Lifecycle: onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        InLogger.log(this, "Lifecycle: onTrimMemory");
        super.onTrimMemory(level);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        InLogger.log(this, "Lifecycle: onTaskRemoved");
        InLogger.log(this,"INTENT", rootIntent);
        super.onTaskRemoved(rootIntent);
    }

}
