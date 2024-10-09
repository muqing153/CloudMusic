package com.muqingbfq;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.muqingbfq.fragment.bflb_db;
import com.muqingbfq.fragment.mp3;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;

import java.io.IOException;

public class MediaPlayer extends android.media.MediaPlayer {
    // 每秒更新一次进度
    @SuppressLint("UnsafeOptInUsageError")
    public MediaPlayer() {
        setOnErrorListener((mediaPlayer, i, i1) -> {
            if (bfqkz.list.isEmpty()) {
                return false;
            }
            //针对错误进行相应的处理
            bfqkz.list.remove(bfqkz.xm);
            bfqkz.xm = bfqkz.list.get(bfqkz.getmti());
            new bfqkz.mp3(com.muqingbfq.api.
                    url.hq(bfqkz.xm));
            return false;
        });
        setOnCompletionListener(mediaPlayer -> {
            if (bfqkz.list.isEmpty()) {
                return;
            }
            bfq_an.xyq();
        });
        setAudioAttributes(new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
//        main.handler.post(updateSeekBar); // 在播放开始时启动更新进度
    }

    @Override
    public void pause() throws IllegalStateException {
        if (isPlaying()) {
            super.pause();
//            bfq.isPlaying = false;
        }
    }

    @Override
    public void start() throws IllegalStateException {
        if (bfqkz.xm == null) {
            if (bfqkz.list != null && !bfqkz.list.isEmpty()) {
                bfq_an.xyq();
            }
            return;
        }
        super.start();

//        bfq.isPlaying = true;
    }


    public void setDataSource(MP3 mp3) throws IOException {
        reset();
        super.setDataSource(mp3.url);
        prepare();
        start();
        bfqkz.xm = mp3;

        new Thread() {
            @Override
            public void run() {
                super.run();
                if (bfqkz.lishi_list.size() >= 100) {
                    bfqkz.lishi_list.remove(0);
                }
                bfqkz.lishi_list.remove(bfqkz.xm);
                if (!bfqkz.lishi_list.contains(bfqkz.xm)) {
                    bfqkz.lishi_list.add(0, bfqkz.xm);
                    wj.xrwb(wj.gd + "mp3_hc.json", new com.google.gson.Gson().toJson(bfqkz.lishi_list));
                }
                wj.setMP3ToFile(bfqkz.xm);
            }
        }.start();
    }

    public void DataSource(MP3 path) throws Exception {
        reset();
        super.setDataSource(path.url);
        prepare();
        setTX();
    }

    public void setTX() {
        Glide.with(main.application)
                .asBitmap()
                .load(bfqkz.xm.picurl)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                @NonNull Target<Bitmap> target,
                                                boolean isFirstResource) {
                        Bitmap bitmap = null;
                        try {
                            Mp3File mp3file = new Mp3File(bfqkz.xm.picurl);
                            if (mp3file.hasId3v2Tag()) {
                                ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                                byte[] albumImage = id3v2Tag.getAlbumImage();
                                bitmap =
                                        BitmapFactory.decodeByteArray(albumImage, 0, albumImage.length);

                            }
                        } catch (Exception a) {
                            gj.sc(getClass() + " yc:" + a);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Bitmap bitmap, @NonNull Object model, Target<Bitmap> target,
                                                   @NonNull DataSource dataSource,
                                                   boolean isFirstResource) {
                        return false;
                    }
                })
                .submit();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void bfui() {
        setTX();
        if (bflb_db.adapter != null) {
//            bflb_db.adapter.
            bflb_db.adapter.notifyDataSetChanged();
        }
    }
}