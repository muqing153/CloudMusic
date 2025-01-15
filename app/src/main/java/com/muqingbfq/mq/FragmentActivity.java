package com.muqingbfq.mq;

import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewbinding.ViewBinding;

import com.muqingbfq.R;

public abstract class FragmentActivity<ViewBindingType extends ViewBinding>
        extends AppCompatActivity<ViewBindingType> {
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
    public void setContentView() {
        EdgeToEdge.enable(this);
        super.setContentView(getViewBinding().getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
        setToolbar();

    }

    public void onBack() {

        //    getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//        @Override
//        public void handleOnBackPressed() {
//            if (binding.searchview.isShowing()) {
//                binding.searchview.hide();
//                return;
//            }
//            moveTaskToBack(true);
//        }
//    });
    }
}
