package com.muqingbfq.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqingbfq.MP3;
import com.muqingbfq.R;
import com.muqingbfq.api.FileDownloader;
import com.muqingbfq.api.playlist;
import com.muqingbfq.bfq;
import com.muqingbfq.bfq_an;
import com.muqingbfq.bfqkz;
import com.muqingbfq.databinding.ActivityMp3Binding;
import com.muqingbfq.databinding.ListMp3Binding;
import com.muqingbfq.list.MyViewHoder;
import com.muqingbfq.main;
import com.muqingbfq.mq.FragmentActivity;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class mp3 extends FragmentActivity<ActivityMp3Binding> {
    private List<MP3> list = new ArrayList<>();
    private List<MP3> list_ys = new ArrayList<>();
    public static Adapter adapter;

    public static void start(Activity context, String[] str, View view) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context,
                view, "text");
        Intent intent = new Intent(context, mp3.class);
        intent.putExtra("id", str[0]);
        intent.putExtra("name", str[1]);
        context.startActivity(intent, options.toBundle());
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewBinding().getRoot());
        setToolbar();
        Intent intent = getIntent();
        binding.title.setText(intent.getStringExtra("name"));
        String id = intent.getStringExtra("id");
        adapter = new Adapter(list);
        binding.lb.setLayoutManager(new LinearLayoutManager(this));
        binding.lb.setAdapter(adapter);
        new start(id);
        binding.edittext.addTextChangedListener(new TextWatcher()  {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem itemA = menu.add("搜索");
        itemA.setTitle("搜索");
        itemA.setIcon(R.drawable.sousuo);
        itemA.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected ActivityMp3Binding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityMp3Binding.inflate(layoutInflater);
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
            gj.tcjp(binding.edittext);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (binding.edittext.getVisibility() == View.VISIBLE) {
            binding.title.setVisibility(View.VISIBLE);
            binding.edittext.setVisibility(View.GONE);
            gj.ycjp(binding.edittext);
            adapter.getFilter().filter("");
        } else {
            ActivityCompat.finishAfterTransition(this);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    class start extends Thread {
        String id;

        public start(String id) {
            binding.recyclerview1Bar.setVisibility(View.VISIBLE);
            this.id = id;
            list.clear();
            list_ys.clear();
            start();
        }

        @Override
        public void run() {
            super.run();
            switch (id) {
                case "mp3_xz.json":
                    playlist.hq_xz(list);
                    break;
                case "mp3_like.json":
                    playlist.hq_like(list);
                    break;
                case "cd.json":
                    playlist.hq_cd(mp3.this, list);
                    break;
                default:
                    playlist.hq(list, id);
                    break;
            }
            list_ys = list;
            main.handler.post(() -> {
                binding.lb.getAdapter().notifyDataSetChanged();
                binding.recyclerview1Bar.setVisibility(View.GONE);
                if (list.isEmpty()) {
                    binding.recyclerview1Text.setVisibility(View.VISIBLE);
                    binding.recyclerview1Text.setOnClickListener(v -> new start(id));
                } else {
                    binding.recyclerview1Text.setVisibility(View.GONE);
                }
            });
        }
    }
    public static class Adapter extends RecyclerView.Adapter<MyViewHoder> implements Filterable {

        private List<MP3> list;
        private List<MP3> list_ys;

        public Adapter(List<MP3> list) {
            this.list = list;
            list_ys = list;
        }

        @NonNull
        @Override
        public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHoder(ListMp3Binding.bind(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_mp3,
                            parent, false)));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {
            MP3 x = list.get(position);
            holder.binding.text1.setText(String.valueOf(position + 1));
            holder.binding.name.setText(x.name);
            holder.binding.zz.setText(x.zz);
            if (bfqkz.xm != null && x.id.equals(bfqkz.xm.id)) {
                TypedValue typedValue = new TypedValue();
                holder.itemView.getContext().getTheme().resolveAttribute(
                        com.google.android.material.R.attr.colorSurfaceVariant, typedValue, true);
                int colorSurface = typedValue.data;
                // 这里 colorSurface 就是你要找的颜色值
                holder.binding.getRoot().setCardBackgroundColor(colorSurface);
            }else{
                holder.binding.getRoot().setCardBackgroundColor(ContextCompat
                        .getColor(holder.itemView.getContext(), android.R.color.transparent));
            }
//            holder.binding.zz.setTextColor(color);
            holder.itemView.setOnClickListener(view -> {
                if (bfqkz.list!=list) {
                    bfqkz.list.clear();
                    bfqkz.list.addAll(list);
                }
                bfq.startactivity(holder.getContext(), x);
            });
            holder.itemView.setOnLongClickListener(view -> {
                List<String> stringList = new ArrayList<>();
                boolean getlike = bfq_an.getlike(x);
                if (getlike) {
                    stringList.add("取消喜欢");
                } else {
                    stringList.add("喜欢歌曲");
                }
                if (wj.cz(wj.mp3 + x.id)) {
                    stringList.add("删除下载");
                } else {
                    stringList.add("下载歌曲");
                }
                stringList.add("复制名字");
                String[] array = stringList.toArray(new String[0]);
                new MaterialAlertDialogBuilder(view.getContext()).
                        setItems(array, (dialog, id) -> {
                            switch (array[id]) {
                                case "下载歌曲":
                                    new FileDownloader(view.getContext()).downloadFile(x);
                                    break;
                                case "删除下载":
                                    wj.sc(wj.mp3 + x.id);
/*                                    if (sc&&) {
                                        list.remove(position);
                                        notifyItemRemoved(position);
                                    }*/
                                    break;
                                case "喜欢歌曲":
                                case "取消喜欢":
                                    try {
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<List<MP3>>() {
                                        }.getType();
                                        List<MP3> list = gson.fromJson(wj.dqwb(wj.gd + "mp3_like.json"), type);
                                        if (list == null) {
                                            list = new ArrayList<>();
                                        }
                                        if (list.contains(x))
                                            list.remove(x);
                                        else
                                            list.add(x);
                                        wj.xrwb(wj.gd + "mp3_like.json", gson.toJson(list));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "复制名字":
                                    gj.fz(view.getContext(), x.name);
                                    break;

                            }
                        }).show();
                return false;
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
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
                        List<MP3> filteredList = new ArrayList<>();
                        for (int i = 0; i < list_ys.size(); i++) {
                            MP3 mp3 = list_ys.get(i);
                            if (mp3.name.contains(charString)
                                    || mp3.zz.contains(charString)) {
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
                    list = (List<MP3>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }


    public static void startactivity(Context context, String id) {
        context.startActivity(new Intent(context, mp3.class).putExtra("id", id));
    }

    @Override
    public void finish() {
        super.finish();
        adapter = null;
    }
}
