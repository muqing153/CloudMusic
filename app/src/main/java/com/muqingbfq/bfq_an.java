package com.muqingbfq;

import androidx.media3.common.MediaItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.muqingbfq.mq.MediaItemAdapter;
import com.muqingbfq.mq.FilePath;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class bfq_an {
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);

    public static String getTime(long time) {
        return simpleDateFormat.format(new Date(time));
    }

    public static boolean islike(String id) {
        String dqwb = FilePath.dqwb(FilePath.gd + "mp3_like.json");
        if (dqwb != null) {
            try {
                Gson gson = new GsonBuilder().registerTypeAdapter(MediaItem.class, new MediaItemAdapter())
                        .create();
                List<MediaItem> o = gson.fromJson(dqwb, MediaItemAdapter.type);
                if (o != null) {
                    return o.stream().anyMatch(mediaItem -> mediaItem.mediaId.equals(id));
                }
            } catch (Exception e) {
                FilePath.sc(FilePath.gd + "mp3_like.json");
            }
        }
        return false;
    }

    public static boolean AddLike(MediaItem xm) {
        String dqwb = FilePath.dqwb(FilePath.gd + "mp3_like.json");
        if (dqwb == null) {
            dqwb = "[]";
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(MediaItem.class, new MediaItemAdapter())
                .create();
        List<MediaItem> o = gson.fromJson(dqwb, MediaItemAdapter.type);
        boolean add = o.add(xm);
        FilePath.xrwb(FilePath.gd + "mp3_like.json", gson.toJson(o));
        return add;
    }

    public static boolean DelLike(MediaItem xm) {
        String dqwb = FilePath.dqwb(FilePath.gd + "mp3_like.json");
        if (dqwb == null) {
            dqwb = "[]";
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(MediaItem.class, new MediaItemAdapter())
                .create();
        List<MediaItem> o = gson.fromJson(dqwb, MediaItemAdapter.type);
        boolean b = o.removeIf(mediaItem -> mediaItem.mediaId.equals(xm.mediaId));
        FilePath.xrwb(FilePath.gd + "mp3_like.json", gson.toJson(o));
        return b;
    }
}
