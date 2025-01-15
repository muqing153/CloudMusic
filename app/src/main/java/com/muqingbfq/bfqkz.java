package com.muqingbfq;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.muqingbfq.api.url;
import com.muqingbfq.mq.gj;

import java.util.ArrayList;
import java.util.List;

public class bfqkz extends MediaBrowserServiceCompat {

    public static List<MP3> list = new ArrayList<>();
    //保存原始list顺序
    public static List<MP3> list_baocun = new ArrayList<>();
    public static List<MP3> lishi_list = new ArrayList<>();
    public static int ms;
    //    0 循环 1 顺序 2 随机
    public static MP3 xm;
    public static boolean like_bool;
    public static String lrc;
    @SuppressLint("StaticFieldLeak")
    public static int getmti() {
        if (xm == null) {
            return 0;
        }
        int i = bfqkz.list.indexOf(xm) + 1;
        if (i >= bfqkz.list.size()) {
            i = 0;
        }
        return i;
    }
    public PendingIntent pendingIntent;

    public MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();

    @Override
    public void onCreate() {
//        super.onCreate();
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setComponent(new ComponentName(this, home.class));//用ComponentName得到class对象
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式，两种情况
//        pendingIntent = com.muqingbfq.mq.NotificationManagerCompat.getActivity(this, intent);
//        com.muqingbfq.api.playlist.hq_hc(bfqkz.lishi_list);
//        new BluetoothMusicController(this);
//        playback = new PlaybackStateCompat.Builder();
//        playback.setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
//                .build();
//        mSession.setCallback(new callback());
//        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
//
//        playback.setActions(PlaybackStateCompat.ACTION_PLAY);
//        playback.setActions(PlaybackStateCompat.ACTION_STOP);
//
//        mSession.setPlaybackState(playback.build());
//        setSessionToken(mSession.getSessionToken());
//        mSession.setActive(true);
//        notify = new com.muqingbfq.mq.NotificationManagerCompat(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }
}