package com.soultabcaregiver.Model;

import android.app.Dialog;
import android.widget.TextView;

public class DiloagBoxCommon {
    private Dialog dialog;
    private TextView textView;
    private TextView textViewNew;
    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public void setTextViewNew(TextView textViewNew){
        this.textViewNew = textViewNew;
    }

    public TextView getTextViewNew() {
        return textViewNew;
    }

}
