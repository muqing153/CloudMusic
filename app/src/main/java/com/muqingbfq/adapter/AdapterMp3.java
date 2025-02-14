package com.muqingbfq.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class AdapterMp3 extends RecyclerView.Adapter<VH<ListMp3ImageBinding>> implements Filterable {
    public List<MP3> list = new ArrayList<>();
    private List<MP3> list_ys;
    private Activity activity;

    public AdapterMp3() {

    }

    public AdapterMp3(Activity activity) {
        this.activity = activity;
    }

    public AdapterMp3(List<MP3> list) {
        this.list = list;
        list_ys = list;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    list = list_ys;
                } else {
                    List<MP3> filteredList = new ArrayList<>();
                    for (int i = 0; i < list_ys.size(); i++) {
                        MP3 mp3 = list_ys.get(i);
                        if (mp3.name.contains(charString)
                                || mp3.zz.contains(charString)) {
                            filteredList.add(list_ys.get(i));
                        }
                    }
                    list = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                list = (List<MP3>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public VH<ListMp3ImageBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH<>(ListMp3ImageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    // 定义全局监听器
    private final Player.Listener playerListener = new Player.Listener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            if (mediaItem != null) {
                notifyDataSetChanged();
                Log.d("RecyclerView", "切换到新音乐: " + mediaItem.mediaId);
            }
        }
    };

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (activity == null && PlaybackService.mediaSession != null) {

            PlaybackService.mediaSession.getPlayer().addListener(playerListener);
        }
        Log.d("RecyclerView", "Adapter 绑定到 RecyclerView");
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (activity == null && PlaybackService.mediaSession != null) {
            PlaybackService.mediaSession.getPlayer().removeListener(playerListener);
        }
        Log.d("RecyclerView", "Adapter 从 RecyclerView 解绑");
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
        } else {
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
                                player.seekTo(i, 0);
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
                        player.addMediaItem(0, mediaItem);
                        player.seekTo(0, 0);
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
