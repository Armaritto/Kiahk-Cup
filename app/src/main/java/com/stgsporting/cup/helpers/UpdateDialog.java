package com.stgsporting.cup.helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.stgsporting.cup.R;

public class UpdateDialog extends Dialog {

    public UpdateDialog(@NonNull Context context, View.OnClickListener updateListener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.update_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);
        Button update = findViewById(R.id.update);
        update.setOnClickListener(updateListener);
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
