package com.muqingbfq.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.muqingbfq.R;

public class CardImage extends MaterialCardView {
    public ImageView imageView;

    public CardImage(Context context) {
        super(context);
        start();
    }
    public CardImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        start();
    }

    public CardImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        start();
    }

    public void start() {
        imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(imageView);
        imageView.setImageResource(R.drawable.ic_launcher_foreground);
    }


    public void setImage(Object bitmap) {
        try {
            Glide.with(this)
                    .load(bitmap)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setImageapply(Object bitmap) {
        Glide.with(getContext())
                .load(bitmap)
                .apply(new RequestOptions().placeholder(R.drawable.ic_launcher_foreground))
//                .error(R.drawable.app_warning)
                .into(imageView);
    }
}
