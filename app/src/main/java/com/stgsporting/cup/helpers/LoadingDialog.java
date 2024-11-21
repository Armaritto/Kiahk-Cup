package com.stgsporting.cup.helpers;

//import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
//import android.os.CountDownTimer;
import android.view.Window;
//import android.widget.Toast;

import androidx.annotation.NonNull;

import com.stgsporting.cup.R;

public class LoadingDialog extends Dialog {
//    private static final long TIMEOUT_DURATION = 100;
//    private CountDownTimer timeoutTimer;
    public LoadingDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);
        show();
//        startTimeoutTimer(context);
    }

    @Override
    public void dismiss() {
//        if (timeoutTimer != null) {
//            timeoutTimer.cancel();
//        }
        if (isShowing()) super.dismiss();
    }

    @Override
    public void show() {
        if (!isShowing()) super.show();
    }
//
//    private void startTimeoutTimer(Context context) {
//        timeoutTimer = new CountDownTimer(TIMEOUT_DURATION, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                // Optional: Update UI or keep track of remaining time if necessary
//            }
//
//            @Override
//            public void onFinish() {
//                // Timeout reached - dismiss the dialog and show an error message
//                if (isShowing()) {
//                    dismiss();
//                    Toast.makeText(context, "Failed to fetch data. Please try again.", Toast.LENGTH_LONG).show();
//                }
//                onTimeout();
//            }
//        }.start();
//    }
//
//    protected void onTimeout() {
//        if (getContext() instanceof Activity) {
//            Activity activity = (Activity) getContext();
//            if (!activity.isFinishing()) {
//                activity.finish();
//            }
//        }
//    }
}
