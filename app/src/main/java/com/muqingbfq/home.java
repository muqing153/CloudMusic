package com.muqingbfq;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaController;
import androidx.media3.session.MediaSession;
import androidx.media3.session.SessionToken;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchView;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.muqingbfq.databinding.ActivityHomeBinding;
import com.muqingbfq.fragment.SearchTools;
import com.muqingbfq.fragment.gd_adapter;
import com.muqingbfq.fragment.sz;
import com.muqingbfq.fragment.wode;
import com.muqingbfq.mq.AppCompatActivity;
import com.muqingbfq.mq.gj;
import com.muqingbfq.view.Edit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class home extends AppCompatActivity<ActivityHomeBinding> {

    @Override
    protected ActivityHomeBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityHomeBinding.inflate(layoutInflater);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_muqing);
        EdgeToEdge.enable(this);
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


    public void toolbar() {
        setSupportActionBar(binding.toolbar);
    }

    public void UI() {
        setContentView();
//        禁止ViewPager2滑动
        binding.viewPager.setUserInputEnabled(false);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            binding.chb.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });
//        viewTop=binding.
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
        binding.viewPager.setCurrentItem(0, false);
        if (!gj.isTablet(this)) {
            binding.linearLayout4.post(() -> {
                int height = binding.linearLayout4.getHeight();
                binding.viewPager.setPadding(0, 0, 0, height);
            });
        }
        binding.fragmentDb.post(() -> {
            int height = binding.fragmentDb.getHeight();
            binding.searchview.setPadding(0, 0, 0, height);

        });
        binding.tablayout.addView("推荐", R.drawable.zhuye).setOnClickListener(v -> {
            for (int i = 0; i < binding.tablayout.sizeView; i++) {
                MaterialCardView childAt = (MaterialCardView) binding.tablayout.linearLayout.getChildAt(i);
                if (i == 0) {
                    //背景高亮
                    childAt.setCardBackgroundColor(gj.getThemeColor(v.getContext(), com.google.android.material.R.attr.colorPrimaryContainer));
                } else {
                    //背景恢复
                    childAt.setCardBackgroundColor(gj.getThemeColor(v.getContext(), com.google.android.material.R.attr.colorSurface));
                }
            }
            binding.viewPager.setCurrentItem(0, false);
        });
        binding.tablayout.addView("我的", R.drawable.user).setOnClickListener(v -> {
            for (int i = 0; i < binding.tablayout.sizeView; i++) {
                MaterialCardView childAt = (MaterialCardView) binding.tablayout.linearLayout.getChildAt(i);
                if (i == 1) {
                    //背景高亮
                    childAt.setCardBackgroundColor(gj.getThemeColor(v.getContext(), com.google.android.material.R.attr.colorPrimaryContainer));
                } else {
                    //背景恢复
                    childAt.setCardBackgroundColor(gj.getThemeColor(v.getContext(), com.google.android.material.R.attr.colorSurface));
                }
            }
            binding.viewPager.setCurrentItem(1, false);
        });
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
    private final List<String> searchList = new ArrayList<>();

    private activity_search.SearchRecordAdapter searchRecordAdapter;

    public void SearchUI() {
        SearchTools search = new SearchTools(this, binding.searchTablayout, binding.searchViewPager);
        binding.searchview
                .getEditText()
                .setOnEditorActionListener(
                        (v, actionId, event) -> {
//                            binding.searchview.hide();
                            searchStart(search, binding.toolbar.getText().toString());
                            return false;
                        });
        binding.searchview.setOnMenuItemClickListener(
                menuItem -> {
                    // Handle menuItem click.
                    return true;
                });
        FlexboxLayoutManager manager = new FlexboxLayoutManager(this);
        //设置主轴排列方式
        manager.setFlexDirection(FlexDirection.ROW);
        //设置是否换行
        manager.setFlexWrap(FlexWrap.WRAP);
        manager.setAlignItems(AlignItems.STRETCH);//历史记录的LayoutManager
        binding.listRecycler.setNestedScrollingEnabled(false);
        binding.listRecycler.setLayoutManager(manager);
        binding.searchview.addTransitionListener(
                (searchView, previousState, newState) -> {
                    if (newState == SearchView.TransitionState.SHOWING) {
                        // Handle search view opened.
                        gj.sc("SHOWING");
//                        binding.tablayout.setVisibility(View.GONE);
                        searchRecordAdapter = new activity_search.SearchRecordAdapter(binding.searchview);
                        binding.listRecycler.setAdapter(searchRecordAdapter);
                    } else if (newState == SearchView.TransitionState.SHOWN) {
                        gj.sc("SHOWN");
                    } else if (newState == SearchView.TransitionState.HIDING) {
                        binding.tablayout.setVisibility(View.VISIBLE);
                        searchRecordAdapter = null;
                        binding.listRecycler.setAdapter(null);
                        binding.searchFragment.setVisibility(View.GONE);
                        binding.searchRecycler.setVisibility(View.GONE);
                        binding.xxbj1.setVisibility(View.VISIBLE);
                        // 移除当前显示的 Fragment
                        search.delete();
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
                                    hq("/search/suggest", "keywords=" + var1.toString() + "&type=mobile", false);
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
                                    searchStart(search, string);
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
                    binding.searchFragment.setVisibility(View.GONE);
                } else if (!issearchclicklist) {
                    binding.searchRecycler.setVisibility(View.VISIBLE);
                    binding.xxbj1.setVisibility(View.GONE);
                    binding.searchFragment.setVisibility(View.GONE);
                } else issearchclicklist = false;

            }
        });
        binding.toolbar.setNavigationIcon(R.drawable.menu);
        binding.deleat.setOnClickListener(v -> new MaterialAlertDialogBuilder(
                v.getContext())
                .setTitle("删除")
                .setMessage("清空历史记录？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialogInterface, ii) -> {
                    searchRecordAdapter.json_list.clear();
                    binding.listRecycler.setAdapter(searchRecordAdapter);
                    com.muqingbfq.mq.wj.sc(com.muqingbfq.mq.wj.filesdri +
                            com.muqingbfq.mq.wj.lishi_json);
                })
                .show());

    }

    public void searchStart(SearchTools search, String name) {
        issearchclicklist = true;
        binding.toolbar.setText(binding.searchview.getText());
        if (!TextUtils.isEmpty(name)) {
            binding.searchFragment.setVisibility(View.VISIBLE);
            binding.searchRecycler.setVisibility(View.GONE);
            search.sx(name);
            activity_search.addSearchRecord(name, searchRecordAdapter.json_list, searchRecordAdapter);
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
            binding.searchview.show();
//            startActivity(new Intent(this, activity_search.class));
        } else if (item.getItemId() == android.R.id.home) {
            //展开侧滑
            binding.chct.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}