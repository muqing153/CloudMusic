package com.muqingbfq.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.muqing.Fragment;
import com.muqing.databinding.ViewSeetingSwitchtBinding;
import com.muqingbfq.PlaybackService;
import com.muqingbfq.R;
import com.muqingbfq.activity.Music;
import com.muqingbfq.databinding.FragmentBfqDbBinding;
import com.muqingbfq.databinding.ViewBottommusicBinding;

public class BottomMusic extends BottomNavigationView implements GestureDetector.OnGestureListener {
    private GestureDetector gestureDetector;
    private Context context;

    public BottomMusic(@NonNull Context context) {
        super(context);
        this.context = context;
        Init();
    }

    public BottomMusic(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        Init();
    }

    public BottomMusic(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        Init();
    }

    private void Init() {
//        setUI();
    }

    ViewBottommusicBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    public void setUI() {
        View inflate = LayoutInflater.from(context).inflate(R.layout.view_bottommusic, this);
        binding = ViewBottommusicBinding.bind(inflate);
        // 获取当前活动的主题
        binding.txb.setOnClickListener(view -> bflb_db.start(getContext()));
        gestureDetector = new GestureDetector(getContext(), this);
        binding.kg.setOnClickListener(view -> {
            if (PlaybackService.mediaSession != null) {
                Util.handlePlayPauseButtonAction(PlaybackService.mediaSession.getPlayer());
            }
        });
        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
//            gj.sc("手势");
            gestureDetector.onTouchEvent(motionEvent);
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                binding.linearLayout.setTranslationX(0);
//                binding.getRoot().setAlpha(1.0f);
            }
            return false;
        });
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (PlaybackService.mediaSession != null) {
//            PlaybackService.mediaSession.getPlayer().addListener(playerListener);
//            setUI(PlaybackService.mediaSession.getPlayer());
//        }
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if (PlaybackService.mediaSession != null) {
//            PlaybackService.mediaSession.getPlayer().removeListener(playerListener); // 移除监听器
//        }
//    }

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
            if (binding.linearLayout.getTranslationX() - v >= 0) {
                v = 0;
            }
        } else if (player.getCurrentMediaItemIndex() == player.getMediaItemCount() - 1) {
            //如果没有下一曲
            if (binding.linearLayout.getTranslationX() - v <= 0) {
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
        float distance = 0;
        if (e1 != null) {
            distance = e1.getX() - e2.getX();
        }
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