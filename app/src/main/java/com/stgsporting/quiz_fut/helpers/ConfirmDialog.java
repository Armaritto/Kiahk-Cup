package com.stgsporting.quiz_fut.helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.quiz_fut_draft.R;

public class ConfirmDialog extends Dialog {

    public ConfirmDialog(@NonNull Context context, View.OnClickListener yesListener, View.OnClickListener noListener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(true);
        Button yesButton = findViewById(R.id.yes);
        Button noButton = findViewById(R.id.no);

        yesButton.setOnClickListener(yesListener);
        noButton.setOnClickListener(noListener);
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
