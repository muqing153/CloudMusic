package com.muqingbfq.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.muqingbfq.MP3;
import com.muqingbfq.R;
import com.muqingbfq.bfq;
import com.muqingbfq.bfq_an;
import com.muqingbfq.bfqkz;
import com.muqingbfq.databinding.FragmentBfqDbBinding;

import java.util.Objects;

public class bfq_db extends Fragment implements GestureDetector.OnGestureListener {
    FragmentBfqDbBinding binding;
    private GestureDetector gestureDetector;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBfqDbBinding.inflate(inflater, container, false);
        // 获取当前活动的主题
        binding.kg.setOnClickListener(v -> {
            if (bfqkz.mt.isPlaying()) {
                bfqkz.mt.pause();
            } else {
                bfqkz.mt.start();
            }
            setkg(bfqkz.mt.isPlaying());
        });
        binding.txb.setOnClickListener(view -> bflb_db.start(getContext()));
        gestureDetector = new GestureDetector(getContext(), this);
        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            gestureDetector.onTouchEvent(motionEvent);
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                binding.linearLayout.setTranslationX(0);
//                binding.getRoot().setAlpha(1.0f);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                binding.getRoot().setAlpha(0.2f);
            }
            return false;
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(runnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    MP3 mp3;
    boolean isPlaying = false, isvisible = false;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!Objects.equals(mp3, bfqkz.xm)) {
                mp3 = bfqkz.xm;
                setname(mp3.name, " - " + mp3.zz);
            }
            if (bfqkz.mt.isPlaying() != isPlaying) {
                setkg(bfqkz.mt.isPlaying());
            }
            if (isvisible != bfqkz.list.isEmpty()) {
                isvisible = bfqkz.list.isEmpty();
                if (isvisible) {
                    binding.getRoot().setVisibility(View.GONE);
                } else {
                    binding.getRoot().setVisibility(View.VISIBLE);
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    public void setkg(boolean bool) {
        if (bool) {
            binding.kg.setImageResource(R.drawable.bf);
        } else {
            binding.kg.setImageResource(R.drawable.zt);
        }
        isPlaying = bool;
    }

    public void setname(String a,String b) {
        binding.textview1.setText(a);
        binding.textview2.setText(b);
    }

    @Override
    public boolean onDown(@NonNull MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
        bfq.startactivity(getContext(), bfqkz.xm);
        return true;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent motionEvent, @NonNull MotionEvent motionEvent1,
                            float v, float v1) {
        binding.linearLayout.setTranslationX(binding.linearLayout.getTranslationX() - v);
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1,
                           @NonNull MotionEvent e2, float v, float v1) {
        float distance = e1.getX() - e2.getX();
        float threshold = getResources().getDisplayMetrics().widthPixels / 2.0f;
        // 判断手势方向并限制滑动距离
        if (distance > threshold) {
            // 向左滑动
            // 在这里添加你的逻辑代码
            bfq_an.xyq();
        } else if (distance < -threshold) {
            // 向右滑动
            // 在这里添加你的逻辑代码
            bfq_an.syq();
        }
        return true;
    }
}