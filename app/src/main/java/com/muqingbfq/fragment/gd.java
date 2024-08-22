package com.muqingbfq.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqingbfq.R;
import com.muqingbfq.XM;
import com.muqingbfq.api.playlist;
import com.muqingbfq.api.resource;
import com.muqingbfq.bfqkz;
import com.muqingbfq.databinding.ActivityGdBinding;
import com.muqingbfq.databinding.ListGdBBinding;
import com.muqingbfq.databinding.ListGdBinding;
import com.muqingbfq.main;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;
import com.muqingbfq.mq.wl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class gd extends com.muqingbfq.mq.FragmentActivity<ActivityGdBinding> {
    public static String gdid;
    private final List<XM> list = new ArrayList<>();
    public baseadapter adapter;
    int k;

    public static void start(Activity context, String[] str, View view) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context,
                view, "text");
        Intent intent = new Intent(context, gd.class);
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
        adapter = new baseadapter(this, list);
        k = (int) (getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().density + 0.5f);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, k / 120);
        binding.lb.setLayoutManager(gridLayoutManager);
        binding.lb.setAdapter(adapter);
        String id = intent.getStringExtra("id");
        new start(id);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this);
        }
        return true;
    }

    @Override
    protected ActivityGdBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityGdBinding.inflate(layoutInflater);
    }

    @SuppressLint("NotifyDataSetChanged")
    class start extends Thread {
        String id;

        public start(String id) {
            binding.recyclerview1Bar.setVisibility(View.VISIBLE);
            this.id = id;
            list.clear();
            start();
        }

        @Override
        public void run() {
            super.run();
            if (id.equals("排行榜")) {
                resource.leaderboard(list);
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
                        list.add(new XM(id, name, coverImgUrl));
                    }
                } catch (Exception e) {
                    gj.sc(e);
                }
            }
            main.handler.post(() -> {
                adapter.notifyDataSetChanged();
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

    public static class baseadapter extends RecyclerView.Adapter<VH> {
        Activity context;
        public List<XM> list;

        public baseadapter(Activity context, List<XM> list) {
            this.context = context;
            this.list = list;
        }

        boolean bool = false;

        public baseadapter(Activity context, List<XM> list, boolean bool) {
            this.context = context;
            this.list = list;
            this.bool = bool;
        }

        public void setonlong(int position) {
            XM xm = list.get(position);
            gj.sc(xm.name);
            String[] stringArray = context.getResources()
                    .getStringArray(R.array.gd_list);
            if (!wj.cz(wj.gd + xm.id)) {
                stringArray = new String[]{"下载歌单"};
            }
            String[] finalStringArray = stringArray;
            new MaterialAlertDialogBuilder(context).
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
                                                wode.addlist(fh);
                                                main.handler.post(() -> notifyItemChanged(position));
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
                                    wode.removelist(xm);
                                } catch (Exception e) {
                                    gj.sc(e);
                                }
                                break;
                        }
                        // 在这里处理菜单项的点击事件
                    }).show();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (bool) {
                return new VH(ListGdBBinding.bind(LayoutInflater.from(context)
                        .inflate(R.layout.list_gd_b, parent, false)));
            }
            return new VH(ListGdBinding.bind(LayoutInflater.from(context)
                    .inflate(R.layout.list_gd, parent, false)));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            XM xm = list.get(position);
            holder.itemView.setOnClickListener(new CARD(position));
            Drawable color_kg = ContextCompat.getDrawable(context, R.drawable.zt);
            if (xm.id.equals(gdid)) {
                color_kg = ContextCompat.getDrawable(context, R.drawable.bf);
            }
            if (bool) {
                holder.bindingB.text1.setText(xm.name);
                holder.bindingB.text2.setText(xm.message);
                Glide.with(holder.itemView.getContext())
                        .load(xm.picurl)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(R.drawable.ic_launcher_foreground))
                        .into(holder.bindingB.image);
                holder.bindingB.kg.setOnClickListener(new KG(this, xm.id));
            } else {
                holder.binding.text1.setText(xm.name);
                holder.binding.kg.setImageDrawable(color_kg);
                holder.binding.kg.setOnClickListener(new KG(this, xm.id));
                Glide.with(holder.itemView.getContext())
                        .asBitmap()
                        .load(xm.picurl)
                        .addListener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(@NonNull Bitmap resource, @NonNull Object model, Target<Bitmap> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                Palette.from(resource).generate(palette -> {
                                    int color = palette.getLightMutedColor(Color.WHITE);
                                    GradientDrawable gradientDrawable = new GradientDrawable(
                                            GradientDrawable.Orientation.BOTTOM_TOP,
                                            new int[]{color, color});
                                    gradientDrawable.setAlpha(128);
                                    holder.binding.text1.setBackground(gradientDrawable);
                                    holder.binding.getRoot().setRippleColor(ColorStateList.valueOf(color));
                                });
                                holder.binding.image.setImageBitmap(resource);
                                return true;
                            }
                        })
                        .into(holder.binding.image);
            }
            holder.itemView.setOnLongClickListener(v -> {
                setonlong(position);
                return false;
            });
        }

        class KG implements View.OnClickListener {
            RecyclerView.Adapter adapter;
            String id;

            public KG(RecyclerView.Adapter adapter, String id) {
                this.adapter = adapter;
                this.id = id;

            }

            @Override
            public void onClick(View v) {
                new Thread() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        super.run();
                        boolean an = playlist.hq(bfqkz.list, id);
                        if (bfqkz.ms == 2) {
                            Collections.shuffle(bfqkz.list);
                        }
                        main.handler.post(() -> {
                            if (an) {
                                com.muqingbfq.bfq_an.xyq();
                                ((ImageView) v).setImageResource(R.drawable.bf);
                                com.muqingbfq.fragment.gd.gdid = id;
                            }
                            adapter.notifyDataSetChanged();
                        });
                    }
                }.start();
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class CARD implements View.OnClickListener {
            int position;

            public CARD(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View view) {
                XM xm = list.get(position);
                mp3.start(context, new String[]{xm.id, xm.name}, view);
            }
        }

    }

    static class VH extends RecyclerView.ViewHolder {
        public ListGdBinding binding;

        public VH(@NonNull ListGdBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        ListGdBBinding bindingB;

        public VH(@NonNull ListGdBBinding itemView) {
            super(itemView.getRoot());
            bindingB = itemView;
        }
    }

}