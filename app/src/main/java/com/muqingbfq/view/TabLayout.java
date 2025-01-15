package com.muqingbfq.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
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
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.muqingbfq.R;
import com.muqingbfq.mq.gj;

public class TabLayout extends MaterialCardView {
    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs,defStyleAttr);
        Init(context, attrs, defStyleAttr);
    }

    public LinearLayout linearLayout;
    private void Init(Context context, AttributeSet attrs, int defStyleAttr) {
        linearLayout = new LinearLayout(context, attrs, defStyleAttr);
        int a = gj.dp2px(context, 9);
        LinearLayout.LayoutParams layoutParams ;
        if (gj.isTablet(context)) {
//            这是平板模式
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            setContentPadding(a, 0, a, 0);
        } else {
//            这是手机模式
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER);
            setContentPadding(0, a, 0, a);
        }
//        linearLayout.setBackgroundColor(Color.BLACK);
        addView(linearLayout,layoutParams);
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
        linearLayout.addView(itemB, layoutParams);
        if (sizeView == 0) {
            itemB.setCardBackgroundColor(gj.getThemeColor(context, com.google.android.material.R.attr.colorPrimaryContainer));
        }
        sizeView++;

        return itemB;
    }

    public static class Item extends MaterialCardView {
        ImageView imageView;
        TextView textView;
        public Item(Context context) {
            this(context, null);
        }

        public Item(Context context, @Nullable AttributeSet attrs) {
            super(new ContextThemeWrapper(context, com.google.android.material.R.style.Widget_Material3_CardView_Elevated), attrs);
            setUseCompatPadding(true);
            setFocusable(true);
            setClickable(true);
//            没有边缘线
            setStrokeWidth(0);
            int padding = gj.dp2px(context, 6);
            setContentPadding(padding,padding,padding,padding);
            inflate(context, R.layout.view_tab, this);
            imageView = findViewById(R.id.image);
            textView = findViewById(R.id.text);
        }

    }
}
