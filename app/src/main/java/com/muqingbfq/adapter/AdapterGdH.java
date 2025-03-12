package com.muqingbfq.adapter;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.muqing.gj;
import com.muqingbfq.XM;
import com.muqingbfq.databinding.ListGdBBinding;
import com.muqingbfq.fragment.mp3;
import com.muqingbfq.mq.VH;

import java.util.ArrayList;
import java.util.List;

public class AdapterGdH extends RecyclerView.Adapter<VH<ListGdBBinding>> {

    public List<XM> list = new ArrayList<>();

    public AdapterGdH() {}
    public AdapterGdH(List<XM> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public VH<ListGdBBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH<>(ListGdBBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH<ListGdBBinding> holder, int position) {
        XM xm = list.get(position);
        holder.binding.text1.setText(xm.name);
        holder.binding.text2.setText(xm.message);
//        gj.sc(xm.picurl);
        Glide.with(holder.itemView.getContext())
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
                            Palette.Swatch lightVibrantSwatch = palette.getVibrantSwatch();
                            if (lightVibrantSwatch != null) {
                                int color = lightVibrantSwatch.getRgb();
                                holder.binding.text1.setTextColor(color);
                                holder.binding.getRoot().setRippleColor(ColorStateList.valueOf(color));
                            }
                        });
                        holder.binding.image.setImageBitmap(resource);
                        return true;
                    }
                })
                .into(holder.binding.image);
        holder.binding.getRoot().setOnClickListener(v -> {
            mp3.drawable = holder.binding.image.getDrawable();
            mp3.start(v.getContext(), new String[]{xm.id, xm.name});
        });
        holder.binding.getRoot().setOnLongClickListener(v -> {
            AdapterGd.setOnLongClickListener(v.getContext(), xm);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
