package com.muqingbfq.mq;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewbinding.ViewBinding;

import com.muqingbfq.R;

public abstract class AppCompatActivity<ViewBindingType extends ViewBinding> extends androidx.appcompat.app.AppCompatActivity {

    protected abstract ViewBindingType getViewBindingObject(LayoutInflater layoutInflater);

    protected ViewBindingType getViewBinding() {
        binding = getViewBindingObject(getLayoutInflater());
        return binding;
    }

    public ViewBindingType binding;

    public void setContentView() {
        EdgeToEdge.enable(this);
        super.setContentView(getViewBinding().getRoot());
//        Window window = getWindow();
////        请求进行全屏布局+更改状态栏字体颜色
//        //          获取程序是不是夜间模式
//        int uiMode = getApplicationContext().getResources().getConfiguration().uiMode;
//        if ((uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
////            SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION  and  SYSTEM_UI_FLAG_LAYOUT_STABLE请求进行全屏布局
////            SYSTEM_UI_FLAG_VISIBLE进行更改状态栏字体颜色
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_VISIBLE);//白色
//        } else {
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//黑色
//
//        }
////                让内容显示在系统栏的后面,也就是显示在状态栏和导航栏的后面
//        WindowCompat.setDecorFitsSystemWindows(window, true);
////      沉浸状态栏(给任务栏上透明的色)(Android 10 上，只需要将系统栏颜色设为完全透明即可:)
//        window.setStatusBarColor(Color.TRANSPARENT);
//        //                沉浸导航栏（设置透明色）
//        window.setNavigationBarColor(Color.TRANSPARENT);
//
////                在安卓10以上禁用系统栏视觉保护。
//// 当设置了  导航栏 栏背景为透明时，NavigationBarContrastEnforced 如果为true，则系统会自动绘制一个半透明背景
//// 状态栏的StatusBarContrast 效果同理，但是值默认为false，因此不用设置
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            window.setNavigationBarContrastEnforced(false);
//        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,0);
            return insets;
        });
    }
}
