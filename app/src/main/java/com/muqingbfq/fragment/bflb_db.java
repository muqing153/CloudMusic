package com.muqingbfq.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqingbfq.MP3;
import com.muqingbfq.R;
import com.muqingbfq.api.url;
import com.muqingbfq.bfqkz;
import com.muqingbfq.databinding.FragmentBflbDbBinding;
import com.muqingbfq.databinding.ListMp3ABinding;
import com.muqingbfq.list.MyViewHoder;
import com.muqingbfq.yc;

import java.util.Collections;

public class bflb_db extends BottomSheetDialog {
    public static RecyclerView.Adapter<MyViewHoder> adapter;
    FragmentBflbDbBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentBflbDbBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            binding.lb.setAdapter(new spq());
            if (bfqkz.xm != null) {
                binding.lb.smoothScrollToPosition(getI());
            }
            binding.textView.setOnClickListener(v -> {
                        if (bfqkz.xm != null) {
                            binding.lb.smoothScrollToPosition(getI());
                        }
                    });
            binding.sc.setOnClickListener(view -> new MaterialAlertDialogBuilder(getContext())
                    .setTitle("清空播放列表")
                    .setPositiveButton("确定", (dialogInterface, i) -> {
                        bfqkz.list.clear();
                        dismiss();
                    })
                    .setNegativeButton("取消", null)
                    .show());

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    int fromPosition = viewHolder.getAdapterPosition();
                    int toPosition = target.getAdapterPosition();
                    // 在这里处理数据集的移动
                    Collections.swap(bfqkz.list,fromPosition,toPosition);
                    adapter.notifyItemMoved(fromPosition, toPosition);
                    return true; // 返回true表示已经处理了拖动
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    // 不处理滑动操作
                }
            });
            itemTouchHelper.attachToRecyclerView(binding.lb);

        } catch (Exception e) {
            yc.start(getContext(), e);
        }
    }

    private int getI() {
        int i = bfqkz.list.indexOf(bfqkz.xm);
        if (i == -1) {
            i = 0;
        }
        return i;
    }
    public bflb_db(Context context) {
        super(context);
    }

    public static void start(Context context) {
        new bflb_db(context).show();
    }

    private class spq extends RecyclerView.Adapter<MyViewHoder> {
        public spq() {
            adapter = this;
        }
        @NonNull
        @Override
        public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHoder(ListMp3ABinding.
                    inflate(getLayoutInflater(),parent,false));
        }
        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {
            MP3 x = bfqkz.list.get(position);
            holder.bindingA.name.setText(x.name);
            holder.bindingA.zz.setText(String.format(" · %s", x.zz));
            int color = ContextCompat.getColor(holder.getContext(), R.color.text);
            if (bfqkz.xm != null && x.id.equals(bfqkz.xm.id)) {
                color = ContextCompat.getColor(holder.getContext(), R.color.text_cz);
            }
            holder.bindingA.name.setTextColor(color);
            holder.bindingA.zz.setTextColor(color);
            holder.itemView.setOnClickListener(view -> {
                if (bfqkz.xm != x) {
                    bfqkz.xm = x;
                    new url(x);
                }
            });
            holder.bindingA.delete.setOnClickListener(v -> {
                bfqkz.list.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            });
        }
        @Override
        public int getItemCount() {
//            binding.textView.setText(String.valueOf(bfqkz.list.size()));
            return bfqkz.list.size();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        adapter = null;
    }
}
