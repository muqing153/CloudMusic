package com.muqingbfq.fragment;

import static android.content.Context.WINDOW_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqingbfq.PlaybackService;
import com.muqingbfq.R;
import com.muqingbfq.bfqkz;
import com.muqingbfq.databinding.FragmentBflbDbBinding;
import com.muqingbfq.databinding.ListMp3ABinding;
import com.muqingbfq.list.MyViewHoder;
import com.muqingbfq.yc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class bflb_db extends BottomSheetDialog {
    public static RecyclerView.Adapter<MyViewHoder> adapter;
    FragmentBflbDbBinding binding;

    private void ingList() {

        if (PlaybackService.mediaSession == null) {
            return;
        }
        Player player = PlaybackService.mediaSession.getPlayer();
        List<MediaItem> list = new ArrayList<>();
        int mediaItemCount = player.getMediaItemCount();
        for (int i = 0; i < mediaItemCount; i++) {
            list.add(player.getMediaItemAt(i));
        }
        binding.lb.setAdapter(new spq(list));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentBflbDbBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        // 计算高度，比如设定为 300dp 高度
        int heightInDp = 300;
        // 设置高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        binding.getRoot().getLayoutParams().height = displayMetrics.heightPixels - displayMetrics.heightPixels / 3;
        binding.getRoot().requestLayout();
// 设置固定的高度（例如 500dp）
        try {
            ingList();
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
                    int fromPosition = viewHolder.getAbsoluteAdapterPosition();
                    int toPosition = target.getAbsoluteAdapterPosition();
                    // 在这里处理数据集的移动
                    Collections.swap(bfqkz.list, fromPosition, toPosition);
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
        if (PlaybackService.mediaSession == null) {
            return 0;
        }
        Player player = PlaybackService.mediaSession.getPlayer();
        int i = player.getCurrentMediaItemIndex();
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
        List<MediaItem> list;

        public spq(List<MediaItem> list) {
            this.list = list;
            adapter = this;
        }

        @NonNull
        @Override
        public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHoder(ListMp3ABinding.
                    inflate(getLayoutInflater(), parent, false));
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {
            MediaItem mediaItem = list.get(position);
            holder.bindingA.name.setText(mediaItem.mediaMetadata.title);
            holder.bindingA.zz.setText(String.format(" · %s", mediaItem.mediaMetadata.artist));
            int color = ContextCompat.getColor(holder.getContext(), R.color.text);
            //获取当前播放的项目
            if (PlaybackService.mediaSession != null) {
                if (mediaItem.mediaId.equals(PlaybackService.mediaSession.getPlayer().getCurrentMediaItem().mediaId)) {
                    color = ContextCompat.getColor(holder.getContext(), R.color.text_cz);
                }
            }
            holder.bindingA.name.setTextColor(color);
            holder.bindingA.zz.setTextColor(color);
            holder.itemView.setOnClickListener(view -> {
                if (PlaybackService.mediaSession != null) {
                    PlaybackService.mediaSession.getPlayer().seekTo(holder.getAbsoluteAdapterPosition(), 0);
                    PlaybackService.mediaSession.getPlayer().prepare();
                    PlaybackService.mediaSession.getPlayer().play();
                    notifyDataSetChanged();
                }
            });
            holder.bindingA.delete.setOnClickListener(v -> {
                list.remove(holder.getAbsoluteAdapterPosition());
                notifyItemRemoved(holder.getAbsoluteAdapterPosition());
            });
        }

        @Override
        public int getItemCount() {
//            binding.textView.setText(String.valueOf(bfqkz.list.size()));
            return list.size();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        adapter = null;
    }
}
