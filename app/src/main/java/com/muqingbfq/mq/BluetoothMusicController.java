package com.muqingbfq.mq;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.v4.media.session.MediaSessionCompat;

import com.muqingbfq.MyButtonClickReceiver;

public class BluetoothMusicController {
    private Context context;
    private MediaSessionCompat mediaSession;

    public BluetoothMusicController(Context context) {
        this.context = context;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        context.registerReceiver(new MyButtonClickReceiver(), intentFilter);
        registerHeadsetReceiver();
    }

    public void stop() {
        unregisterHeadsetReceiver();
    }

    // 注册媒体按钮事件接收器
    @SuppressLint("ObsoleteSdkInt")
    public void registerHeadsetReceiver() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ComponentName name = new ComponentName(context.getPackageName(), MyButtonClickReceiver.class.getName());
        audioManager.registerMediaButtonEventReceiver(name);
    }

    // 注销媒体按钮事件接收器
    public void unregisterHeadsetReceiver() {
        if (mediaSession != null) {
            mediaSession.setActive(false);
            mediaSession.release();
            mediaSession = null;
        }
    }
}
