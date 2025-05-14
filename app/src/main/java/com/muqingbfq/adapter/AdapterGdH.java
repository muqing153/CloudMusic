package com.muqingbfq.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.muqing.BaseAdapter;
import com.muqingbfq.XM;
import com.muqingbfq.databinding.ListGdBBinding;
import com.muqingbfq.fragment.mp3;

import java.util.List;

public class AdapterGdH extends BaseAdapter<ListGdBBinding, XM> {

    public AdapterGdH(Context context, List<XM> dataList) {
        super(context, dataList);
    }

    public AdapterGdH(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(XM xm, ListGdBBinding viewBinding, ViewHolder<ListGdBBinding> viewHolder, int position) {

        viewBinding.text1.setText(xm.name);
        viewBinding.text2.setText(xm.message);
//        gj.sc(xm.picurl);
        Glide.with(context)
                .asBitmap()
                .load(xm.picurl)
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Bitmap> target, boolean isFirstResource) {
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Bitmap resource,
                                                   @NonNull Object model, Target<Bitmap> target,
                                                   @NonNull DataSource dataSource, boolean isFirstResource) {
                        Palette.from(resource).generate(palette -> {
                            Palette.Swatch lightVibrantSwatch = null;
                            if (palette != null) {
                                lightVibrantSwatch = palette.getVibrantSwatch();
                            }
                            if (lightVibrantSwatch != null) {
                                int color = lightVibrantSwatch.getRgb();
                                viewBinding.text1.setTextColor(color);
                                viewBinding.getRoot().setRippleColor(ColorStateList.valueOf(color));
                            }
                        });
                        viewBinding.image.setImageBitmap(resource);
                        return true;
                    }
                })
                .into(viewBinding.image);

        viewBinding.getRoot().setOnClickListener(v -> {
            mp3.drawable = viewBinding.image.getDrawable();
            mp3.start(v.getContext(), new String[]{xm.id, xm.name});
        });
        viewBinding.getRoot().setOnLongClickListener(v -> {
            AdapterGd.setOnLongClickListener(v.getContext(), xm);
            return false;
        });
    }

    @Override
    protected ListGdBBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ListGdBBinding.inflate(inflater, parent, false);
    }
}
