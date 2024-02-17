package apps.in.loggerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import apps.in.android_logger.LogActivity;
import apps.in.android_logger.InLogger;
import apps.in.loggerapp.databinding.ActivityMainBinding;


public class MainActivity extends LogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonZipLogs.setOnClickListener(v -> binding.textViewZipPath.setText(InLogger.getLogZip()));
        final Activity activity = this;
        binding.buttonShareLogs.setOnClickListener(v -> InLogger.shareLog(activity, "Send logs", true));
        binding.buttonCrash.setOnClickListener(v -> {
            InLogger.log(this, "Throwing exception");
            int temp = 1 / 0;
        });
        binding.buttonCheckCrash.setOnClickListener(v -> {
            boolean result = InLogger.hasUncheckedCrashes();
            binding.textCheckCrash.setText(String.valueOf(result));
        });
        binding.buttonStartActivity.setOnClickListener(v -> {
            startActivity(new Intent(this, SecondActivity.class));
        });
    }
}
