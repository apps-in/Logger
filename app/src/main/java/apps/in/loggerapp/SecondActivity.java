package apps.in.loggerapp;

import android.os.Bundle;

import apps.in.android_logger.LogActivity;
import apps.in.loggerapp.databinding.ActivitySecondBinding;

public class SecondActivity extends LogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySecondBinding binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

}
