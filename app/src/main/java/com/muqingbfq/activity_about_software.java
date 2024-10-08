package com.muqingbfq;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDragHandleView;
import com.muqingbfq.databinding.ActivityAboutSoftwareBinding;
import com.muqingbfq.databinding.ListKaifazheBinding;
import com.muqingbfq.mq.AppCompatActivity;
import com.muqingbfq.mq.FragmentActivity;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class activity_about_software extends FragmentActivity<ActivityAboutSoftwareBinding> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            binding.text2.setText(String.format("%s Bate", versionName));
        } catch (PackageManager.NameNotFoundException e) {
            yc.start(this, e);
        }
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.button1).setOnClickListener(view -> {
        });
        TextView viewById = findViewById(R.id.text1);
        AssetManager assets = getAssets();
        try {
            InputStream open = assets.open("about.html");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(open));
            StringBuilder stringBuilder = new StringBuilder();
            String ling;
            while ((ling = bufferedReader.readLine()) != null) {
                stringBuilder.append(ling);
            }
            viewById.setText(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_LEGACY));
            open.close();
            bufferedReader.close();

        } catch (IOException e) {
            viewById.setText(String.format("错误:%s", e));
        }
    }

    MenuItem itemA;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        itemA= menu.add("特别鸣谢");
        itemA.setTitle("特别鸣谢");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else if (item == itemA) {
            new botton(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected ActivityAboutSoftwareBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityAboutSoftwareBinding.inflate(layoutInflater);
    }

    class botton extends BottomSheetDialog {

        List<Object[]> list = new ArrayList<>();
        public botton(@NonNull Context context) {
            super(context);
            setTitle("特别鸣谢");
            list.add(new Object[]{"2923268971","薄荷今天吃什么?", "维护开发者", "QQ"});
            list.add(new Object[]{"3301074923","威廉", "主要测试BUG", "QQ"});
            show();
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LinearLayout linearLayout = new LinearLayout(getContext());
            RecyclerView recyclerView = new RecyclerView(getContext());
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(50,0,50,500);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new RecyclerView.Adapter<VH>() {
                @NonNull
                @Override
                public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    ListKaifazheBinding binding = ListKaifazheBinding.inflate(getLayoutInflater());
                    return new VH(binding.getRoot());
                }

                @Override
                public void onBindViewHolder(@NonNull VH holder, int position) {
                    Object[] objects = list.get(position);
                    holder.name.setText(objects[1].toString());
                    holder.zz.setText(objects[2].toString());
//                    https://q1.qlogo.cn/g?b=qq&nk=1966944300&s=100
                    Glide.with(getContext())
                            .load("https://q1.qlogo.cn/g?b=qq&nk=" + objects[0] + "&s=100")
                            .error(R.drawable.ic_launcher_foreground)
                            .into(holder.imageView);
                }

                @Override
                public int getItemCount() {
                    return list.size();
                }
            });
            linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayout.addView(new BottomSheetDragHandleView(getContext()));
            linearLayout.addView(recyclerView);
            setContentView(linearLayout);
        }
    }

    class VH extends RecyclerView.ViewHolder {
        public TextView name, zz;
        public ImageView imageView;
        public VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text1);
            zz = itemView.findViewById(R.id.text2);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }


}