package com.muqingbfq;

import android.content.ComponentName;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.navigation.NavigationBarView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.muqing.AppCompatActivity;
import com.muqing.Dialog.DialogEditText;
import com.muqing.ViewUI.SettingSwitch;
import com.muqing.gj;
import com.muqingbfq.activity.activity_search;
import com.muqingbfq.databinding.ActivityHomeBinding;
import com.muqingbfq.fragment.gd_adapter;
import com.muqingbfq.fragment.sz;
import com.muqingbfq.fragment.wode;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity<ActivityHomeBinding> {

    @Override
    protected ActivityHomeBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityHomeBinding.inflate(layoutInflater);
    }

    public static int ColorBackground = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeSteer homeSteer = new HomeSteer(this, this::UI);
        SessionToken sessionToken =
                new SessionToken(this, new ComponentName(this, PlaybackService.class));
        ListenableFuture<MediaController> controllerFuture =
                new MediaController.Builder(this, sessionToken).buildAsync();
        //加载完成
        controllerFuture.addListener(() -> {
            gj.sc("加载完成");
            homeSteer.SetIP();
        }, MoreExecutors.directExecutor());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setOnApplyWindowInsetsListener(Insets systemBars, View v) {
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//        v.setPadding(systemBars.left, 0, systemBars.right, 0);
//        binding.chb.setPadding(0, systemBars.top, 0, systemBars.bottom);
//        binding.toolbar.set
    }

    public void UI() {
        setContentView();
        ColorBackground = gj.getbackgroundColor(this);
//        禁止ViewPager2滑动
        binding.viewPager.setUserInputEnabled(false);
//        viewTop=binding.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });

        //初始化侧滑
        binding.chb.setNavigationItemSelectedListener(item -> {
            sz.switch_sz(home.this, item.getItemId());
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
        binding.viewPager.setCurrentItem(0, false);
        if (!gj.isTablet(this)) {
            if (binding.linearLayout4 != null) {
                binding.linearLayout4.post(() -> {
                    int height = binding.linearLayout4.getHeight();
                    binding.viewPager.setPadding(0, 0, 0, height);
                });
            }
        }
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setOnClickListener((DialogEditText.OnClickListener) view -> activity_search.start(home.this, binding.toolbar));
        binding.toolbar.setNavigationIcon(R.drawable.menu);

        NavigationBarView tablayout = (NavigationBarView) binding.tablayout;
        tablayout.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.a) {
                binding.viewPager.setCurrentItem(0, true);

            } else if (itemId == R.id.c) {
                binding.viewPager.setCurrentItem(1, true);
            }
//            tablayout.setSelectedItemId(itemId);
            return true;
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在销毁 Activity 之前，系统会先调用 onDestroy()。系统调用此回调的原因如下：
        // 保存列表数据
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_search) {
            activity_search.start(this, binding.toolbar);
//            startActivity(new Intent(this, activity_search.class));
        } else if (item.getItemId() == android.R.id.home) {
            //展开侧滑
            binding.chct.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}