package com.stgsporting.quiz_fut.helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import androidx.annotation.NonNull;

import com.stgsporting.quiz_fut.R;

public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);
        show();
    }

    @Override
    public void dismiss() {
        if (isShowing()) super.dismiss();
    }

    @Override
    public void show() {
        if (!isShowing()) super.show();
    }
}
