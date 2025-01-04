package com.muqingbfq.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.muqingbfq.R;
import com.muqingbfq.mq.gj;

public class TabLayout extends LinearLayoutCompat {
    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs, defStyleAttr);
    }

    private void Init(Context context, AttributeSet attrs, int defStyleAttr) {
//        setOrientation(HORIZONTAL);
        setBackgroundColor(gj.getThemeColor(context, com.google.android.material.R.attr.colorSurface));
        setGravity(Gravity.CENTER);
        int a = gj.dp2px(context, 9);
        if (gj.isTablet(context)) {
            setPadding(a, 0, a, 0);
        } else {
            setPadding(0, a, 0, a);
        }
//        if (gj.isTablet(context)) {
//            setOrientation(VERTICAL);
//        } else {
//            setOrientation(HORIZONTAL);
//        }
//        addView(new Button(context));
    }

    public int sizeView = 0;

    @SuppressLint("ResourceType")
    public View addView(String title, int icon) {
        Context context = getContext();
        Item itemB = new Item(context);

        itemB.imageView.setImageResource(icon);
        itemB.textView.setText(title);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int a = gj.dp2px(context, 26);
        if (!gj.isTablet(context)) {
            layoutParams.setMargins(a, 0, a, 0);
        } else {
            layoutParams.setMargins(0, a, 0, a);
        }
        addView(itemB, layoutParams);
        MaterialCardView childAt = (MaterialCardView) itemB.getChildAt(0);
        if (sizeView == 0) {
            childAt.setCardBackgroundColor(gj.getThemeColor(context, com.google.android.material.R.attr.colorPrimaryContainer));
        } else {

        }
        sizeView++;

        return childAt;
    }

    public void setSelected(int a) {

    }

    public static class Item extends FrameLayout {
        public Item(Context context) {
            this(context, null);
        }

        public Item(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        ImageView imageView;
        TextView textView;

        public Item(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);

            inflate(context, R.layout.view_tab, this);
            imageView = findViewById(R.id.image);
            textView = findViewById(R.id.text);


//            LayoutInflater.from(context).inflate(R.layout.view_tab, this, true);

        }
    }
}
