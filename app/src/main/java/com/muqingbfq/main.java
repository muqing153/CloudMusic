package com.muqingbfq;

import android.annotation.SuppressLint;
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

import com.google.gson.reflect.TypeToken;
import com.muqingbfq.login.visitor;
import com.muqingbfq.mq.FloatingLyricsService;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;
import com.muqingbfq.mq.wl;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
public class main extends Application {
    public static Application application;
    public static Handler handler = new Handler(Looper.getMainLooper());
    public static String api = "https://api.csm.sayqz.com";
    //    public static String http = "https://www.muqingkaifazhe.top/muqingbfq.php"; 过时的更新检测
    public static SharedPreferences sp;
    public static SharedPreferences.Editor edit;
    @Override
    public void onCreate() {
        super.onCreate();
        if (wj.filesdri == null) {
            new wj(this);
        }
        File file = new File(wj.filesdri + "API.mq");
        if (file.exists() && file.isFile()) {
            String dqwb = wj.dqwb(file.toString());
            if (!TextUtils.isEmpty(dqwb) && dqwb.startsWith("http")) {
                api = dqwb;
            } else {
                file.delete();
            }
        } else {
            wj.xrwb(file.toString(), main.api);
        }
        application = this;
        sp = getSharedPreferences("Set_up", MODE_PRIVATE);
        edit = sp.edit();
        boolean bj = false;
        try {
            com.muqingbfq.bfqkz.ms = sp.getInt("ms", 1);
        } catch (Exception e) {
            edit.putInt("ms", 1);
            bj = true;
            com.muqingbfq.bfqkz.ms = 1;
        }
        try {
            wl.Cookie = sp.getString("Cookie", "");
        } catch (Exception e) {
            edit.putString("Cookie", "");
            wl.Cookie = "";
            bj = true;
        }
        if (bj) {
            edit.commit();
        }

        wl.Cookie = main.sp.getString("Cookie", "");
        if (wl.Cookie.isEmpty()) {
            new visitor();
        }
        SharedPreferences theme = getSharedPreferences("theme", MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor edit = theme.edit();
        int i = theme.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (i == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            edit.putInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            edit.apply();
        }
        AppCompatDelegate.setDefaultNightMode(i);
        String jsonList = this.getSharedPreferences("list", Context.MODE_PRIVATE)
                .getString("listData", null); // 获取保存的 JSON 字符串
        if (jsonList != null) {
            Type type = new TypeToken<List<MP3>>() {
            }.getType();
            bfqkz.list = new com.google.gson.Gson().fromJson(jsonList, type);
            // 将 JSON 字符串转换回列表数据
        }
//        bfqkz.xm = wj.getMP3FromFile();
        // 注册 ProcessLifecycleOwner 以监听应用生命周期事件
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onStart(@Nullable LifecycleOwner owner) {
                // 应用进入前台
                gj.sc("onStart");
                if (FloatingLyricsService.lei != null) {
                    stopService(new Intent(main.this, FloatingLyricsService.class));
                }
            }

            @Override
            public void onStop(@Nullable LifecycleOwner owner) {
                // 应用进入后台
                gj.sc("onStop");
                if (Settings.canDrawOverlays(main.this) && FloatingLyricsService.lei == null ) {
                    startService(new Intent(main.this, FloatingLyricsService.class));
                }
            }
        });
    }
}
