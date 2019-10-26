package apps.in.android_logger;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

public class LogPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(this, "Lifecycle: onCreate");
        Logger.log(this,"savedInstanceState", savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Logger.log(this, "Lifecycle: onPostCreate");
        Logger.log(this,"savedInstanceState", savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Logger.log(this, "Lifecycle: onPostResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.log(this, "Lifecycle: onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.log(this, "Lifecycle: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.log(this, "Lifecycle: onDestroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Logger.log(this, "Lifecycle: onLowMemory");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.log(this, "Lifecycle: onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.log(this, "Lifecycle: onResume");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Logger.log(this, String.format(Locale.US, "ActivityResult received, requestCode = %d, resultCode = %d", requestCode, resultCode));
        Logger.log(this,"Data", data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        Logger.log(this, "Starting Activity");
        Logger.log(this,"Intent", intent);
        Logger.log(this,"Options", options);
        super.startActivity(intent, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        Logger.log(this, String.format(Locale.US, "Starting Activity for result (request code: %d)", requestCode));
        Logger.log(this,"Intent", intent);
        Logger.log(this,"Options", options);
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public ComponentName startService(Intent service) {
        Logger.log(this, "Starting service");
        Logger.log(this,"Intent", service);
        ComponentName componentName = super.startService(service);
        Logger.log(this, String.format("Service component name: %s", componentName != null ? componentName.getClass().getSimpleName() : "null"));
        return componentName;
    }

    @Override
    public ComponentName startForegroundService(Intent service) {
        Logger.log(this, "Starting foreground service");
        Logger.log(this,"Intent", service);
        ComponentName componentName = super.startForegroundService(service);
        Logger.log(this, String.format("Service component name: %s", componentName != null ? componentName.getClass().getSimpleName() : "null"));
        return componentName;
    }

    @Override
    public int checkSelfPermission(String permission) {
        int result = super.checkSelfPermission(permission);
        Logger.log(this, String.format("Checking permission: %s, result: %s", permission, result == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
        return result;
    }

    public void requestPermission(String[] permissions, int requestCode){
        if (Build.VERSION.SDK_INT >= 23) {
            Logger.log(this, String.format(Locale.US, "Requesting permissions (request code = %d): %s", requestCode, TextUtils.join(", ", permissions)));
            requestPermissions(permissions, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(Locale.US, "Permission request result received (request code: %d)", requestCode));
        for (int i = 0; i < Math.min(permissions.length, grantResults.length); i++) {
            stringBuilder.append(String.format("\n\tPermission: %s, Result: %s", permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
        }
        Logger.log(this, stringBuilder.toString());
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
