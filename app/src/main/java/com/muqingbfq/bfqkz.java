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
    public final static MediaPlayer mt = new MediaPlayer();

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
    public static com.muqingbfq.mq.NotificationManagerCompat notify;

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

    public static void mp3(MP3 mp3) {
        try {
            if (mp3 == null) {
                return;
            }
            if (TextUtils.isEmpty(mp3.url)) {
                //针对错误进行相应的处理
                bfqkz.list.remove(bfqkz.xm);
                bfq_an.xyq();
                return;
            }
            if (TextUtils.isEmpty(mp3.picurl)) {
                mp3.picurl = url.picurl(mp3.id);
            }
            mt.setDataSource(mp3);
        } catch (Exception e) {
            gj.sc("bfqkz mp3(" + mp3 + ") :" + e);
        }
    }

    public static class mp3 extends Thread {
        MP3 mp3;

        public mp3(MP3 mp3) {
            this.mp3 = mp3;
            start();
        }

        @Override
        public void run() {
            super.run();
            mp3(mp3);
        }
    }


    public MediaSessionCompat mSession;
    public PlaybackStateCompat.Builder playback;

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

    class callback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            super.onPlay();
            if (playback.build().getState() == PlaybackStateCompat.STATE_PAUSED) {
                mt.start();
                playback.setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                        .build();
                mSession.setPlaybackState(playback.build());
            }
            gj.sc(this.getClass());

        }

        @Override
        public void onPause() {
            super.onPause();
            if (playback.build().getState() == PlaybackStateCompat.STATE_PLAYING) {
                mt.pause();
                playback.setState(PlaybackStateCompat.STATE_PAUSED, 0, 1.0f)
                        .build();
                mSession.setPlaybackState(playback.build());
            }
        }

        @SuppressLint("SwitchIntDef")
        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            try {
                switch (playback.build().getState()) {
                    case PlaybackStateCompat.STATE_PLAYING:
                    case PlaybackStateCompat.STATE_PAUSED:
                    case PlaybackStateCompat.STATE_NONE:
//                        mp3(uri);/
                        playback.setState(PlaybackStateCompat.STATE_CONNECTING, 0, 1.0f)
                                .build();
                        mSession.setPlaybackState(playback.build());
                        //我们可以保存当前播放音乐的信息，以便客户端刷新UI
                        mSession.setMetadata(new MediaMetadataCompat.Builder()
                                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, extras.getString("title"))
                                .build()
                        );
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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