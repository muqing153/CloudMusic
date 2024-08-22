package com.muqingbfq;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jaeger.library.StatusBarUtil;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.muqingbfq.databinding.ActivityBfqBinding;
import com.muqingbfq.fragment.Media;
import com.muqingbfq.mq.AppCompatActivity;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;

import java.util.Objects;

public class bfq extends AppCompatActivity<ActivityBfqBinding>
        implements GestureDetector.OnGestureListener {
    public String lrc;
    public MP3 mp3;
    public boolean isplay = true;
    GestureDetector gestureDetector;
    int seekbarH = 0;

    private void lrc(View v) {
        // 隐藏view2并显示view1的动画效果
        v.animate()
                .alpha(0.0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(View.GONE);
                        binding.cardview.setVisibility(View.VISIBLE);
                        binding.cardview.setAlpha(0.0f);
                        binding.cardview.animate().alpha(1.0f).setDuration(500).setListener(null);
                    }
                });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setLrc() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        ViewGroup.LayoutParams layoutParams = binding.cardview.getLayoutParams();
        if (!gj.isTablet(this)) {
            layoutParams.height = (int) (dm.widthPixels / 1.3f);
            layoutParams.width = (int) (dm.widthPixels / 1.3f);
            binding.lrcView.setOnClickListener(this::lrc);

            binding.lrcView.setOnSingerClickListener(() -> lrc(binding.lrcView));
//            binding.lrcView.setTextGravity(GRAVITY_LEFT)
        } else {
            layoutParams.height = (int) (dm.heightPixels / 2.0f);
            layoutParams.width = (int) (dm.heightPixels / 2.0f);
        }
        binding.lrcView.setNormalTextSize(80f);
        binding.lrcView.setCurrentTextSize(100f);
        binding.lrcView.setTranslateTextScaleValue(0.8f);
        binding.lrcView.setHorizontalOffset(-50f);
        binding.lrcView.setHorizontalOffsetPercent(0.5f);
        binding.lrcView.setItemOffsetPercent(0.5f);
        binding.lrcView.setIsDrawTranslation(true);
        binding.lrcView.setIsEnableBlurEffect(true);
        binding.cardview.setLayoutParams(layoutParams);

        binding.lrcView.setDraggable(true, time -> {
            bfqkz.mt.seekTo((int) time);
            return false;
        });
        binding.tdt.setThumb(null);
        binding.tdt.post(() -> seekbarH = binding.tdt.getHeight());
        binding.tdt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当进度发生变化时执行操作
                setTime_b(bfq_an.getTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 当开始拖动滑块时执行操作
                // 创建 ValueAnimator 对象，实现进度条高度的平滑过渡
                ValueAnimator animator = ValueAnimator.ofInt(seekbarH,
                        seekbarH + 30);
                animator.addUpdateListener(animation -> {
                    seekBar.getLayoutParams().height = (int) animation.getAnimatedValue();
                    seekBar.requestLayout();
                });
                animator.setDuration(200); // 设置动画持续时间为 200 毫秒
                animator.start(); // 开始执行动画
                isplay = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 当停止拖动滑块时执行操作
                isplay = true;
                // 创建 ValueAnimator 对象，实现恢复进度条高度的平滑过渡
                ValueAnimator animator = ValueAnimator.ofInt(seekbarH + 30,
                        seekbarH);
                animator.addUpdateListener(animation -> {
                    seekBar.getLayoutParams().height = (int) animation.getAnimatedValue();
                    seekBar.requestLayout();
                });
                animator.setDuration(200); // 设置动画持续时间为 200 毫秒
                animator.start(); // 开始执行动画
                bfqkz.mt.seekTo(seekBar.getProgress());
            }
        });
    }

    public static void startactivity(Context context, MP3 mp3) {
        gj.sc(mp3.toString());
        Intent intent = new Intent(context, bfq.class);
        intent.putExtra("MP3", mp3);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    ObjectAnimator rotateAnimation;

    private void Animation() {
        if (bfqkz.mt.isPlaying()) {
            if (rotateAnimation.isPaused()) {
                rotateAnimation.resume();
            } else {
                rotateAnimation.start();
            }
        } else {
            rotateAnimation.pause();
        }
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparent(this);
        setContentView();
//        gestureDetector = new GestureDetector(this, this);
//        setLrc();
//        rotateAnimation = ObjectAnimator.ofFloat(binding.cardview
//                , "rotation", 0f, 360f);
//        rotateAnimation.setDuration(30000); // 设置动画持续时间，单位为毫秒
//        rotateAnimation.setRepeatCount(ObjectAnimator.INFINITE); // 设置重复次数为无限
//        rotateAnimation.setInterpolator(new LinearInterpolator()); // 设置插值器，这里使用线性插值器
//        rotateAnimation.start();
//        binding.kg.setOnClickListener(v -> {
//            if (bfqkz.mt.isPlaying()) {
//                bfqkz.mt.pause();
//                setbf(false);
//            } else {
//                bfqkz.mt.start();
//                setbf(true);
//            }
//            Animation();
//        });
//        binding.xyq.setOnClickListener(v -> bfq_an.xyq());
//        binding.syq.setOnClickListener(v -> bfq_an.syq());
//
//        binding.image1.setOnClickListener(new toolbar());
//        binding.image2.setOnClickListener(new toolbar());
//
//        binding.bfqListMp3.
//                setOnClickListener(view1 -> com.muqingbfq.fragment.bflb_db.start(this));
//        binding.control.setOnClickListener(new bfq_an.control(binding.control));
//
//        binding.like.setOnClickListener(view1 -> {
//            try {
//                Gson gson = new Gson();
//                Type type = new TypeToken<List<MP3>>() {
//                }.getType();
//                List<MP3> list = gson.fromJson(wj.dqwb(wj.gd + "mp3_like.json"), type);
//                if (list == null) {
//                    list = new ArrayList<>();
//                }
//                if (bfqkz.like_bool) {
//                    list.remove(bfqkz.xm);
//                    setlike(false);
//                } else {
//                    if (!list.contains(bfqkz.xm)) {
//                        list.add(bfqkz.xm);
//                        setlike(true);
//                    }
//                }
//                bfqkz.like_bool = !bfqkz.like_bool;
//                wj.xrwb(wj.gd + "mp3_like.json", gson.toJson(list));
//            } catch (Exception e) {
//                gj.sc(e);
//            }
//        });
//        binding.download.setOnClickListener(view -> {
//            if (wj.cz(wj.mp3 + bfqkz.xm.id)) {
//                gj.ts(this, "你已经下载过这首歌曲了");
//                return;
//            }
//            if (bfqkz.xm != null) {
//                new FileDownloader(bfq.this).downloadFile(bfqkz.xm);
//            }
//        });
//        Intent intent = getIntent();
//        mp3 = (MP3) intent.getSerializableExtra("MP3");
//        new thread().start();
//        binding.fragmentBfq.setOnTouchListener((v, event) -> {
//            gestureDetector.onTouchEvent(event);
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                if (binding.getRoot().getRootView().getTranslationY() > (getResources().getDisplayMetrics().heightPixels / 2.0f)) {
//                    finish();
//                    return true;
//                }
//                ObjectAnimator animator = ObjectAnimator.ofFloat(binding.getRoot().getRootView()
//                        , "y", binding.getRoot().getRootView().getTranslationY(), 0);
//                animator.setDuration(500);
//                animator.start();
//            }
//            return true;
//        });

    }

    private class toolbar implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image1) {
                finish();
            } else if (v.getId() == R.id.image2) {
                com.muqingbfq.mq.gj.fx(v.getContext(),
                        "音乐名称：" + mp3.name +
                                "\n 作者：" + mp3.zz +
                                "\n 链接：https://music.163.com/#/song?id=" + mp3.id);
            }
        }
    }

    class thread extends Thread {
        @Override
        public void run() {
            super.run();
            if (mp3 != null) {
                if (bfqkz.xm == null || !bfqkz.xm.equals(mp3)) {
                    bfqkz.xm = mp3;
                    bfqkz.mp3(com.muqingbfq.api.url.hq(mp3));
                }
            }
            if (binding == null) {
                return;
            }
            main.handler.post(() -> {
                if (mp3 != null) {
                    sx();
                }
                setbf(bfqkz.mt.isPlaying());
                Animation();
            });
//            main.handler.post(runnable);
        }
    }

    public void sx() {
        setname(mp3.name);
        setzz(mp3.zz);
        bfq_an.islike();
        int duration = bfqkz.mt.getDuration();
        setMax(duration);
        gj.sc(duration);
        setTime_a(bfq_an.getTime(duration));
        int position = bfqkz.mt.getCurrentPosition();
        Progress(position);
        setImageBitmap();
    }

    public void setname(String str) {
        binding.name.setText(str);
    }

    public void setzz(String str) {
        binding.zz.setText(str);
    }

    public void kgsetImageResource(int a) {
        if (binding == null) {
            return;
        }
        binding.kg.setImageResource(a);
    }

    @Override
    protected ActivityBfqBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityBfqBinding.inflate(layoutInflater);
    }

    public void setlike(boolean bool) {
        if (bool) {
            binding.like.setImageTintList(ContextCompat.
                    getColorStateList(binding.getRoot().getContext(), android.R.color.holo_red_dark));
        } else {
            binding.like.setImageTintList(ColorStateList.valueOf(ColorTint));
        }
        islike = bool;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isplay) {
                int position = bfqkz.mt.getCurrentPosition();
                Progress(position);
            }
            if (mp3 != null && !mp3.equals(bfqkz.xm) && binding != null) {
                mp3 = bfqkz.xm;
                setname(mp3.name);
                setzz(mp3.zz);
                bfq_an.islike();
                int duration = bfqkz.mt.getDuration();
                setMax(duration);
                gj.sc(duration);
                setTime_a(bfq_an.getTime(duration));
                int position = bfqkz.mt.getCurrentPosition();
                Progress(position);
                setImageBitmap();
            }
            if (bfqkz.mt.isPlaying() != isPlaying) {

                setbf(bfqkz.mt.isPlaying());
            }
            if (bfqkz.like_bool != islike) {
                setlike(bfqkz.like_bool);
            }
            if (!Objects.equals(bfqkz.lrc, lrc)) {
                lrc = bfqkz.lrc;
                String[] strings = Media.loadLyric();
                binding.lrcView.loadLyric(strings[0], strings[1]);
            }
            main.handler.postDelayed(this, 1000); // 每秒更新一次进度
        }
    };

    public boolean islike = false;
    public boolean isPlaying = false;

    public void setbf(boolean bool) {
        if (bool) {
            //开始
            kgsetImageResource(R.drawable.bf);
        } else {
            //暂停
            kgsetImageResource(R.drawable.zt);
        }
        isPlaying = bool;
    }

    public void setImageBitmap() {
        if (binding == null) {
            return;
        }
        if (wj.cz(bfqkz.xm.picurl)) {
            try {
                Mp3File mp3file = new Mp3File(bfqkz.xm.picurl);
                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                    byte[] albumImage = id3v2Tag.getAlbumImage();
                    Bitmap bitmap = BitmapFactory.
                            decodeByteArray(albumImage, 0, albumImage.length);
                    binding.cardview.imageView.setImageBitmap(bitmap);
                    color(bitmap);
                }
                return;
            } catch (Exception a) {
                gj.sc(getClass() + " yc:" + a);
            }
        }
        if (!isFinishing()) {
            Glide.with(this)
                    .asBitmap()
                    .load(mp3.picurl)
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

    private int ColorTint = Color.WHITE;

    private void setTint(int color) {
        this.ColorTint = color;
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

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        main.handler.post(runnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        main.handler.removeCallbacks(runnable);
    }

    public void setTime_a(String str) {
        binding.timeA.setText(str);
    }

    public void setTime_b(String str) {
        binding.timeB.setText(str);
    }

    public void setMax(int max) {
        binding.tdt.setMax(Math.max(0, max));
    }

    public void Progress(int progress) {
        int min = Math.min(progress, binding.tdt.getMax());
        binding.tdt.setProgress(min);
        binding.lrcView.updateTime(min, true);
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        // 判断是哪个视图被点击了
        if (!gj.isTablet(this)) {
            switchViews(binding.cardview, binding.lrcView);
        }
        return true;
    }

    private void switchViews(final View view1, final View view2) {
        // 隐藏view1并显示view2的动画效果
        if (binding.cardview.getVisibility() == View.VISIBLE) {
            view1.animate()
                    .alpha(0.0f)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view1.setVisibility(View.GONE);
                            view2.setVisibility(View.VISIBLE);
                            view2.setAlpha(0.0f);
                            view2.animate().alpha(1.0f).setDuration(500).setListener(null);
                        }
                    });
        }
    }

    // 判断触摸点是否在视图范围内的辅助方法
    @Override
    public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2,
                            float distanceX, float distanceY) {
        float y = binding.getRoot().getRootView().getTranslationY() - distanceY;
        y = Math.max(0, y);
        //移动的距离
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        if (y > heightPixels - heightPixels / 5.0) {
            finish();
            return true;
        }
        binding.getRoot().getRootView().setTranslationY(y);
        return true;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2,
                           float velocityX, float velocityY) {
        return false;
    }
}