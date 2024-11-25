package com.muqingbfq;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.common.Player;

import com.colorpicker.ColorPickerView;
import com.colorpicker.builder.ColorPickerDialogBuilder;
import com.dirror.lyricviewx.LyricEntry;
import com.dirror.lyricviewx.LyricViewX;
import com.google.android.material.slider.Slider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqingbfq.databinding.ActivitySzBinding;
import com.muqingbfq.databinding.ActivitySzSetlrcBinding;
import com.muqingbfq.mq.AppCompatActivity;
import com.muqingbfq.mq.FloatingLyricsService;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;

import java.io.File;
import java.lang.reflect.Type;

public class sz extends AppCompatActivity<ActivitySzBinding> {
    @Override
    protected ActivitySzBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivitySzBinding.inflate(layoutInflater);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView();
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.sz));
        SharedPreferences theme = getSharedPreferences("theme", MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor edit = theme.edit();
        int i = theme.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (i == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            binding.switchA1.setChecked(true);
            binding.switchA2.setEnabled(false);
        } else {
            binding.switchA1.setChecked(false);
            binding.switchA2.setEnabled(true);
            binding.switchA2.setChecked(i == AppCompatDelegate.MODE_NIGHT_YES);
        }
        binding.switchA1.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
// 跟随系统设置切换颜色模式
                int ms = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                AppCompatDelegate.setDefaultNightMode(ms);
                edit.putInt("theme", ms);
                edit.apply();
            }
            binding.switchA2.setEnabled(!b);
        });
        binding.switchA2.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isEnabled()) {
                int ms;
                if (b) {
                    ms = AppCompatDelegate.MODE_NIGHT_YES;
                } else {
                    ms = AppCompatDelegate.MODE_NIGHT_NO;
                }
                AppCompatDelegate.setDefaultNightMode(ms);
                edit.putInt("theme", ms);
                edit.commit();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class setlrc extends Fragment implements Slider.OnSliderTouchListener, Slider.OnChangeListener {
        ActivitySzSetlrcBinding binding;
        ActivityResultLauncher<Intent> LyricsService = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (Settings.canDrawOverlays(getContext())) {
//                getContext().startService(new Intent(getContext(), FloatingLyricsService.class));
                binding.switchA3.setChecked(true);
                binding.slide1.setEnabled(true);
            } else {
                binding.switchA3.setChecked(false);
                binding.slide1.setEnabled(false);
            }
            UI();
        });
        FloatingLyricsService.SETUP setup;

        Handler handler = new Handler();

        private Runnable ThreadLrc = new Runnable() {
            @Override
            public void run() {
                if (PlaybackService.mediaSession == null) {
                    handler.postDelayed(this, 1000);
                    return;
                }
                Player player = PlaybackService.mediaSession.getPlayer();
                int index = 0;
                for (int i = 0; i < LyricViewX.lyricEntryList.size(); i++) {
                    LyricEntry lineLrc = LyricViewX.lyricEntryList.get(i);
                    gj.sc(player.getCurrentPosition());
                    if (lineLrc.time <= player.getCurrentPosition()) {
                        index = i;
                    } else {
                        break;
                    }
                }
                if (index < LyricViewX.lyricEntryList.size()) {
                    LyricEntry currentLrc = LyricViewX.lyricEntryList.get(index);
                    if (currentLrc.secondText != null) {
                        binding.lrcViewMessage.setText(currentLrc.secondText);
                    } else {
                        binding.lrcViewMessage.setText("");
                    }
                    binding.lrcView.setText(currentLrc.text);
                }
                handler.postDelayed(this, 1000);
            }
        };

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = ActivitySzSetlrcBinding.inflate(inflater, container, false);
            UI();
            return binding.getRoot();
        }

        private void UI() {
            File file = new File(wj.filesdri + "FloatingLyricsService.json");
            if (file.exists() && file.isFile()) {
                String dqwb = wj.dqwb(file.toString());
                Gson gson = new Gson();
                Type type = new TypeToken<FloatingLyricsService.SETUP>() {
                }.getType();

                binding.slide1.setEnabled(true);
                setup = gson.fromJson(dqwb, type);
                binding.slide1.setValue(setup.size);

                binding.lrcView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, setup.size, getResources().getDisplayMetrics()));
                binding.lrcView.setTextColor(Color.parseColor(setup.Color));

                binding.lrcViewMessage.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, setup.size, getResources().getDisplayMetrics()) - 1.0f);
                binding.lrcViewMessage.setTextColor(Color.parseColor(setup.Color));
                binding.lrclin.setOnClickListener(view -> ColorPickerDialogBuilder.with(view.getContext()).setTitle("调色盘").initialColor(Color.parseColor(setup.Color)).wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(6).setOnColorSelectedListener(selectedColor -> {
                }).setPositiveButton("确定", (dialog, selectedColor, allColors) -> {
                    setup.Color = String.format("#%08X", selectedColor);
                    binding.lrcView.setTextColor(selectedColor);
                    binding.lrcViewMessage.setTextColor(selectedColor);
                    FloatingLyricsService.baocun(setup);
                }).setNegativeButton("取消", null).build().show());
                binding.textSlide1.setText(String.valueOf(setup.size));
                if (setup.i != 0) {
                    binding.switchA3.setChecked(true);
                }
                binding.lock.setVisibility(View.VISIBLE);
                binding.lock.setImageResource(setup.i == 2 ? R.drawable.lock : R.drawable.lock_open);
                handler.post(ThreadLrc);
            } else {
                binding.lock.setVisibility(View.GONE);
            }
            binding.switchA3.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    if (setup != null) {
                        setup.i = 1;
                    }
                    if (!Settings.canDrawOverlays(getContext())) {
                        // 无权限，需要申请权限
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
                        LyricsService.launch(intent);
                    } else {
                        getContext().startService(new Intent(getContext(), FloatingLyricsService.class));
                    }
                } else {
                    if (setup != null) {
                        setup.i = 0;
                    }
                    main.application.stopService(new Intent(main.application, FloatingLyricsService.class));
                }
                FloatingLyricsService.baocun(setup);
            });
            //强制跳转到权限界面
            binding.switchA3.setOnLongClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
                LyricsService.launch(intent);
                return false;
            });
            binding.slide1.setLabelFormatter(value -> String.valueOf((int) value));
            binding.slide1.addOnChangeListener(this);
            binding.slide1.addOnChangeListener(this);
            binding.slide1.addOnSliderTouchListener(this);
            binding.lock.setOnClickListener(view -> {
                if (setup.i == 1) {
                    setup.i = 2;
                    binding.lock.setImageResource(R.drawable.lock);
                } else if (setup.i == 2) {
                    setup.i = 1;
                    binding.lock.setImageResource(R.drawable.lock_open);
                }
                FloatingLyricsService.baocun(setup);
            });
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(ThreadLrc);
        }

        @Override
        public void onStartTrackingTouch(@NonNull Slider slider) {

        }

        @Override
        public void onStopTrackingTouch(@NonNull Slider slider) {
            if (setup == null) {
                return;
            }
            FloatingLyricsService.baocun(setup);
        }

        @Override
        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
            if (setup == null) {
                return;
            }
            if (slider == binding.slide1) {
                setup.size = (int) value;
                float v = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, setup.size, getResources().getDisplayMetrics());
                binding.lrcView.setTextSize(v);
                binding.lrcViewMessage.setTextSize(v - 1.0f);
                binding.textSlide1.setText(String.valueOf(setup.size));
            }
        }
    }

}