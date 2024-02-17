package apps.in.android_logger;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public abstract class LogFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        InLogger.log(this, "Lifecycle: onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        InLogger.log(this, "Lifecycle: onCreate");
        InLogger.log(this,"savedInstanceState", savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        InLogger.log(this, "Lifecycle: onViewCreated");
        InLogger.log(this,"savedInstanceState", savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        InLogger.log(this, "Lifecycle: onActivityCreated");
        InLogger.log(this,"savedInstanceState", savedInstanceState);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        InLogger.log(this, "Lifecycle: onViewStateRestored");
        InLogger.log(this,"savedInstanceState", savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        InLogger.log(this, "Lifecycle: onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        InLogger.log(this, "Lifecycle: onResume");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        InLogger.log(this, "Lifecycle: onSaveInstanceState");
        InLogger.log(this,"outState", outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        InLogger.log(this, "Lifecycle: onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        InLogger.log(this, "Lifecycle: onStop");
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        InLogger.log(this, "Lifecycle: onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        InLogger.log(this, "Lifecycle: onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        InLogger.log(this, "Lifecycle: onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        InLogger.log(this, "Lifecycle: onDetach");
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        InLogger.log(this, String.format(Locale.US, "ActivityResult received, requestCode = %d, resultCode = %d", requestCode, resultCode));
        InLogger.log(this,"Data", data);
        super.onActivityResult(requestCode, resultCode, data);
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
}
