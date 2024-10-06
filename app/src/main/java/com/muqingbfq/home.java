package com.muqingbfq;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.search.SearchView;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.ListenableFuture;
import com.muqingbfq.databinding.ActivityHomeBinding;
import com.muqingbfq.fragment.gd_adapter;
import com.muqingbfq.fragment.search;
import com.muqingbfq.fragment.sz;
import com.muqingbfq.fragment.wode;
import com.muqingbfq.mq.AppCompatActivity;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wl;
import com.muqingbfq.view.Edit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity<ActivityHomeBinding> {

    @Override
    protected ActivityHomeBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityHomeBinding.inflate(layoutInflater);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.Theme_muqing);
        super.onCreate(savedInstanceState);

        // 启动前台服务时，确保类型为媒体播放
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            startForegroundService(new Intent(this, YourForegroundService.class)
//                    .putExtra("EXTRA_SERVICE_TYPE", ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK));
//        } else {
//            startService(new Intent(this, YourForegroundService.class));
//        }

        SessionToken sessionToken =
                new SessionToken(this, new ComponentName(this, PlaybackService.class));
        ListenableFuture<MediaController> controllerFuture =
                new MediaController.Builder(this, sessionToken).buildAsync();
//        controllerFuture.addListener(() -> {
//
//        }, MoreExecutors.directExecutor());

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
                        runOnUiThread(() -> UI());
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
        setSupportActionBar(binding.toolbar);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, binding.chct, binding.toolbar, R.string.app_name, R.string.app_name);
//        binding.chct.addDrawerListener(toggle);
//        toggle.syncState();
//        binding.toolbar.setOnClickListener(v -> activity_search.start(home.this, v));
    }

    public void UI() {
        setContentView();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.searchview.isShowing()) {
                    binding.searchview.hide();
                    return;
                }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK)
                    != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK},
                            1001);
                }
            }
        }
        toolbar();
        SearchUI();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //在销毁 Activity 之前，系统会先调用 onDestroy()。系统调用此回调的原因如下：
        // 保存列表数据

    }

    public boolean issearchclicklist = false;//是否点击了列表项目
    //搜索建议列表
    private List<String> searchList = new ArrayList<>();
    public void SearchUI() {
        binding.searchview
                .getEditText()
                .setOnEditorActionListener(
                        (v, actionId, event) -> {
                            binding.toolbar.setText(binding.searchview.getText());
//                            binding.searchview.hide();
                            return false;
                        });
        binding.searchview.setOnMenuItemClickListener(
                menuItem -> {
                    // Handle menuItem click.
                    return true;
                });
        binding.searchview.addTransitionListener(
                (searchView, previousState, newState) -> {
                    if (newState == SearchView.TransitionState.SHOWING) {
                        // Handle search view opened.
                        gj.sc("SHOWING");
                        binding.tablayout.setVisibility(View.GONE);
                    } else if (newState == SearchView.TransitionState.SHOWN) {
                        gj.sc("SHOWN");
                    } else if (newState == SearchView.TransitionState.HIDING) {
                        binding.tablayout.setVisibility(View.VISIBLE);
                        binding.searchFragment.setVisibility(View.GONE);
                        binding.searchRecycler.setVisibility(View.GONE);
                        binding.xxbj1.setVisibility(View.VISIBLE);
                    }
                });
        final Object o = new Object();
        binding.searchview.getEditText().addTextChangedListener(new Edit.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence var1, int var2, int var3, int var4) {
            }

            @Override
            public void onTextChanged(CharSequence var1, int var2, int var3, int var4) {
                new Thread() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        synchronized (o) {
                            searchList.clear();
                            String hq = com.muqingbfq.mq.wl.
                                    hq("/search/suggest?keywords=" + var1.toString() + "&type=mobile");
                            try {
                                JSONArray jsonArray = new JSONObject(hq).getJSONObject("result")
                                        .getJSONArray("allMatch");
                                int length = jsonArray.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String keyword = jsonObject.getString("keyword");
                                    searchList.add(keyword);
                                }
                                runOnUiThread(() -> binding.searchRecycler.setAdapter(new activity_search.search_adapter(searchList, string -> {
                                    gj.sc(string);
                                    issearchclicklist = true;
                                    binding.searchRecycler.setAdapter(null);
                                    binding.searchRecycler.setVisibility(View.GONE);
                                    binding.xxbj1.setVisibility(View.GONE);
                                    binding.searchFragment.setVisibility(View.VISIBLE);
                                    binding.searchview.setText(string);
                                    searchStart(string);
//                                        binding.searchRecycler.set
                                })));
                            } catch (Exception e) {
                                gj.sc(e);
                            }
                        }
                    }
                }.start();
            }

            @Override
            public void afterTextChanged(Editable var1) {
                if (Strings.isNullOrEmpty(var1.toString())) {
                    binding.searchRecycler.setAdapter(null);
                    binding.searchRecycler.setVisibility(View.GONE);
                    binding.xxbj1.setVisibility(View.VISIBLE);
                } else if (!issearchclicklist){
                    binding.searchRecycler.setVisibility(View.VISIBLE);
                    binding.xxbj1.setVisibility(View.GONE);
                }else issearchclicklist = false;

            }
        });
        binding.toolbar.setNavigationIcon(R.drawable.menu);

    }

    public void searchStart(String name) {
        if (!TextUtils.isEmpty(name)) {
            search sea = (search) getSupportFragmentManager().findFragmentById(binding.searchFragment.getId());
            binding.searchFragment.setVisibility(View.VISIBLE);
            sea.sx(name);
//            addSearchRecord(name);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_search) {
//            binding.searchview.show();
            startActivity(new Intent(this, activity_search.class));
        } else if (item.getItemId() == android.R.id.home) {
            //展开侧滑
            binding.chct.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}