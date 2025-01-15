package com.muqingbfq.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

public class Text extends androidx.appcompat.widget.AppCompatTextView {
    public Text(@NonNull Context context) {
        super(context);
        initView();
    }


    public Text(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public Text(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        this.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        this.setSingleLine(true);
        this.setMarqueeRepeatLimit(-1);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
