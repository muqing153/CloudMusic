package com.muqingbfq.mq;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;

import com.muqingbfq.R;

public abstract class FragmentActivity<ViewBindingType extends ViewBinding>
        extends androidx.appcompat.app.AppCompatActivity {

    protected abstract ViewBindingType getViewBindingObject(LayoutInflater layoutInflater);

    protected ViewBindingType getViewBinding() {
        binding = getViewBindingObject(getLayoutInflater());
        return binding;
    }

    public ViewBindingType binding;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void setToolbar() {
        View viewById = findViewById(R.id.toolbar);
        if (viewById != null) {
            setSupportActionBar((Toolbar) viewById);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public void setContentView(int view) {
        super.setContentView(view);
    }
    public void setContentView() {
        binding = getViewBindingObject(getLayoutInflater());
        setContentView(binding.getRoot());
        setToolbar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }
}
