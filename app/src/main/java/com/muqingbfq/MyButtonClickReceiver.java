package com.muqingbfq;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqingbfq.mq.FloatingLyricsService;
import com.muqingbfq.mq.wj;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyButtonClickReceiver extends BroadcastReceiver {
    private final Timer timer = new Timer();
    private static int clickCount;

    public MyButtonClickReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
            int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0);
            //蓝牙断开
            if (bluetoothState == BluetoothAdapter.STATE_DISCONNECTED) {
                receiverPause();
            }
            return;
        }
        if (action.equals("android.intent.action.HEADSET_PLUG")) {
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 2) == 0) {
                    //拔出
                    if (bfqkz.mt.isPlaying()) {
                        receiverPause();
                    }
                } else if (intent.getIntExtra("state", 2) == 1) {
                    receiverPlay();
                    //插入
                }
            }
            return;
        }
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                clickCount = clickCount + 1;
                if (clickCount == 1) {
                    HeadsetTimerTask headsetTimerTask = new HeadsetTimerTask();
                    timer.schedule(headsetTimerTask, 500);
                }
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                handler(2);
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                handler(3);
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PAUSE && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                handler(4);
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                handler(5);
            }
            return;
        }

        switch (action) {
            case "kg":
                playOrPause();
                break;
            case "syq":
                bfq_an.syq();
                break;
            case "xyq":
                bfq_an.xyq();
                break;
            case "lrc":
                if (FloatingLyricsService.lei == null) {
                    if (!Settings.canDrawOverlays(main.application)) {
                        // 无权限，需要申请权限
                        main.application.startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + main.application.getPackageName())));
                    } else {
                        main.application.startService(
                                new Intent(main.application, FloatingLyricsService.class));
                    }
                    return;
                }
                FloatingLyricsService lei = FloatingLyricsService.lei;
                if (lei.setup.i == 1) {
                    lei.setyc();
                } else {
                    lei.setup.i = 0;
                    lei.baocun();
                    main.application.stopService(
                            new Intent(main.application, FloatingLyricsService.class));
                }
                break;
            case "like":
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<MP3>>() {
                    }.getType();
                    List<MP3> list = gson.fromJson(wj.dqwb(wj.gd + "mp3_like.json"), type);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    if (bfqkz.like_bool) {
                        list.remove(bfqkz.xm);
                        bfqkz.like_bool = false;
                    } else {
                        if (!list.contains(bfqkz.xm)) {
                            list.add(bfqkz.xm);
                            bfqkz.like_bool = true;
                        }
                    }
                    wj.xrwb(wj.gd + "mp3_like.json", gson.toJson(list));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        // 处理按钮点击事件的逻辑
    }

    class HeadsetTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (clickCount == 1) {
                    handler(1);
                } else if (clickCount == 2) {
                    handler(2);
                } else if (clickCount >= 3) {
                    handler(3);
                }
                clickCount = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void handler(int a) {
        switch (a) {
            case 1:
                playOrPause();
                break;
            case 2:
                playNext();
                break;
            case 3:
                playPrevious();
                break;
            case 4:
                receiverPause();
                break;
            case 5:
                receiverPlay();
                break;
            default:
                break;
        }
    }


    //     * 对蓝牙 播放
    public void receiverPlay() {
        bfqkz.mt.start();
    }

    //     * 对蓝牙 暂停
    public void receiverPause() {
        bfqkz.mt.pause();
    }

    /**
     * 对蓝牙  播放-暂停
     */
    public static void playOrPause() {
        if (bfqkz.mt == null) {
            return;
        }
//        gj.sc(isMusicServiceBound);播放/暂停按钮点击事件 if (isMusicServiceBound)
        if (bfqkz.mt.isPlaying()) {
            bfqkz.mt.pause();
        } else {
            bfqkz.mt.start();
        }
        bfqkz.notify.tzl();
    }

    /**
     * 对蓝牙  下一首
     */
    public void playNext() {
        bfq_an.xyq();
    }

    /**
     * 对蓝牙  上一首
     */
    public void playPrevious() {
        bfq_an.syq();
    }
}
