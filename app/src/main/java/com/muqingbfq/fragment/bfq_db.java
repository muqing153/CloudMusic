package com.muqingbfq.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.Util;

import com.muqingbfq.MP3;
import com.muqingbfq.PlaybackService;
import com.muqingbfq.R;
import com.muqingbfq.activity.Music;
import com.muqingbfq.bfq;
import com.muqingbfq.bfq_an;
import com.muqingbfq.bfqkz;
import com.muqingbfq.databinding.FragmentBfqDbBinding;
import com.muqingbfq.mq.Fragment;
import com.muqingbfq.mq.gj;

import java.util.Objects;

public class bfq_db extends Fragment<FragmentBfqDbBinding> implements GestureDetector.OnGestureListener {
    private GestureDetector gestureDetector;

    @Override
    protected FragmentBfqDbBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentBfqDbBinding.inflate(inflater, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setUI(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 获取当前活动的主题
        binding.txb.setOnClickListener(view -> bflb_db.start(getContext()));
        gestureDetector = new GestureDetector(getContext(), this);
        binding.kg.setOnClickListener(view -> {
            if (PlaybackService.mediaSession != null) {
                Util.handlePlayPauseButtonAction(PlaybackService.mediaSession.getPlayer());
            }
        });
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
    }


    private void setPlay() {
        PlaybackService.mediaSession.getPlayer().addListener(new Player.Listener() {
            @Override
            public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
                Player.Listener.super.onEvents(player, events);
                boolean shouldShowPlayButton = Util.shouldShowPlayButton(player);
                gj.sc("播放状态" + shouldShowPlayButton);
                binding.kg.setImageResource(shouldShowPlayButton ? R.drawable.zt : R.drawable.bf);

                // 获取当前播放的 MediaItem
                MediaItem currentMediaItem = player.getCurrentMediaItem();
                if (currentMediaItem != null) {

                    MediaMetadata metadata = currentMediaItem.mediaMetadata;
                    String title = metadata.title != null ? metadata.title.toString() : "没有名字的音乐？";
                    String artist = metadata.artist != null ? metadata.artist.toString() : "未知艺术家";
                    binding.textview1.setText(title);
                    binding.textview2.setText(artist);

                    binding.getRoot().setVisibility(View.VISIBLE);
                } else {
                    binding.getRoot().setVisibility(View.GONE);
                }

            }
        });
    }

    MP3 mp3;
    boolean isPlaying = false;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (PlaybackService.mediaSession != null) {
                setPlay();
                handler.removeCallbacks(this);
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public boolean onDown(@NonNull MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
//        bfq.startactivity(getContext(), bfqkz.xm);
        Intent intent = new Intent(getContext(), Music.class);
        intent.putExtra("mp3", bfqkz.xm);
        getContext().startActivity(intent);
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