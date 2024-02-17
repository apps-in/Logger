package apps.in.android_logger;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public abstract class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InLogger.log(this, "Lifecycle: onCreate");
        InLogger.log(this,"savedInstanceState", savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        InLogger.log(this, "Lifecycle: onPostCreate");
        InLogger.log(this,"savedInstanceState", savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        InLogger.log(this, "Lifecycle: onPostResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        InLogger.log(this, "Lifecycle: onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        InLogger.log(this, "Lifecycle: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InLogger.log(this, "Lifecycle: onDestroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        InLogger.log(this, "Lifecycle: onLowMemory");
    }

    @Override
    protected void onPause() {
        super.onPause();
        InLogger.log(this, "Lifecycle: onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        InLogger.log(this, "Lifecycle: onResume");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        InLogger.log(this, String.format(Locale.US, "ActivityResult received, requestCode = %d, resultCode = %d", requestCode, resultCode));
        InLogger.log(this,"Data", data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        InLogger.log(this, "Starting Activity");
        InLogger.log(this,"Intent", intent);
        InLogger.log(this,"Options", options);
        super.startActivity(intent, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        InLogger.log(this, String.format(Locale.US, "Starting Activity for result (request code: %d)", requestCode));
        InLogger.log(this,"Intent", intent);
        InLogger.log(this,"Options", options);
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public ComponentName startService(Intent service) {
        InLogger.log("this, Starting service");
        InLogger.log(this,"Intent", service);
        ComponentName componentName = super.startService(service);
        InLogger.log(this, String.format("Service component name: %s", componentName != null ? componentName.getClass().getSimpleName() : "null"));
        return componentName;
    }

    @Override
    public ComponentName startForegroundService(Intent service) {
        InLogger.log(this, "Starting foreground service");
        InLogger.log(this,"Intent", service);
        ComponentName componentName = super.startForegroundService(service);
        InLogger.log(this, String.format("Service component name: %s", componentName != null ? componentName.getClass().getSimpleName() : "null"));
        return componentName;
    }

    @Override
    public int checkSelfPermission(String permission) {
        int result = super.checkSelfPermission(permission);
        InLogger.log(this, String.format("Checking permission: %s, result: %s", permission, result == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
        return result;
    }

    public void requestPermission(String[] permissions, int requestCode){
        if (Build.VERSION.SDK_INT >= 23) {
            InLogger.log(this, String.format(Locale.US, "Requesting permissions (request code = %d): %s", requestCode, TextUtils.join(", ", permissions)));
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
        InLogger.log(this, stringBuilder.toString());
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
