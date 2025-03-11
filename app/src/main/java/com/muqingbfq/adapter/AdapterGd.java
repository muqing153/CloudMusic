package com.muqingbfq.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.muqingbfq.MP3;
import com.muqingbfq.PlaybackService;
import com.muqingbfq.R;
import com.muqingbfq.XM;
import com.muqingbfq.api.playlist;
import com.muqingbfq.api.resource;
import com.muqingbfq.databinding.ListGdBinding;
import com.muqingbfq.fragment.mp3;
import com.muqingbfq.fragment.wode;
import com.muqingbfq.main;
import com.muqingbfq.mq.VH;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AdapterGd extends RecyclerView.Adapter<VH<ListGdBinding>> {

    public List<XM> list = new ArrayList<>();
    @NonNull
    @Override
    public VH<ListGdBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH<>(ListGdBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull VH<ListGdBinding> holder, int position) {
        XM xm = list.get(position);
        holder.itemView.setOnClickListener(v -> {
            mp3.drawable = holder.binding.image.getDrawable();
            mp3.start(v.getContext(), new String[]{xm.id, xm.name});
        });
        Glide.with(holder.itemView.getContext())
                .asBitmap()
                .load(xm.picurl)
                .placeholder(R.drawable.mdimusicbox)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
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
                            int color = palette.getLightMutedColor(Color.WHITE);
                            GradientDrawable gradientDrawable = new GradientDrawable(
                                    GradientDrawable.Orientation.BOTTOM_TOP,
                                    new int[]{color, color});
                            gradientDrawable.setAlpha(128);
                            holder.binding.text1.setBackground(gradientDrawable);
                            holder.binding.getRoot().setRippleColor(ColorStateList.valueOf(color));
                        });
                        holder.binding.image.setImageBitmap(resource);
                        return true;
                    }
                })
                .into(holder.binding.image);
        holder.binding.text1.setText(xm.name);
        holder.binding.kg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        super.run();
                        List<MP3> an = playlist.hq(xm.id);
                        if (bfqkz.ms == 2) {
//                            Collections.shuffle(bfqkz.list);
                        }

                        main.handler.post(() -> {
                            if (PlaybackService.mediaSession == null) {
                                return;
                            }
                            Player player = PlaybackService.mediaSession.getPlayer();
                            player.clearMediaItems();
                            for (MP3 mp3 : an) {
//                                gj.sc(mp3.url);
                                MediaItem mediaItem = PlaybackService.GetMp3(mp3);
                                player.addMediaItem(mediaItem);
                            }
                            player.prepare();
                            player.seekTo(0, 0);
                            player.play();
                            //保存播放列表
                            PlaybackService.list.clear();
                            PlaybackService.list.addAll(an);
                            PlaybackService.ListSave();
                        });
                    }
                }.start();
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            setOnLongClickListener(v.getContext(), xm);
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public static void setOnLongClickListener(Context context, XM xm) {
        List<String> ls = new ArrayList<>();
        ls.add("下载歌单");
        if (wj.cz(wj.gd + xm.id)) {
            ls.add("删除歌单");
        }
        new MaterialAlertDialogBuilder(context)
                .setTitle("歌单选项")
                .setItems(ls.toArray(new String[0]), (dialog, which) -> {
                    if (which == 0) {
                        new baocun(xm.id);
                    }else if (which == 1) {
                        Gson gson = new Gson();
                        String dqwb = wj.dqwb(wj.gd_xz);
                        List<XM> xms = new ArrayList<>();
                        if (dqwb != null) {
                            xms = gson.fromJson(dqwb,
                                    new TypeToken<List<XM>>() {
                                    }.getType());
                        }
                        try {
                            xms.removeIf(xm1 -> xm1.id.equals(xm.id));
                            wj.xrwb(wj.gd_xz, gson.toJson(xms));
                            wode.load.run();
                        } catch (Exception e) {
                            gj.sc(e);
                        }
                    }
                })
                .show();

    }
    public static class baocun extends Thread {
        String Id;

        public baocun(String id) {
            this.Id = id;
            start();
        }
        @Override
        public void run() {
            super.run();
            String gethq = playlist.gethq(Id);
            if (gethq != null) {
                Gson gson = new Gson();
                String dqwb = wj.dqwb(wj.gd_xz);
                List<XM> xms = new ArrayList<>();
                if (dqwb != null) {
                    xms = gson.fromJson(dqwb,
                            new TypeToken<List<XM>>() {
                            }.getType());
                }
                try {
                    xms.removeIf(xm1 -> xm1.id.equals(Id));
                    XM playlistContent = resource.Playlist_content(Id);
                    xms.add(0, playlistContent);
                    wj.xrwb(wj.gd_xz, gson.toJson(xms));
                    wj.xrwb(wj.gd + Id, gethq);
                    wode.load.run();
                    Yes();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        public void Yes() {

        }
    }
}
