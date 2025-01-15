package com.muqingbfq.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.Util;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.muqingbfq.PlaybackService;
import com.muqingbfq.R;
import com.muqingbfq.activity.Music;
import com.muqingbfq.bfq_an;
import com.muqingbfq.databinding.FragmentBfqDbBinding;
import com.muqingbfq.main;
import com.muqingbfq.mq.Fragment;
import com.muqingbfq.mq.gj;

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
            }
            return false;

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        main.handler.post(runnable); // 恢复监听或更新UI
    }

    @Override
    public void onPause() {
        super.onPause();
        main.handler.removeCallbacks(runnable);
        if (PlaybackService.mediaSession != null) {
            PlaybackService.mediaSession.getPlayer().removeListener(playerListener); // 移除监听器
        }
    }

    private void setUI(Player player) {

        binding.kg.setImageResource(player.isPlaying() ? R.drawable.bf : R.drawable.zt);

        MediaItem currentMediaItem = player.getCurrentMediaItem();
        if (currentMediaItem != null) {
            MediaMetadata metadata = currentMediaItem.mediaMetadata;
            String title = metadata.title != null ? metadata.title.toString() : "没有名字的音乐？";
            String artist = metadata.artist != null ? metadata.artist.toString() : "未知艺术家";
            binding.textview1.setText(title);
            binding.textview2.setText(artist);
            if (player.getMediaItemCount() > 0) {
                binding.getRoot().setVisibility(View.VISIBLE);
            } else {
                binding.getRoot().setVisibility(View.GONE);
            }
        } else {
            binding.getRoot().setVisibility(View.GONE);
        }
    }

    private final Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
//            boolean shouldShowPlayButton = Util.shouldShowPlayButton(player);
//            gj.sc("播放状态" + shouldShowPlayButton);
            setUI(player);
        }
    };

    private void setPlay() {
        if (PlaybackService.mediaSession != null) {
            PlaybackService.mediaSession.getPlayer().addListener(playerListener);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (PlaybackService.mediaSession != null) {
                setPlay();
                setUI(PlaybackService.mediaSession.getPlayer());
                main.handler.removeCallbacks(this);
            } else {
                main.handler.postDelayed(this, 1000);
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
        Music.startActivity(getContext());
        return true;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent motionEvent, @NonNull MotionEvent motionEvent1,
                            float v, float v1) {
        if (PlaybackService.mediaSession == null) {
            return false;
        }
        Player player = PlaybackService.mediaSession.getPlayer();
        if (player.getMediaItemCount() == 0) {
            //如果只有一首曲子
            return false;
        }
        if (player.getCurrentMediaItemIndex() == 0) {
            //如果没有上一曲
            if (v < 0) {
                v = 0;
            }
        }else if (player.getCurrentMediaItemIndex() == player.getMediaItemCount() - 1) {
            //如果没有下一曲
            if (v > 0) {
                v = 0;
            }
        }
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

        if (PlaybackService.mediaSession == null) {
            return false;
        }
        Player player = PlaybackService.mediaSession.getPlayer();
        // 判断手势方向并限制滑动距离
        if (distance > threshold) {
            // 向左滑动 下一曲
            player.seekToNextMediaItem();
        } else if (distance < -threshold) {
            // 向右滑动 上一曲
            player.seekToPreviousMediaItem();
        }
        return true;
    }
}