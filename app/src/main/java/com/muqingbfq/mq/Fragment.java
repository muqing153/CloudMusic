package com.muqingbfq.mq;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

public abstract class Fragment<Binding extends ViewBinding> extends androidx.fragment.app.Fragment {

    protected abstract Binding inflateViewBinding(LayoutInflater inflater,ViewGroup container);

    public Binding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = inflateViewBinding(inflater, container);
        setUI(inflater, container, savedInstanceState);
        return binding.getRoot();
    }

    public abstract void setUI(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);
}
