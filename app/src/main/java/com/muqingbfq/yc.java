package com.muqingbfq;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqing.gj;
import com.muqingbfq.databinding.ActivityYcBinding;
import com.muqing.AppCompatActivity;
public class yc extends AppCompatActivity<ActivityYcBinding> {
    public Object exception;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        Intent intent = getIntent();
        exception = intent.getStringExtra("e");

        String deviceModel = Build.MODEL;
        String deviceManufacturer = Build.MANUFACTURER;
        String osVersion = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float density = metrics.density;
        int densityDpi = metrics.densityDpi;
// 假设你已经获取到了手机信息，保存在相应的变量中
        String wb = "设备型号：" + deviceModel + "\n"
                + "制造商：" + deviceManufacturer + "\n"
                + "设备ID：" + deviceId + "\n"
                + "操作系统版本：" + osVersion + "\n"
                + "SDK版本：" + sdkVersion + "\n"
                + "屏幕尺寸：" + widthPixels + "x" + heightPixels + "\n"
                + "屏幕密度：" + density + "\n"
                + "密度DPI：" + densityDpi + "\n" +
                "异常信息： " + exception.toString();
        binding.text.setText(wb);
        binding.button2.setOnClickListener(view -> finish());
    }
    public static void start(Context context, Object e) {
        gj.sc(e);
        Intent intent = new Intent(context, yc.class);
        intent.putExtra("e",e.toString());
        context.startActivity(intent);
    }

    public static void tc(Context context, Object exception) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("不是特别重要的警告")
                .setMessage(exception.toString())
                .setNegativeButton("无视", null)
                .setPositiveButton("分享", null)
                .show();
    }

    @Override
    protected ActivityYcBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityYcBinding.inflate(layoutInflater);
    }
}
