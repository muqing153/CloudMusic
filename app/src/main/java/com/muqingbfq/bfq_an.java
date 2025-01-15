package com.muqingbfq;

import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqingbfq.api.url;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class bfq_an {
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);
    public static String getTime(long time) {
        return simpleDateFormat.format(new Date(time));
    }

    public static boolean islike() {
        boolean contains = false;
        String dqwb = wj.dqwb(wj.gd + "mp3_like.json");
        if (dqwb != null) {
            try {
                Type type = new TypeToken<List<MP3>>() {
                }.getType();
                List<MP3> o = new Gson().fromJson(dqwb, type);
                if (o != null) {
                    contains = o.contains(bfqkz.xm);
                }
            } catch (Exception e) {
                wj.sc(wj.gd + "mp3_like.json");
            }
        }
        return bfqkz.like_bool = contains;
    }

    public static boolean getlike(MP3 xm) {
        boolean contains = false;
        String dqwb = wj.dqwb(wj.gd + "mp3_like.json");
        if (dqwb != null) {
            try {
                Type type = new TypeToken<List<MP3>>() {
                }.getType();
                List<MP3> o = new Gson().fromJson(dqwb, type);
                if (o != null) {
                    contains = o.contains(xm);
                }
            } catch (Exception e) {
                wj.sc(wj.gd + "mp3_like.json");
            }
        }
        return contains;
    }
}
