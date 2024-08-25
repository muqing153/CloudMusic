package com.muqingbfq;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.jaeger.library.StatusBarUtil;
import com.muqingbfq.databinding.ActivityHomeBinding;
import com.muqingbfq.fragment.gd_adapter;
import com.muqingbfq.fragment.wode;
import com.muqingbfq.mq.AppCompatActivity;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wl;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity<ActivityHomeBinding> {

    @Override
    protected ActivityHomeBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityHomeBinding.inflate(layoutInflater);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_muqing);
        super.onCreate(savedInstanceState);


        SessionToken sessionToken =
                new SessionToken(this, new ComponentName(this, PlaybackService.class));
        ListenableFuture<MediaController> controllerFuture =
                new MediaController.Builder(this, sessionToken).buildAsync();
        controllerFuture.addListener(new Runnable() {
            @Override
            public void run() {

            }
        }, MoreExecutors.directExecutor());
//        if (PlaybackService.mediaSession!=null){
//            Player player = PlaybackService.mediaSession.getPlayer();
//            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "26096272");
//            // 设置媒体源（音乐文件）
//            MediaItem mediaItem = MediaItem.fromUri(file.getPath()); // 替换为你的音乐文件路径或 URL
//            player.setMediaItem(mediaItem);
//
//            // 准备并开始播放
//            player.prepare();
//            player.play();
//        }

        wl.Cookie = main.sp.getString("Cookie", "");
        if (wl.Cookie.isEmpty()) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    String hq = wl.hq("/register/anonimous");
                    try {
                        JSONObject jsonObject = new JSONObject(hq);
                        wl.setcookie(jsonObject.getString("cookie"));
                        home.this.runOnUiThread(() -> UI());
                    } catch (Exception e) {
                        com.muqingbfq.mq.gj.sc(e);
                    }
                }
            }.start();
        } else {
            UI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void toolbar() {
//        binding.bar.setupWithDrawer
        setSupportActionBar(binding.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.chct, binding.toolbar, R.string.app_name, R.string.app_name);
        binding.chct.addDrawerListener(toggle);
        toggle.syncState();
        binding.toolbar.setOnClickListener(v -> activity_search.start(home.this, v));
    }

    public void UI() {
        StatusBarUtil.setTransparent(home.this);
        setContentView();
        toolbar();
        //初始化侧滑
        binding.chb.setNavigationItemSelectedListener(item -> {
            com.muqingbfq.fragment.sz.switch_sz(home.this, item.getItemId());
            return false;
        });
        List<Fragment> list = new ArrayList<>();
        list.add(new gd_adapter());
        list.add(new wode());
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return list.get(position);
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
        binding.viewPager.setSaveEnabled(false);
// 将 ViewPager2 绑定到 TabLayou
        binding.tablayout.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.a) {
                binding.viewPager.setCurrentItem(0);
            } else if (itemId == R.id.c) {
                binding.viewPager.setCurrentItem(1);
            }
            return true;
        });
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        binding.tablayout.setSelectedItemId(R.id.a);
                        break;
                    case 1:
                        binding.tablayout.setSelectedItemId(R.id.c);
                        break;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在销毁 Activity 之前，系统会先调用 onDestroy()。系统调用此回调的原因如下：
        // 保存列表数据

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_search) {
            startActivity(new Intent(this, activity_search.class));
        }
        return super.onOptionsItemSelected(item);
    }
}