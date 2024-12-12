package com.stgsporting.cup.helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stgsporting.cup.R;

public class AlertDialog extends Dialog {

    public AlertDialog(@NonNull Context context, String text) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.update_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(true);
        Button update = findViewById(R.id.update);
        TextView title = findViewById(R.id.title);
        title.setText(text);
        title.setTextSize(16);
        update.setVisibility(View.GONE);
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
