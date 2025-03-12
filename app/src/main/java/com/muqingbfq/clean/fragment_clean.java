package com.muqingbfq.clean;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.muqing.gj;
import com.muqingbfq.R;
import com.muqingbfq.databinding.ActivityCleanBinding;
import com.muqingbfq.mq.FragmentActivity;
import com.muqingbfq.mq.FilePath;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class fragment_clean extends FragmentActivity<ActivityCleanBinding> {
    List<String[]> list = new ArrayList<>();
    List<String> list_box = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        UI();
    }

    private void UI() {
        list.clear();
        list.add(new String[]{"下载的音乐", FilePath.mp3});
        list.add(new String[]{"下载的歌单", FilePath.gd});
        list.add(new String[]{"缓存的音乐", FilePath.filesdri+"hc"});
        list.add(new String[]{"内部缓存", getCacheDir().toString()});
        String s = Objects.requireNonNull(Glide.getPhotoCacheDir(this)).toString();
        list.add(new String[]{"Glide缓存", s});
        binding.toolbar.setTitle("储存清理");
        binding.recyclerview.setAdapter(adapter);
    }

    private final RecyclerView.Adapter<VH> adapter = new RecyclerView.Adapter<VH>() {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(fragment_clean.this).
                    inflate(R.layout.list_clean, parent, false);
            return new VH(inflate);
        }

        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            String[] s = list.get(position);
            File file = new File(s[1]);
            long leng = 0;
            int size = 0;
            if (file.isDirectory()) {
                gj.sc(file.toString());
                for (File a : file.listFiles()) {
                    leng += a.length();
                    size++;
                }
            } else {
                holder.checkBox.setEnabled(false);
            }
            String s1 = Formatter.formatFileSize(fragment_clean.this, leng);
            holder.checkBox.setText(s[0] + ":" + s1 + "  共计:" + size+" 个文件");
            holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    list_box.add(file.toString());
                }else {
                    list_box.remove(file.toString());
                }
                menu_deleat.setVisible(list_box.size() > 0);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    };

    class VH extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public VH(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.box);
        }
    }

    MenuItem menu_deleat;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu_deleat = menu.add("删除");
        menu_deleat.setIcon(R.drawable.delete);
        menu_deleat.setTitle("删除");
        menu_deleat.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu_deleat.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected ActivityCleanBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityCleanBinding.inflate(layoutInflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item == menu_deleat) {
            for (int i = 0; i < list_box.size(); i++) {
                File s= new File(list_box.get(i));
                FilePath.sc(s);
            }
            list_box.clear();
            menu_deleat.setVisible(false);
            UI();
        }
        return super.onOptionsItemSelected(item);
    }
}
