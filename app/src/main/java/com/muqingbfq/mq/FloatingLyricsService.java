package com.muqingbfq.mq;

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
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.dirror.lyricviewx.LyricEntry;
import com.dirror.lyricviewx.LyricViewX;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqingbfq.R;
import com.muqingbfq.bfqkz;
import com.muqingbfq.databinding.FloatLrcviewBinding;
import com.muqingbfq.main;

import java.io.File;
import java.lang.reflect.Type;

public class FloatingLyricsService extends Service implements View.OnClickListener, View.OnTouchListener {
    private WindowManager windowManager;
    FloatLrcviewBinding binding;

    public Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            int index = 0;
            for (int i = 0; i < LyricViewX.lyricEntryList.size(); i++) {
                LyricEntry lineLrc = LyricViewX.lyricEntryList.get(i);
                if (lineLrc.time <= bfqkz.mt.getCurrentPosition()) {
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
                }else{
                    binding.lrcViewMessage.setText("");
                }
            }
            handler.postDelayed(this, 1000); // 每秒更新一次进度
        }
    };
    @SuppressLint("StaticFieldLeak")
    public static FloatingLyricsService lei;

    public static boolean get() {
        File file = new File(wj.filesdri + "FloatingLyricsService.json");
        if (file.exists() && file.isFile()) {
            String dqwb = wj.dqwb(file.toString());
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

    public SETUP setup = new SETUP();

    public int lock() {
        if (setup != null && setup.i == 2) {
            return WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        return WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lei = this;
        File file = new File(wj.filesdri + "FloatingLyricsService.json");
        try {
            if (file.exists() && file.isFile()) {
                String dqwb = wj.dqwb(file.toString());
                Gson gson = new Gson();
                Type type = new TypeToken<SETUP>() {
                }.getType();
                setup = gson.fromJson(dqwb, type);
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
            binding.lrcViewMessage.setTextSize(v-1.0f);
            binding.lrcViewMessage.setTextColor(Color.parseColor(setup.Color));
            binding.lock.setOnClickListener(this);
//        params.gravity = Gravity.CENTER;


            // 获取 WindowManager 并将悬浮窗歌词添加到窗口中
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.addView(binding.getRoot(), params);
            if (setup.i == 2) {
                setyc();

            } else {
                show();
            }
            /*
            if (setup.i == 0)
                setup.i = 1;
                baocun();*/
            handler.post(updateSeekBar); // 在播放开始时启动更新进度
        } catch (Exception e) {
            wj.sc(file.toString());
            gj.sc(getClass() + ":" + e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在 Service 销毁时移除悬浮窗歌词
        if (windowManager != null && binding != null) {
            windowManager.removeView(binding.getRoot());
            handler.removeCallbacks(updateSeekBar); // 在播放开始时启动更新进度
        }
        lei = null;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void baocun() {
        wj.xrwb(new File(wj.filesdri + "FloatingLyricsService.json").toString(),
                new Gson().toJson(setup));
    }

    public static void baocun(SETUP setup) {
        if (setup == null) {
            return;
        }

        wj.xrwb(new File(wj.filesdri + "FloatingLyricsService.json").toString(),
                new Gson().toJson(setup));
    }

    private int initialY;
    private float initialTouchY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录触摸事件的初始位置和坐标
                initialY = params.y;
                initialTouchY = motionEvent.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                // 计算触摸事件的偏移量，将悬浮窗口的位置设置为初始位置加上偏移量
                int offsetY = (int) (motionEvent.getRawY() - initialTouchY);
                setup.Y = initialY + offsetY;
                params.y = setup.Y;
                windowManager.updateViewLayout(binding.getRoot(), params);
                return true;
            case MotionEvent.ACTION_UP:
                baocun();
                break;

        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.kg) {
            ImageView kg = (ImageView) view;
            if (bfqkz.mt == null) {
                return;
            }
            if (bfqkz.mt.isPlaying()) {
                bfqkz.mt.pause();
                kg.setImageResource(R.drawable.zt);
            } else {
                bfqkz.mt.start();
                kg.setImageResource(R.drawable.bf);
            }
        } else if (id == R.id.lock) {
            setyc();
        }
    }

    public void setyc() {
        setup.i = 2;
        params.flags = lock();
        binding.lock.setVisibility(View.GONE);
        windowManager.updateViewLayout(binding.getRoot(), params);
        baocun();
    }

    @SuppressLint("CheckResult")
    public void show() {
        setup.i = 1;
        params.flags = lock();
        binding.lock.setVisibility(View.VISIBLE);
        windowManager.updateViewLayout(binding.getRoot(), params);
        baocun();
    }
}
