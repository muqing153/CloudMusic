package com.muqingbfq.Dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.muqingbfq.databinding.DialogEdittextBinding;

public class DialogEditText extends MaterialAlertDialogBuilder {
    public final DialogEdittextBinding binding;

    public AlertDialog alertDialog;
    public DialogEditText(Context context, String[] strings) {
        super(context);
        binding = DialogEdittextBinding.inflate(LayoutInflater.from(context));
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        for (String s : strings) {
            stringArrayAdapter.add(s);
        }
        binding.edittext.setAdapter(stringArrayAdapter);
        setView(binding.getRoot());
    }

    public void setPositiveButton(@Nullable Button.OnClickListener listener) {
        binding.Yes.setOnClickListener(listener);
    }

    public void setNegativeButton(@Nullable Button.OnClickListener listener) {
        binding.No.setOnClickListener(listener);
    }

    @Override
    public AlertDialog show() {
        alertDialog = super.show();
        return alertDialog;
    }
}
