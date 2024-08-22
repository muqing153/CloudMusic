package com.muqingbfq.mq;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class BlurGradientTransformation extends BitmapTransformation {
    private static final String TAG = "BlurGradientTrans";
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mScriptIntrinsicBlur;
    private float mRadius = 25f;
    private int[] mGradientColors = new int[]{0x66000000, 0xcc000000};
    private float[] mGradientPositions = new float[]{0f, 1f};

    public BlurGradientTransformation(Context context) {
        super();
        mRenderScript = RenderScript.create(context);
        mScriptIntrinsicBlur = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
    }

    public BlurGradientTransformation(Context context, float radius) {
        this(context);
        mRadius = radius;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap source = pool.get(toTransform.getWidth(), toTransform.getHeight(), toTransform.getConfig());
        if (source == null) {
            source = Bitmap.createBitmap(toTransform.getWidth(), toTransform.getHeight(), toTransform.getConfig());
        }
        Canvas canvas = new Canvas(source);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(new RectF(0, 0, toTransform.getWidth(), toTransform.getHeight()), 30f, 30f, paint);
        Allocation input = Allocation.createFromBitmap(mRenderScript, source, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(mRenderScript, input.getType());
        mScriptIntrinsicBlur.setInput(input);
        mScriptIntrinsicBlur.setRadius(mRadius);
        mScriptIntrinsicBlur.forEach(output);
        output.copyTo(source);

        // 绘制渐变色
        Canvas canvas1 = new Canvas(source);
        Paint paint1 = new Paint();
        paint1.setAntiAlias(true);
        paint1.setShader(new android.graphics.LinearGradient(0, 0, 0, source.getHeight(), mGradientColors, mGradientPositions, Shader.TileMode.CLAMP));
        paint1.setXfermode(null);
        canvas1.drawRect(new Rect(0, 0, source.getWidth(), source.getHeight()), paint1);

        return source;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update("BlurGradientTransformation".getBytes());
    }

    @Override
    public int hashCode() {
        return BlurGradientTransformation.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BlurGradientTransformation;
    }

    public BlurGradientTransformation setRadius(float radius) {
        mRadius = radius;
        return this;
    }

    public BlurGradientTransformation setGradientColors(int[] colors) {
        mGradientColors = colors;
        return this;
    }

    public BlurGradientTransformation setGradientPositions(float[] positions) {
        mGradientPositions = positions;
        return this;
    }
}
