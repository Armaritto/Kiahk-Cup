package com.stgsporting.cup.helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import androidx.annotation.NonNull;

import com.stgsporting.cup.R;

public class LoadingDialog extends Dialog {

    private final Context context;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);
        show();
    }

    @Override
    public void dismiss() {
        if (isShowing() && context!=null) super.dismiss();
    }

    @Override
    public void show() {
        if (!isShowing() && context!= null) super.show();
    }
}
