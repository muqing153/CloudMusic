package com.muqingbfq.fragment;

import static android.content.Context.WINDOW_SERVICE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqingbfq.PlaybackService;
import com.muqingbfq.R;
import com.muqingbfq.bfqkz;
import com.muqingbfq.databinding.FragmentBflbDbBinding;
import com.muqingbfq.databinding.ListMp3ABinding;
import com.muqingbfq.list.MyViewHoder;
import com.muqingbfq.main;
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
//                    Collections.swap(bfqkz.list, fromPosition, toPosition);
                    Player player = PlaybackService.mediaSession.getPlayer();
                    player.moveMediaItem(fromPosition, toPosition);
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
        bflb_db dialog = new bflb_db(context);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED); // 默认展开
                behavior.setSkipCollapsed(true); // 禁止折叠
            }
        });
        dialog.show();
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
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
//                        gj.sc(String.format("id=%s",mediaItem.mediaId));
//                            MP3 hq = url.hq(new MP3(mediaItem.mediaId));
//                            gj.sc(hq);
                            main.handler.post(() -> {
                                Player player = PlaybackService.mediaSession.getPlayer();
                                int absoluteAdapterPosition = holder.getAbsoluteAdapterPosition();
                                player.seekTo(absoluteAdapterPosition, 0);
//                                player.replaceMediaItem(absoluteAdapterPosition, PlaybackService.GetMp3(hq));
                                player.prepare();
                                player.play();
                                notifyDataSetChanged();
                            });
                        }
                    }.start();
                }
            });
            holder.bindingA.delete.setOnClickListener(v -> {
                list.remove(holder.getAbsoluteAdapterPosition());
                PlaybackService.ListSave();
                PlaybackService.list.removeIf(mp3 -> mp3.id.equals(mediaItem.mediaId));
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
