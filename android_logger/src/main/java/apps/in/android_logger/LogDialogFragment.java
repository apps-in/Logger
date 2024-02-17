package apps.in.android_logger;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public abstract class LogDialogFragment extends DialogFragment {

    @Override
    public void onAttach(Context context) {
        InLogger.log(this, "Lifecycle: onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        InLogger.log(this, "Lifecycle: onDetach");
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        InLogger.log(this, "Lifecycle: onCreate");
        InLogger.log(this,"savedInstanceState", savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        InLogger.log(this, "Lifecycle: onCancel");
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        InLogger.log(this, "Lifecycle: onDismiss");
        super.onDismiss(dialog);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        InLogger.log(this, "Lifecycle: onActivityCreated");
        InLogger.log(this,"savedInstanceState", savedInstanceState);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        InLogger.log(this, "Lifecycle: onStart");
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        InLogger.log(this, "Lifecycle: onSaveInstanceState");
        InLogger.log(this,"outState", outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        InLogger.log(this, "Lifecycle: onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        InLogger.log(this, "Lifecycle: onDestroyView");
        super.onDestroyView();
    }
}
