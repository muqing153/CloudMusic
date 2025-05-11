package com.muqingbfq;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.muqing.gj;
import com.muqingbfq.mq.FloatingLyricsService;
import com.muqingbfq.mq.FilePath;
import java.io.File;

public class main extends Application {
    public static Application application;
    public static Handler handler = new Handler(Looper.getMainLooper());
    public static String api;
//    https://ncm.nekogan.com
    //    public static String http = "https://www.muqingkaifazhe.top/muqingbfq.php"; 过时的更新检测
    public static SharedPreferences sp;
    public static SharedPreferences.Editor edit;
    @Override
    public void onCreate() {
        super.onCreate();
        if (FilePath.filesdri == null) {
            new FilePath(this);
        }

        SharedPreferences nickname = getSharedPreferences("Set_up", Context.MODE_PRIVATE);
        main.api = nickname.getString("IP", "");
        application = this;
        sp = getSharedPreferences("Set_up", MODE_PRIVATE);
        edit = sp.edit();
        SharedPreferences theme = getSharedPreferences("theme", MODE_PRIVATE);
        SharedPreferences.Editor edit = theme.edit();
        int i = theme.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (i == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            edit.putInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            edit.apply();
        }
        AppCompatDelegate.setDefaultNightMode(i);
        // 注册 ProcessLifecycleOwner 以监听应用生命周期事件
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onStart(@Nullable LifecycleOwner owner) {
                // 应用进入前台
                gj.sc("onStart");
                if (FloatingLyricsService.isRunning) {
                    stopService(new Intent(main.this, FloatingLyricsService.class));
                }
            }

            @Override
            public void onStop(@Nullable LifecycleOwner owner) {
                // 应用进入后台
                gj.sc("onStop");
                if (Settings.canDrawOverlays(main.this) && !FloatingLyricsService.isRunning) {
                    startService(new Intent(main.this, FloatingLyricsService.class));
                }
            }
        });
    }
}
