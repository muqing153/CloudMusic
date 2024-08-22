package com.muqingbfq.mq;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.muqingbfq.MyButtonClickReceiver;
import com.muqingbfq.R;
import com.muqingbfq.bfqkz;
import com.muqingbfq.yc;

public class NotificationManagerCompat {
    bfqkz context;
    public NotificationCompat.Builder notificationBuilder;
    public androidx.core.app.NotificationManagerCompat notificationManager;
    private String name, zz;

    public NotificationManagerCompat(bfqkz context) {
        this.context = context;
        name = context.getString(R.string.app_name);
        zz = context.getString(R.string.zz);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                        NotificationManager.IMPORTANCE_LOW);
                channel.setDescription(zz);
                NotificationManager systemService = context.getSystemService(NotificationManager.class);
                systemService.createNotificationChannel(channel);
                if (!systemService.areNotificationsEnabled()) {
                    return;
                }
            }
            // 适配12.0及以上

            // 设置启动的程序，如果存在则找出，否则新的启动
            Intent my = new Intent(context, MyButtonClickReceiver.class);
            pendingIntent_kg = getBroadcast(context, my.
                    setAction("kg"));
            pendingIntent_syq = getBroadcast(context, my.
                    setAction("syq"));
            pendingIntent_xyq = getBroadcast(context, my.
                    setAction("xyq"));
            pendingIntent_lrc = getBroadcast(context, my.
                    setAction("lrc"));
            pendingIntent_like = getBroadcast(context, my.
                    setAction("like"));
            style = new androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1, 2, 3)
                    .setMediaSession(context.mSession.getSessionToken());
            notificationManager = androidx.core.app.NotificationManagerCompat.from(context);
            notificationBuilder = getNotificationBuilder(context)
                    .setSmallIcon(R.drawable.foreground)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true).setColorized(true).setShowWhen(false)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(context.pendingIntent)
                    .setStyle(style);
            tzl();
        } catch (Exception e) {
            yc.start(context, e);
        }
    }

    androidx.media.app.NotificationCompat.MediaStyle style;

    @SuppressLint("RestrictedApi")
    public void tzl_button() {
        if (notificationBuilder == null) {
            return;
        }
        notificationBuilder.mActions.clear();
        notificationBuilder
                .addAction(R.drawable.like, "like", pendingIntent_like) // #0
                .addAction(R.drawable.syq, "syq", pendingIntent_syq) // #0
                .addAction(bfqkz.mt.isPlaying() ? R.drawable.bf : R.drawable.zt
                        , "kg", pendingIntent_kg)  // #1
                .addAction(R.drawable.xyq, "xyq", pendingIntent_xyq)
                .addAction(R.drawable.lock, "lrc", pendingIntent_lrc)
                .setOngoing(bfqkz.mt.isPlaying());
        notificationManager_notify();
    }

    public void setbitmap(Bitmap bitmap) {
        notificationBuilder
                .setLargeIcon(bitmap);

        context.builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, name)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, zz)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, zz)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 100);

        context.mSession.setMetadata(context.builder.build());
        notificationManager_notify();
    }

    @SuppressLint("RestrictedApi")
    public void tzl() {
        if (notificationBuilder == null) {
            return;
        }
        if (bfqkz.xm != null) {
            name = bfqkz.xm.name;
            zz = bfqkz.xm.zz;
        }
        notificationBuilder.mActions.clear();
        notificationBuilder
                .addAction(R.drawable.like, "like", pendingIntent_like) // #0
                .addAction(R.drawable.syq, "syq", pendingIntent_syq) // #0
                .addAction(bfqkz.mt.isPlaying() ? R.drawable.bf : R.drawable.zt
                        , "kg", pendingIntent_kg)  // #1
                .addAction(R.drawable.xyq, "xyq", pendingIntent_xyq)
                .addAction(R.drawable.lock, "lrc", pendingIntent_lrc)
                .setContentTitle(name)
                .setContentText(zz)
                .setOngoing(bfqkz.mt.isPlaying());


        context.mSession.setMetadata(context.builder.build());
        notificationManager_notify();
    }

    private PendingIntent pendingIntent_kg,
            pendingIntent_syq,
            pendingIntent_xyq,
            pendingIntent_lrc,
            pendingIntent_like;

    private final String CHANNEL_ID = "MediaSessionCompat";

    public void notificationManager_notify() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(0, notificationBuilder.build());
    }

    private NotificationCompat.Builder getNotificationBuilder(Context context) {
        // 适用于Android 8.0及以上版本
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        return new NotificationCompat.Builder(context, CHANNEL_ID);
    }

    private PendingIntent getBroadcast(Context context, Intent intent) {
        int flag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flag = PendingIntent.FLAG_IMMUTABLE;
        } else {
            flag = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        return PendingIntent.getBroadcast(context, 0, intent, flag);
    }


    public static PendingIntent getActivity(Context context, Intent intent) {
        int flag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flag = PendingIntent.FLAG_IMMUTABLE;
        } else {
            flag = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        return PendingIntent.getActivity(context, 0, intent, flag);
    }

}
