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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.muqingbfq.MP3;
import com.muqingbfq.R;
import com.muqingbfq.adapter.AdapterMp3;
import com.muqingbfq.api.playlist;
import com.muqingbfq.databinding.ActivityMp3Binding;
import com.muqingbfq.databinding.ListMp3ImageBinding;
import com.muqingbfq.main;
import com.muqingbfq.mq.FragmentActivity;
import com.muqingbfq.mq.VH;
import com.muqingbfq.mq.gj;

import java.util.ArrayList;
import java.util.List;

public class mp3 extends FragmentActivity<ActivityMp3Binding> {
    private List<MP3> list = new ArrayList<>();
    private List<MP3> list_ys = new ArrayList<>();
    public Adapter adapter;

    public static void start(Activity context, String[] str, View view) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context,
                view, "text");
        Intent intent = new Intent(context, mp3.class);
        intent.putExtra("id", str[0]);
        intent.putExtra("name", str[1]);
        context.startActivity(intent, options.toBundle());
    }

    public static void start(Context context, String[] strings) {
        Intent intent = new Intent(context, mp3.class);
        intent.putExtra("id", strings[0]);
        intent.putExtra("name", strings[1]);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        Intent intent = getIntent();
        binding.title.setText(intent.getStringExtra("name"));
        String id = intent.getStringExtra("id");
        adapter = new Adapter(list);
        binding.lb.setLayoutManager(new LinearLayoutManager(this));
        binding.lb.setAdapter(adapter);
        new start(id);
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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {
                if (binding.edittext.getVisibility() == View.VISIBLE) {
                    binding.title.setVisibility(View.VISIBLE);
                    binding.edittext.setVisibility(View.GONE);
                    gj.ycjp(binding.edittext);
                    adapter.getFilter().filter("");
                } else {
                    finish();
//                    ActivityCompat.finishAfterTransition(mp3.this);
                }
            }
        });
        binding.fragmentDb.post(new Runnable() {
            @Override
            public void run() {
                int height = binding.fragmentDb.getHeight();
                binding.lb.setPadding(0, 0, 0, height);
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

    public class Adapter extends AdapterMp3 implements Filterable {

        private List<MP3> list_ys;

        public Adapter(List<MP3> list) {
            this.list = list;
            list_ys = list;
        }

        @Override
        public void onBindViewHolder(@NonNull VH<ListMp3ImageBinding> holder, int position) {
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
