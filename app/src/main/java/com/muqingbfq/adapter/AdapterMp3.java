package com.muqingbfq.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.muqingbfq.MP3;
import com.muqingbfq.R;
import com.muqingbfq.api.url;
import com.muqingbfq.bfqkz;
import com.muqingbfq.databinding.ListMp3ImageBinding;
import com.muqingbfq.mq.VH;
import com.muqingbfq.mq.gj;

import java.util.ArrayList;
import java.util.List;

public class AdapterMp3 extends RecyclerView.Adapter<VH<ListMp3ImageBinding>> {
    public List<MP3> list = new ArrayList<>();

    @NonNull
    @Override
    public VH<ListMp3ImageBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH<>(ListMp3ImageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    int position_wei;
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull VH<ListMp3ImageBinding> holder, int position) {
        MP3 x = list.get(position);
        holder.binding.wb1.setText(x.name);
        holder.binding.zz.setText(x.zz);
        if (bfqkz.xm != null && x.id.equals(bfqkz.xm.id)) {
            holder.binding.getRoot().setCardBackgroundColor(
                    gj.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceVariant));
        } else {
            holder.binding.getRoot().setCardBackgroundColor(ContextCompat
                    .getColor(holder.itemView.getContext(), android.R.color.transparent));
        }
        holder.itemView.setOnClickListener(view -> {
            if (bfqkz.xm == null || !bfqkz.xm.id.equals(x.id)) {
                bfqkz.xm = x;
                new url(x);
                notifyDataSetChanged();

            } else if (!bfqkz.mt.isPlaying()) {
                bfqkz.mt.start();
            }
            if (!bfqkz.list.contains(x)) {
                bfqkz.list.add(0, x);
            }
//                    bfqkz.list.addAll(list);
//                    bfq.start(getContext());
        });
        Glide.with(holder.itemView.getContext()).load(x.picurl)
                .apply(new RequestOptions().placeholder(R.drawable.ic_launcher_foreground))
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.binding.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
