package com.muqingbfq.mq;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewbinding.ViewBinding;

import com.muqingbfq.R;

public abstract class AppCompatActivity<ViewBindingType extends ViewBinding> extends androidx.appcompat.app.AppCompatActivity {

    protected abstract ViewBindingType getViewBindingObject(LayoutInflater layoutInflater);

    protected ViewBindingType getViewBinding() {
        binding = getViewBindingObject(getLayoutInflater());
        return binding;
    }

    public ViewBindingType binding;

    public void setContentView() {
        EdgeToEdge.enable(this);
        super.setContentView(getViewBinding().getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
