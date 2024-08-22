package com.muqingbfq.mq;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqingbfq.R;
import com.muqingbfq.main;

public class EditViewDialog {

    public EditText editText;
    public Button buttona, buttonb;
    public MaterialAlertDialogBuilder AlertDialogBuilder;
    private AlertDialog alertDialog;

    public EditViewDialog(@NonNull Context context, String str) {
        AlertDialogBuilder = new MaterialAlertDialogBuilder(context);
        setTitle(str);
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(context).inflate(R.layout.view_edit, null);
        editText = inflate.findViewById(R.id.editview);
        buttona = inflate.findViewById(R.id.button1);
        buttonb = inflate.findViewById(R.id.button2);
        AlertDialogBuilder.setView(inflate);
        buttona.setOnClickListener(view -> dismiss());
        buttonb.setOnClickListener(view -> dismiss());
    }
    public EditViewDialog setTitle(String str) {
        AlertDialogBuilder.setTitle(str);
        return this;
    }

    public EditViewDialog setMessage(String str) {
        AlertDialogBuilder.setMessage(str);
        return this;
    }

    public AlertDialog show() {
        alertDialog = AlertDialogBuilder.show();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    public String getEditText() {
        return editText.getText().toString();
    }

    public EditViewDialog setEditinputType(String str) {
        int inputType;
        switch (str) {
            case "textCapCharacters":
                inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
                break;
            case "textCapWords":
                inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS;
                break;
            case "textCapSentences":
                inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
                break;
            case "textAutoCorrect":
                inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
                break;
            case "textAutoComplete":
                inputType = InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE;
                break;
            case "textMultiLine":
                inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE;
                break;
            case "textImeMultiLine":
                inputType = InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE;
                break;
            case "textNoSuggestions":
                inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
                break;
            case "textUri":
                inputType = InputType.TYPE_TEXT_VARIATION_URI;
                break;
            case "textEmailAddress":
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                break;
            case "textEmailSubject":
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT;
                break;
            case "textShortMessage":
                inputType = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE;
                break;
            case "textLongMessage":
                inputType = InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE;
                break;
            case "textPersonName":
                inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME;
                break;
            case "textPostalAddress":
                inputType = InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS;
                break;
            case "textPassword":
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                break;
            case "textVisiblePassword":
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                break;
            case "textWebEditText":
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT;
                break;
            case "textFilter":
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_FILTER;
                break;
            case "textPhonetic":
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PHONETIC;
                break;
            case "number":
                inputType = InputType.TYPE_CLASS_NUMBER;
                break;
            case "numberSigned":
                inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED;
                break;
            case "numberDecimal":
                inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
                break;
            case "phone":
                inputType = InputType.TYPE_CLASS_PHONE;
                break;
            case "datetime":
                inputType = InputType.TYPE_CLASS_DATETIME;
                break;
            case "date":
                inputType = InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE;
                break;
            case "time":
                inputType = InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME;
                break;
            case "none":
            case "text":
            default:
                inputType = InputType.TYPE_CLASS_TEXT;
                break;
        }
        editText.setInputType(inputType);
        return this;
    }

    public void dismiss() {
        main.handler.post(() -> alertDialog.dismiss());
    }

    public void setNegative(View.OnClickListener a) {
        buttona.setOnClickListener(a);
    }

    public EditViewDialog setPositive(View.OnClickListener a) {
        buttonb.setOnClickListener(a);
        return this;
    }

    public interface OnClickListener extends View.OnClickListener {
        @Override
        void onClick(View view);
    }
}
