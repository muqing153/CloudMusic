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
import com.muqing.BaseAdapter;
import com.muqing.gj;
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
import com.muqingbfq.mq.FilePath;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AdapterGd extends BaseAdapter<ListGdBinding, XM> {
    public AdapterGd(Context context) {
        super(context);
    }

    @Override
    protected ListGdBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ListGdBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void onBindView(XM data, ListGdBinding viewBinding, ViewHolder<ListGdBinding> viewHolder, int position) {
        XM xm = dataList.get(position);
        viewBinding.getRoot().setOnClickListener(v -> {
            mp3.drawable = viewBinding.image.getDrawable();
            mp3.start(v.getContext(), new String[]{xm.id, xm.name});
        });
//        gj.sc(xm.picurl);
        Glide.with(context)
                .asBitmap()
                .load(xm.picurl)
                .placeholder(R.drawable.mdimusicbox)
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
                            assert palette != null;
                            int color = palette.getLightMutedColor(Color.WHITE);
                            GradientDrawable gradientDrawable = new GradientDrawable(
                                    GradientDrawable.Orientation.BOTTOM_TOP,
                                    new int[]{color, color});
                            gradientDrawable.setAlpha(128);
                            viewBinding.text1.setBackground(gradientDrawable);
                            viewBinding.getRoot().setRippleColor(ColorStateList.valueOf(color));
                        });
                        viewBinding.image.setImageBitmap(resource);
                        return true;
                    }
                })
                .into(viewBinding.image);

        viewBinding.text1.setText(xm.name);
        viewBinding.kg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        super.run();
                        List<MP3> an = playlist.hq(xm.id);
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
        viewBinding.getRoot().setOnLongClickListener(v -> {
            setOnLongClickListener(v.getContext(), xm);
            return false;
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public static void setOnLongClickListener(Context context, XM xm) {
        List<String> ls = new ArrayList<>();
        ls.add("下载歌单");
        if (FilePath.cz(FilePath.gd + xm.id)) {
            ls.add("删除歌单");
        }
        new MaterialAlertDialogBuilder(context)
                .setTitle("歌单选项")
                .setItems(ls.toArray(new String[0]), (dialog, which) -> {
                    if (which == 0) {
                        new baocun(xm.id);
                    }else if (which == 1) {
                        Gson gson = new Gson();
                        String dqwb = FilePath.dqwb(FilePath.gd_xz);
                        List<XM> xms = new ArrayList<>();
                        if (dqwb != null) {
                            xms = gson.fromJson(dqwb,
                                    new TypeToken<List<XM>>() {
                                    }.getType());
                        }
                        try {
                            xms.removeIf(xm1 -> xm1.id.equals(xm.id));
                            FilePath.sc(FilePath.gd + xm.id);
                            FilePath.xrwb(FilePath.gd_xz, gson.toJson(xms));
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
                String dqwb = FilePath.dqwb(FilePath.gd_xz);
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
                    FilePath.xrwb(FilePath.gd_xz, gson.toJson(xms));
                    FilePath.xrwb(FilePath.gd + Id, gethq);
                    if (wode.load != null) {
                        wode.load.run();
                    }
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
