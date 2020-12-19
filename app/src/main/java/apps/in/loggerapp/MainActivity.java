package apps.in.loggerapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import apps.in.android_logger.LogActivity;
import apps.in.android_logger.Logger;


public class MainActivity extends LogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.buttonZipLogs).setOnClickListener(v -> ((TextView) findViewById(R.id.textViewZipPath)).setText(Logger.getLogZip()));
        final Activity activity = this;
        findViewById(R.id.buttonShareLogs).setOnClickListener(v -> Logger.shareLog(activity, "Send logs", true));
        findViewById(R.id.buttonCrash).setOnClickListener(v -> {
            int temp = 1 / 0;
        });
    }
}
