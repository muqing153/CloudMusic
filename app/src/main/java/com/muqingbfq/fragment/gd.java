package com.muqingbfq.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.muqingbfq.R;
import com.muqingbfq.XM;
import com.muqingbfq.adapter.AdapterGdH;
import com.muqingbfq.api.resource;
import com.muqingbfq.databinding.ActivityGdBinding;
import com.muqingbfq.databinding.ListGdBBinding;
import com.muqingbfq.mq.FragmentActivity;
import com.muqingbfq.mq.VH;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class gd extends FragmentActivity<ActivityGdBinding> {

    public Adapter adapter = new Adapter();
    int k;

    public static void start(Activity context, String[] str, View view) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context,
                view, "text");
        Intent intent = new Intent(context, gd.class);
        intent.putExtra("id", str[0]);
        intent.putExtra("name", str[1]);
        context.startActivity(intent, options.toBundle());
    }

    public static void start(Context context, String[] str) {
        Intent intent = new Intent(context, gd.class);
        intent.putExtra("id", str[0]);
        intent.putExtra("name", str[1]);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        Intent intent = getIntent();
        binding.title.setText(intent.getStringExtra("name"));
//        k = (int) (getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().density + 0.5f);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, k / 120);
        binding.lb.setLayoutManager(new LinearLayoutManager(this));
        binding.lb.setAdapter(adapter);

        binding.edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.edittext.getVisibility() == View.VISIBLE) {
                    adapter.getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        String id = intent.getStringExtra("id");

        binding.fragmentDb.post(new Runnable() {
            @Override
            public void run() {
                int height = binding.fragmentDb.getHeight();
                binding.lb.setPadding(0,0,0,height);
            }
        });
        new start(id);
    }

    @Override
    protected ActivityGdBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityGdBinding.inflate(layoutInflater);
    }

    class start extends Thread {
        String id;

        public start(String id) {
            binding.recyclerview1Bar.setVisibility(View.VISIBLE);
            this.id = id;
            adapter.list.clear();
            start();
        }

        @Override
        public void run() {
            super.run();
            if (id.equals("排行榜")) {
                resource.leaderboard(adapter.list);
            } else {
                String hq = wl.hq("/search?keywords=" + id + "&limit=" + (k * 3) + "&type=1000");
                try {
                    JSONArray jsonArray = new JSONObject(hq).getJSONObject("result")
                            .getJSONArray("playlists");
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String coverImgUrl = jsonObject.getString("coverImgUrl");
                        adapter.list.add(new XM(id, name, coverImgUrl));
                    }
                } catch (Exception e) {
                    gj.sc(e);
                }
            }
            runOnUiThread(() -> {
                binding.lb.setAdapter(adapter);
                binding.recyclerview1Bar.setVisibility(View.GONE);
                if (adapter.list.isEmpty()) {
                    binding.recyclerview1Text.setVisibility(View.VISIBLE);
                    binding.recyclerview1Text.setOnClickListener(v -> new start(id));
                } else {
                    binding.recyclerview1Text.setVisibility(View.GONE);
                }
            });
        }
    }

/*
    public void setonlong(int position) {
        XM xm = list.get(position);
        gj.sc(xm.name);
        String[] stringArray = getResources()
                .getStringArray(R.array.gd_list);
        if (!wj.cz(wj.gd + xm.id)) {
            stringArray = new String[]{"下载歌单"};
        }
        String[] finalStringArray = stringArray;
        new MaterialAlertDialogBuilder(this).
                setItems(stringArray, (dialog, id) -> {
                    switch (finalStringArray[id]) {
                        case "下载歌单":
                            new Thread() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void run() {
                                    String hq = playlist.gethq(xm.id);
                                    if (hq != null) {
                                        try {
                                            XM fh = resource.Playlist_content(xm.id);
                                            JSONObject json = new JSONObject(hq);
                                            json.put("name", fh.name);
                                            json.put("picUrl", fh.picurl);
                                            json.put("message", fh.message);
//                                                        json.put(fh.id, json);
                                            wj.xrwb(wj.gd + xm.id, json.toString());
//                                            wode.addlist(fh);
//                                            main.handler.post(() -> notifyItemChanged(position));
                                        } catch (JSONException e) {
                                            gj.sc("list gd onclick thear " + e);
                                        }
                                    }
                                }
                            }.start();
                            break;
                        case "删除歌单":
//                                        删除项目
                            try {
                                wj.sc(wj.gd + xm.id);
//                                wode.removelist(xm);
                            } catch (Exception e) {
                                gj.sc(e);
                            }
                            break;
                    }
                    // 在这里处理菜单项的点击事件
                }).show();
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem itemA = menu.add("搜索");
        itemA.setTitle("搜索");
        itemA.setIcon(R.drawable.sousuo);
        itemA.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            if (binding.edittext.getVisibility() == View.VISIBLE) {
                binding.title.setVisibility(View.VISIBLE);
                binding.edittext.setVisibility(View.GONE);
                gj.ycjp(binding.edittext);
                adapter.getFilter().filter("");
            } else {
                ActivityCompat.finishAfterTransition(this);
            }
        } else if (itemId == 0) {
            binding.title.setVisibility(View.GONE);
            binding.edittext.setVisibility(View.VISIBLE);
//            gj.tcjp(binding.edittext);
        }
        return true;
    }


    public class Adapter extends AdapterGdH implements Filterable {
        private List<XM> list_ys;

        public Adapter() {
            list_ys = list;
        }

        @Override
        public void onBindViewHolder(@NonNull VH<ListGdBBinding> holder, int position) {
            super.onBindViewHolder(holder, position);
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        //没有过滤的内容，则使用源数据
                        list = list_ys;
                    } else {
                        List<XM> filteredList = new ArrayList<>();
                        for (int i = 0; i < list_ys.size(); i++) {
                            XM xm = list_ys.get(i);
                            if (xm.name.contains(charString)) {
                                filteredList.add(list_ys.get(i));
                            }
                        }
                        list = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = list;
                    return filterResults;
                }


                @SuppressLint("NotifyDataSetChanged")
                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    list = (List<XM>) filterResults.values;
                    binding.lb.setAdapter(adapter);
                }
            };
        }
    }
}