package com.muqingbfq.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.muqingbfq.MP3;
import com.muqingbfq.PlaybackService;
import com.muqingbfq.R;
import com.muqingbfq.adapter.AdapterMp3;
import com.muqingbfq.api.playlist;
import com.muqingbfq.api.url;
import com.muqingbfq.bfqkz;
import com.muqingbfq.databinding.ActivityMp3Binding;
import com.muqingbfq.databinding.ListMp3ImageBinding;
import com.muqingbfq.main;
import com.muqingbfq.mq.FragmentActivity;
import com.muqingbfq.mq.VH;
import com.muqingbfq.mq.gj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

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

    public static Drawable drawable = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
//        drawable=null;
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.windowBackground, typedValue, true);
        ; // 获取当前主题的背景颜色
        if (drawable != null) {

// 4. 设置到 ImageView 上
            ImageView imageView = findViewById(R.id.toolbarimage);
            Glide.with(this)
                    .load(drawable)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(26,3)))
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {

                            // 使用 Glide 加载图片并应用高斯模糊效果
// 1. 创建渐变遮罩层（从底部白色渐变到顶部透明）
                            GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                    new int[] {typedValue.data, Color.TRANSPARENT});
                            gradient.setShape(GradientDrawable.RECTANGLE);

// 3. 使用 LayerDrawable 来组合模糊图像和渐变效果
                            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{resource, gradient});
                            imageView.setImageDrawable(layerDrawable);
                            return true;
                        }
                    })
                    .into(imageView);
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
//            binding.toolbar.setPadding(0, systemBars.top, 0, 0);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) binding.toolbar.getLayoutParams();
            params.setMargins(0, systemBars.top, 0, 0);  // 参数分别是 left, top, right, bottom
            binding.toolbar.setLayoutParams(params);

            return insets;
        });
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
        binding.fragmentDb.post(() -> {
            int height = binding.fragmentDb.getHeight();
            binding.lb.setPadding(0, 0, 0, height);
        });
        binding.playButton.setOnClickListener(v -> {
            v.setEnabled(false);
            if (PlaybackService.mediaSession == null) {
                return;
            }
            Player player = PlaybackService.mediaSession.getPlayer();
            player.clearMediaItems();
            List<MP3> aalist = new ArrayList<>(list);
            if (bfqkz.ms == 2) {
                Collections.shuffle(aalist);
            }
            for (int i = 0; i < aalist.size(); i++) {
                MP3 mp3 = aalist.get(i);
//                            mp3 = url.hq(mp3);
                MediaItem mediaItem = PlaybackService.GetMp3(mp3);
                player.addMediaItem(mediaItem);
            }
            player.prepare();
            player.seekTo(0, 0);
            player.play();
            //保存播放列表
            PlaybackService.list.clear();
            PlaybackService.list.addAll(aalist);
            PlaybackService.ListSave();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        try {
                            Thread.sleep(500);
                            //检测音乐是否播放
                            runOnUiThread(() -> {
                                if (!player.isPlaying()) {
                                    return;
                                }
                                v.setEnabled(true);
                            });
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }).start();
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
