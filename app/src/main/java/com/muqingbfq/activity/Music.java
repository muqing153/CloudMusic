package com.muqingbfq.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.Util;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.muqingbfq.MP3;
import com.muqingbfq.PlaybackService;
import com.muqingbfq.R;
import com.muqingbfq.bfq_an;
import com.muqingbfq.databinding.ActivityMusicBinding;
import com.muqingbfq.main;
import com.muqingbfq.mq.AppCompatActivity;
import com.muqingbfq.mq.gj;


public class Music extends AppCompatActivity<ActivityMusicBinding> {

    private Player player = PlaybackService.mediaSession.getPlayer();
    private int TdtHeight = 15;

    public static void startActivity(Context context, MP3 mp3) {
        Intent intent = new Intent(context, Music.class);
        intent.putExtra("MP3", mp3);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, Music.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        binding.kg.setOnClickListener(view -> Util.handlePlayPauseButtonAction(player));
        binding.xyq.setOnClickListener(v -> {
            boolean b = player.hasNextMediaItem();
            if (b) {
                player.seekToNextMediaItem();
            } else {
                gj.ts(v.getContext(), "已经是最后一首了");
            }
        });
        binding.syq.setOnClickListener(v -> {
            boolean b = player.hasPreviousMediaItem();
            if (b) {
                player.seekToPreviousMediaItem();
            } else {
                gj.ts(v.getContext(), "已经是第一首了");
            }
        });
        player.addListener(Listener);
        if (PlaybackService.mediaSession != null) {
            updateUI(player);
        }
        binding.tdt.post(() -> TdtHeight = binding.tdt.getHeight());
        binding.tdt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long actualPosition = (progress * player.getDuration()) / 100;
                String time = bfq_an.getTime(actualPosition);
                binding.timeB.setText(time);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 获取 View 当前的高度;
                // 创建一个 ValueAnimator，从当前高度逐渐增加到目标高度
                ValueAnimator animator = ValueAnimator.ofInt(TdtHeight, TdtHeight + 10);
                animator.setDuration(300); // 设置动画持续时间为 300 毫秒
                animator.setInterpolator(new DecelerateInterpolator()); // 设置动画插值器
                // 在动画过程中更新 View 的高度
                animator.addUpdateListener(animation -> {
                    // 获取当前动画的值（高度）
                    int animatedValue = (int) animation.getAnimatedValue();

                    // 更新 View 的高度
                    ViewGroup.LayoutParams layoutParams = seekBar.getLayoutParams();
                    layoutParams.height = animatedValue;
                    seekBar.setLayoutParams(layoutParams);
                });

                // 开始动画
                animator.start();
                isDrag = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDrag = false;
                long actualPosition = (seekBar.getProgress() * player.getDuration()) / 100;
                player.seekTo(actualPosition);

                ValueAnimator animator = ValueAnimator.ofInt(TdtHeight, TdtHeight - 10);
                animator.setDuration(300); // 设置动画持续时间为 300 毫秒
                animator.setInterpolator(new DecelerateInterpolator()); // 设置动画插值器
                // 在动画过程中更新 View 的高度
                animator.addUpdateListener(animation -> {
                    // 获取当前动画的值（高度）
                    int animatedValue = (int) animation.getAnimatedValue();
                    // 更新 View 的高度
                    ViewGroup.LayoutParams layoutParams = seekBar.getLayoutParams();
                    layoutParams.height = animatedValue;
                    seekBar.setLayoutParams(layoutParams);
                });

                // 开始动画
                animator.start();
            }
        });
        binding.back.setOnClickListener(v -> finish());
    }

    //是否拖动
    private boolean isDrag = false;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (player != null && !isDrag) {
                // 获取当前进度和持续时间
                long currentPosition = player.getCurrentPosition();
                long duration = player.getDuration();

                // 更新进度条（假设有一个 SeekBar 名为 seekBar）
                if (duration > 0) {
                    int progress = (int) ((currentPosition * 100) / duration);
//                    gj.sc(progress);
                    if (progress < 1) {
                        progress = 1;
                    }
                    binding.tdt.setProgress(progress);
//                    binding.tdt.setMax(100);
                    binding.timeA.setText(bfq_an.getTime(duration));
                    binding.timeB.setText(bfq_an.getTime(currentPosition));
                }
            }
            // 计划下一次更新
            main.handler.postDelayed(this, 1000); // 每秒更新一次
        }
    };

    @Override
    public void finish() {
        super.finish();
        player.removeListener(Listener);
    }

    private Player.Listener Listener = new Player.Listener() {
        @Override
        public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
            // 监听播放状态变化
            if (events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED)) {
                boolean isPlaying = player.getPlayWhenReady();
                if (isPlaying) {
                    gj.sc("正在播放");
                } else {
                    gj.sc("播放暂停");
                }
            }

            // 监听下一曲事件
            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                MediaItem currentMediaItem = player.getCurrentMediaItem();
                if (currentMediaItem != null) {
                    String title = currentMediaItem.mediaMetadata.title != null
                            ? currentMediaItem.mediaMetadata.title.toString()
                            : "未知曲目";
                    gj.sc("播放下一曲: " + title);
                }
            }

            // 监听上一曲事件（通常通过手动调用控制播放器的skipToPrevious方法实现）
            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                // 你的逻辑代码，处理上一曲
                gj.sc("播放上一曲");
            }

            if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
                int playbackState = player.getPlaybackState();

                switch (playbackState) {
                    case Player.STATE_READY:
                        if (player.getPlayWhenReady()) {
                            gj.sc("播放开始");
                        }
                        break;
                    case Player.STATE_ENDED:
                        gj.sc("播放结束");
                        break;
                    default:
                        // 处理其他状态
                        break;
                }
            }
            updateUI(player);

        }
    };

    @Override
    protected ActivityMusicBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityMusicBinding.inflate(layoutInflater);
    }

    @Override
    protected void onResume() {
        super.onResume();
        main.handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        main.handler.removeCallbacks(runnable);
    }

    private void updateUI(Player player) {

        boolean shouldShowPlayButton = Util.shouldShowPlayButton(player);
        binding.kg.setImageResource(shouldShowPlayButton ? R.drawable.zt : R.drawable.bf);
        // 获取当前播放的 MediaItem
        MediaItem currentMediaItem = player.getCurrentMediaItem();
        if (currentMediaItem != null) {
            MediaMetadata metadata = currentMediaItem.mediaMetadata;
            String title = metadata.title != null ? metadata.title.toString() : "没有名字的音乐？";
            String artist = metadata.artist != null ? metadata.artist.toString() : "未知艺术家";
            binding.name.setText(title);
            binding.zz.setText(artist);
            Glide.with(this)
                    .asBitmap()
                    .load(metadata.artworkUri)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground))
                    .addListener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(@NonNull Bitmap resource,
                                                       @NonNull Object model, Target<Bitmap> target,
                                                       @NonNull DataSource dataSource,
                                                       boolean isFirstResource) {
                            color(resource);
                            binding.cardview.imageView.setImageBitmap(resource);
                            return true;
                        }
                    }).into(binding.cardview.imageView);
        }

    }


    private void color(Bitmap bitmap) {
        Palette.Builder builder = new Palette.Builder(bitmap);
        builder.generate(palette -> {
//                    获取图片中柔和的亮色
            int lightMutedColor = palette.getLightMutedColor(Color.GRAY);
            Palette.Swatch vibrantSwatch = palette.getLightVibrantSwatch();
            if (vibrantSwatch != null) {
                int bodyTextColor = vibrantSwatch.getBodyTextColor();
                binding.lrcView.setCurrentColor(bodyTextColor);
                binding.lrcView.setTimelineTextColor(bodyTextColor);
// 计算半亮度的颜色（直接将RGB分量除以2并向下取整）
                int halfBrightnessColor = (bodyTextColor & 0x00FFFFFF) / 2;
                binding.lrcView.setNormalColor(halfBrightnessColor);

                GradientDrawable gradientDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,      // 渐变方向：从上到下
                        new int[]{vibrantSwatch.getRgb(), lightMutedColor}  // 渐变颜色数组
                );
                setTint(vibrantSwatch.getTitleTextColor());
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                binding.getRoot().setBackground(gradientDrawable);
            } else {
                int color = palette.getLightVibrantColor(Color.WHITE);
                int titleTextColor = palette.getVibrantColor(Color.GRAY);
                binding.lrcView.setCurrentColor(titleTextColor);
                binding.lrcView.setTimelineTextColor(titleTextColor);
// 计算半亮度的颜色（直接将RGB分量除以2并向下取整）
                int halfBrightnessColor = (titleTextColor & 0x00FFFFFF) / 2;
                binding.lrcView.setNormalColor(halfBrightnessColor);

                GradientDrawable gradientDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,      // 渐变方向：从上到下
                        new int[]{color, lightMutedColor}  // 渐变颜色数组
                );
                setTint(titleTextColor);
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                binding.getRoot().setBackground(gradientDrawable);
            }
        });
    }

    private void setTint(int color) {
//        this.ColorTint = color;
        ColorStateList colorStateList = ColorStateList.valueOf(color);
        binding.kg.setImageTintList(colorStateList);
        binding.syq.setImageTintList(colorStateList);
        binding.xyq.setImageTintList(colorStateList);
        binding.bfqListMp3.setImageTintList(colorStateList);
        binding.control.setImageTintList(colorStateList);
        binding.like.setImageTintList(colorStateList);
        binding.download.setImageTintList(colorStateList);
        binding.image2.setImageTintList(colorStateList);
        binding.name.setTextColor(color);
        binding.zz.setTextColor(color);
        binding.timeA.setTextColor(color);
        binding.timeB.setTextColor(color);

        Drawable progressDrawable = binding.tdt.getProgressDrawable();
        LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;
        Drawable progress = layerDrawable.findDrawableByLayerId(android.R.id.progress);
        progress.setColorFilter(color, PorterDuff.Mode.SRC_IN);
// 设置进度条背景的颜色
/*        Drawable background = layerDrawable.findDrawableByLayerId(android.R.id.background);
        background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);*/
    }

    //触摸

}
