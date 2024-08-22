package com.muqingbfq.mq;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muqingbfq.R;
import com.muqingbfq.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class floating extends Service {
    private static RecyclerView.Adapter<ViewHolder> lbspq;

    class spq extends RecyclerView.Adapter<ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(new TextView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String s = list.get(position);
            holder.textView.setText(s);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public static void start(Context context) {
        if (Settings.canDrawOverlays(context)) {
            context.startService(new Intent(context, floating.class));
        }
    }

    public static void end(Context context) {
        Intent serviceIntent = new Intent(context, floating.class);
        context.stopService(serviceIntent);
    }

    public static List<String> list;
    private WindowManager windowManager;
    private View view;
    private View image, layout;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void addtext(String str) {
        if (lbspq == null || list == null) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA); // 创建一个 SimpleDateFormat 对象，指定时间格式
        String formattedDate = sdf.format(new Date()); // 格式化当前时间
        list.add(0, formattedDate + ": " + str);
        main.handler.post(lbspq::notifyDataSetChanged);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate() {
        super.onCreate();
        list = new ArrayList<>();
        lbspq = new spq();
        view = LayoutInflater.from(this).inflate(R.layout.floating_sc, null);
        layout = view.findViewById(R.id.view1);
        ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        layoutParams.height = displayMetrics.heightPixels - displayMetrics.heightPixels / 2 / 2;
        layoutParams.width = displayMetrics.widthPixels - displayMetrics.widthPixels / 2 / 2;
        layout.setLayoutParams(layoutParams);
        layout.setVisibility(View.GONE);
        image = view.findViewById(R.id.image);
        image.setOnClickListener(vw -> {
            layout.setVisibility(View.VISIBLE);
            vw.setVisibility(View.GONE);
        });
        view.findViewById(R.id.text4).setOnClickListener(view -> {
            layout.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
        });
        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setAdapter(lbspq);
        addtext("Android stdio 2022.3.1版-调试器");

        //清空按钮
        view.findViewById(R.id.text1).setOnClickListener(view -> {
            list.clear();
            lbspq.notifyDataSetChanged();
        });
        //复制按钮
        view.findViewById(R.id.text2).setOnClickListener(view -> {
            // 获取剪贴板管理器
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("label", list.get(0)));
            gj.ts(this,"成功复制了第一个数据");
        });
        //关闭按钮
        view.findViewById(R.id.text3).setOnClickListener(view -> stopSelf());
        int i = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                i,
                PixelFormat.TRANSLUCENT
        );
        params.x = -displayMetrics.widthPixels;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(view, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (view != null && windowManager != null) {
            lbspq = null;
            list = null;
            windowManager.removeView(view);
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemview) {
            super(itemview);
            textView = (TextView) itemview;
            textView.setTextColor(Color.WHITE);
            textView.setOnLongClickListener(view -> {
                // 获取剪贴板管理器
                ClipboardManager clipboard = (ClipboardManager) view.getContext().
                        getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("label", list.get(0)));
                gj.ts(view.getContext(), "复制成功");
                return false;
            });
        }
    }
}