package com.muqingbfq.mq;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.dirror.lyricviewx.LyricEntry;
import com.dirror.lyricviewx.LyricViewX;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqing.gj;
import com.muqingbfq.PlaybackService;
import com.muqingbfq.databinding.FloatLrcviewBinding;

import java.io.File;
import java.lang.reflect.Type;

public class FloatingLyricsService extends Service {
    private WindowManager windowManager;
    FloatLrcviewBinding binding;

    public Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (PlaybackService.mediaSession == null) {
                handler.postDelayed(this, 1000); // 每秒更新一次进度
                return;
            }
            if (PlaybackService.mediaSession.getPlayer().isPlaying()) {

                if (LyricViewX.lyricEntryList.isEmpty()) {
                    binding.lrcView.setText("暂无歌词");
                    binding.lrcViewMessage.setText("");
                    handler.postDelayed(this, 1000);
                    return;
                }
                int index = 0;
                for (int i = 0; i < LyricViewX.lyricEntryList.size(); i++) {
                    LyricEntry lineLrc = LyricViewX.lyricEntryList.get(i);
                    if (lineLrc.time <= PlaybackService.mediaSession.getPlayer().getCurrentPosition()) {
                        index = i;
                    } else {
                        break;
                    }
                }
                if (index < LyricViewX.lyricEntryList.size()) {
                    LyricEntry currentLrc = LyricViewX.lyricEntryList.get(index);
                    binding.lrcView.setText(currentLrc.text);
                    if (currentLrc.secondText != null) {
                        binding.lrcViewMessage.setText(currentLrc.secondText);
                    } else {
                        binding.lrcViewMessage.setText("");
                    }
                }
            } else {
                binding.lrcView.setText("");
            }
            handler.postDelayed(this, 1000); // 每秒更新一次进度
        }
    };
    public static SETUP setup = new SETUP();

    public static boolean get() {
        File file = new File(FilePath.filesdri + "FloatingLyricsService.json");
        if (file.exists() && file.isFile()) {
            String dqwb = FilePath.dqwb(file.toString());
            Gson gson = new Gson();
            Type type = new TypeToken<SETUP>() {
            }.getType();
            SETUP setup = gson.fromJson(dqwb, type);
            return setup.i != 0;
        } else {
            return false;
        }
    }

    Handler handler = new Handler();
    WindowManager.LayoutParams params;

    public static class SETUP {
        //0是关闭 1是打开 2是锁定
        public int i = 1, size = 20;
        public float Alpha = 0.9f;
        public String Color = "#0088FF";
        public int Y = 0;
    }


    public int lock() {
        if (setup != null && setup.i == 2) {
            return WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        return WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }

    private int initialY;
    private float initialTouchY;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        try {
            File file = new File(FilePath.filesdri + "FloatingLyricsService.json");
            if (file.exists() && file.isFile()) {
                String dqwb = FilePath.dqwb(file.toString());
                Gson gson = new Gson();
                Type type = new TypeToken<SETUP>() {
                }.getType();
                setup = gson.fromJson(dqwb, type);
            } else {
                setup = new SETUP();
            }
            if (setup != null && setup.i == 0) {
                //在Service中关闭自己
                stopSelf();
                return;
            }
            // 创建悬浮窗歌词的 View
//        FloatLrcviewBinding
            binding = FloatLrcviewBinding.inflate(LayoutInflater.from(this));
//            binding.getRoot().setOnTouchListener(this);
//        int i = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;FLAG_NOT_TOUCH_MODAL
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                            WindowManager.LayoutParams.TYPE_PHONE,
                    lock(),
                    PixelFormat.TRANSLUCENT
            );

            params.y = setup.Y;
            binding.getRoot().setAlpha(setup.Alpha);
            float v = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                    setup.size,
                    getResources().getDisplayMetrics());
            binding.lrcView.setTextSize(v);
            binding.lrcView.setTextColor(Color.parseColor(setup.Color));
            binding.lrcViewMessage.setTextSize(v - 1.0f);
            binding.lrcViewMessage.setTextColor(Color.parseColor(setup.Color));
            binding.lock.setColorFilter(Color.BLACK);
            binding.lock.setOnClickListener(v1 -> setyc());
//        params.gravity = Gravity.CENTER;
            // 获取 WindowManager 并将悬浮窗歌词添加到窗口中
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.addView(binding.getRoot(), params);
            if (setup.i == 2) {
                setyc();
            } else {
                show();
            }
            handler.post(updateSeekBar); // 在播放开始时启动更新进度

        } catch (Exception e) {
            FilePath.sc(FilePath.filesdri + "FloatingLyricsService.json");
            gj.sc(getClass() + ":" + e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Service.stopSelf();
        // 在 Service 销毁时移除悬浮窗歌词
        if (windowManager != null && binding != null) {
            windowManager.removeView(binding.getRoot());
            handler.removeCallbacks(updateSeekBar); // 在播放开始时启动更新进度
        }
        isRunning = false;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void baocun() {
        FilePath.xrwb(new File(FilePath.filesdri + "FloatingLyricsService.json").toString(),
                new Gson().toJson(setup));
    }

    public static boolean isRunning = false;


    @SuppressLint("ClickableViewAccessibility")
    public void setyc() {
        setup.i = 2;
        params.flags = lock();
        binding.lock.setVisibility(View.GONE);
        binding.getRoot().setCardBackgroundColor(Color.parseColor("#00FFFFFF"));
        binding.getRoot().setOnTouchListener(null);
        params.type = Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//将alpha设置为最大遮挡不透明度
        params.alpha = 0.8f;
        windowManager.updateViewLayout(binding.getRoot(), params);
        baocun();
    }

    @SuppressLint({"CheckResult", "ClickableViewAccessibility"})
    public void show() {
        setup.i = 1;
        params.flags = lock();
        binding.lock.setVisibility(View.VISIBLE);
        binding.getRoot().setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        binding.getRoot().setOnTouchListener((v12, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 获取初始Y位置和初始触摸Y坐标
                    initialY = params.y;
                    initialTouchY = event.getRawY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    // 手指在Y轴移动时，计算新的Y位置
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    // 更新悬浮窗的Y轴位置
                    setup.Y = params.y;
                    windowManager.updateViewLayout(binding.getRoot(), params);
                    return true;
            }
            return true;
        });
        windowManager.updateViewLayout(binding.getRoot(), params);
        baocun();
    }
}
