package apps.in.loggerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import apps.in.android_logger.LogActivity;
import apps.in.android_logger.Logger;
import apps.in.loggerapp.databinding.ActivityMainBinding;


public class MainActivity extends LogActivity {

    static {
        System.loadLibrary("native_lib");
    }

    private static native int throwException();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonZipLogs.setOnClickListener(v -> binding.textViewZipPath.setText(Logger.getLogZip()));
        final Activity activity = this;
        binding.buttonShareLogs.setOnClickListener(v -> Logger.shareLog(activity, "Send logs", true));
        binding.buttonCrash.setOnClickListener(v -> {
            int temp = 1 / 0;
        });
        binding.buttonNativeCrash.setOnClickListener(v -> {
            int temp = throwException();
            int n = 0;
        });
        binding.buttonCheckCrash.setOnClickListener(v -> {
            boolean result = Logger.hasUncheckedCrashes();
            binding.textCheckCrash.setText(String.valueOf(result));
        });
        binding.buttonStartActivity.setOnClickListener(v -> {
            startActivity(new Intent(this, SecondActivity.class));
        });
    }
}
