package com.muqingbfq.mq;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.jaeger.library.StatusBarUtil;

public abstract class AppCompatActivity<ViewBindingType extends ViewBinding> extends androidx.appcompat.app.AppCompatActivity {

    protected abstract ViewBindingType getViewBindingObject(LayoutInflater layoutInflater);

    protected ViewBindingType getViewBinding() {
        binding = getViewBindingObject(getLayoutInflater());
        return binding;
    }

    public ViewBindingType binding;
    public void setContentView() {
        super.setContentView(getViewBinding().getRoot());
    }
}
