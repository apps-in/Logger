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
        Logger.log(this, "Lifecycle: onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Logger.log(this, "Lifecycle: onDetach");
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Logger.log(this, "Lifecycle: onCreate");
        Logger.log(this,"savedInstanceState", savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Logger.log(this, "Lifecycle: onCancel");
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Logger.log(this, "Lifecycle: onDismiss");
        super.onDismiss(dialog);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Logger.log(this, "Lifecycle: onActivityCreated");
        Logger.log(this,"savedInstanceState", savedInstanceState);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Logger.log(this, "Lifecycle: onStart");
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Logger.log(this, "Lifecycle: onSaveInstanceState");
        Logger.log(this,"outState", outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        Logger.log(this, "Lifecycle: onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Logger.log(this, "Lifecycle: onDestroyView");
        super.onDestroyView();
    }
}
