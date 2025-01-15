package com.muqingbfq.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.common.base.Strings;
import com.muqingbfq.MP3;
import com.muqingbfq.PlaybackService;
import com.muqingbfq.R;
import com.muqingbfq.api.url;
import com.muqingbfq.databinding.ListMp3ImageBinding;
import com.muqingbfq.main;
import com.muqingbfq.mq.VH;
import com.muqingbfq.mq.gj;

import java.util.ArrayList;
import java.util.List;

public class AdapterMp3 extends RecyclerView.Adapter<VH<ListMp3ImageBinding>> {
    public List<MP3> list = new ArrayList<>();

    public AdapterMp3() {

    }

    public AdapterMp3(List<MP3> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VH<ListMp3ImageBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH<>(ListMp3ImageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull VH<ListMp3ImageBinding> holder, int position) {
        MP3 x = list.get(position);
        holder.binding.wb1.setText(x.name);
        holder.binding.zz.setText(x.zz);
        int themeColor = gj.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurface);
        holder.binding.getRoot().setCardBackgroundColor(themeColor);
        if (PlaybackService.mediaSession != null) {
            Player player = PlaybackService.mediaSession.getPlayer();
            MediaItem currentMediaItem = player.getCurrentMediaItem();
            if (currentMediaItem != null) {
                String mediaId = currentMediaItem.mediaId;
                if (mediaId.equals(x.id)) {
//                    holder.binding.getRoot()
                    holder.binding.getRoot().setCardBackgroundColor(
                            gj.getThemeColor(holder.itemView.getContext(), com.google.android.material.R.attr.colorSurfaceVariant));
                }
            }
        }

        if (Strings.isNullOrEmpty(x.picurl)) {
            holder.binding.text1.setText(String.valueOf(position + 1));
            holder.binding.imageView.setVisibility(ViewGroup.GONE);
            holder.binding.linsum.setVisibility(View.VISIBLE);
        }else{
            Glide.with(holder.itemView.getContext()).load(list.get(position).picurl)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_launcher_foreground))
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.imageView);
        }

        holder.itemView.setOnClickListener(view -> {
            if (PlaybackService.mediaSession == null) {
                return;
            }
            Player player = PlaybackService.mediaSession.getPlayer();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    MP3 hq = url.hq(x);
//                    gj.sc(String.format("链接:%s，图片：%s", hq.url,hq.picurl));
                    main.handler.post(() -> {
                        for (int i = 0; i < player.getMediaItemCount(); i++) {
                            MediaItem currentItem = player.getMediaItemAt(i);
                            if (currentItem.mediaId.equals(hq.id)) {
                                gj.sc("存在播放:" + currentItem.mediaId + "==" + hq.id + " i=" + i);
                                player.seekTo(i,0);
                                player.prepare();
                                player.play();
                                notifyDataSetChanged();
                                return;
                            }
                        }
                        gj.sc("不存在添加播放");
                        PlaybackService.list.add(hq);
                        PlaybackService.ListSave();
                        MediaItem mediaItem = PlaybackService.GetMp3(hq);
                        player.addMediaItem(0,mediaItem);
                        player.seekTo(0,0);
                        player.prepare();
                        player.play();
                        notifyDataSetChanged();
                    });
                }
            }.start();

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
