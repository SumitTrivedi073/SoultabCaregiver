package com.soultabcaregiver.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.soultabcaregiver.R;

public class CustomProgressDialog extends ProgressDialog {
    public  final String DEBUG_TAG = getClass().getSimpleName();
    String mMessage;
    private TextView progressMessage;
    public CustomProgressDialog(Context context, String message) {
        super(context, R.style.CustomAlertDialogStyle);
        //	this.mMessage = message;
        // TODO Auto-generated constructor stub
    }

    public CustomProgressDialog(Context context) {
        super(context, R.style.CustomAlertDialogStyle);
        //    this.mMessage = context.getResources().getString(R.stringp-.Loading);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);

        setCancelable(false);

        progressMessage = findViewById(R.id.progressMessage);
        //	progressMessage.setText(mMessage);
    }
}


