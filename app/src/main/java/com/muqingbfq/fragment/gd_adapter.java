package com.muqingbfq.fragment;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muqing.Fragment;
import com.muqing.gj;
import com.muqingbfq.MP3;
import com.muqingbfq.PlaybackService;
import com.muqingbfq.adapter.AdapterGd;
import com.muqingbfq.adapter.AdapterMp3;
import com.muqingbfq.api.resource;
import com.muqingbfq.databinding.FragmentGdBinding;
import com.muqingbfq.main;
import com.muqingbfq.mq.FilePath;
import com.muqingbfq.mq.wl;

import org.json.JSONArray;
import org.json.JSONObject;

public class gd_adapter extends Fragment<FragmentGdBinding> {
    AdapterMp3 adapterMp3;


    AdapterGd adapterGd;

    @Override
    public void setUI(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                      @Nullable Bundle savedInstanceState) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerview1.setLayoutManager(linearLayoutManager);
        adapterGd = new AdapterGd();
        binding.recyclerview1.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                // 只在第一个 Item
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.left = gj.dp2px(getContext(), 9);
                } else if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                    outRect.right = gj.dp2px(getContext(), 9);
                }
            }
        });
        new Thread() {
            @Override
            public void run() {
                super.run();
                resource.recommend(adapterGd.list);
                main.handler.post(new sx());
            }
        }.start();

        adapterMp3 = new AdapterMp3(this.requireActivity());
        binding.recyclerview2.setLayoutManager(new LinearLayoutManager(getContext()));
        mp3list();
        onResume();
    }

    private class sx implements Runnable {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void run() {
            binding.recyclerview1.setAdapter(adapterGd);
            binding.recyclerview1Bar.setVisibility(View.GONE);
        }
    }

    public void mp3list() {
        new Thread() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                super.run();
                String hq = wl.hq("/recommend/songs", null);
                if (hq == null) {
                    hq = FilePath.dqwb(FilePath.filesdri + "songs.json");
                }
                try {
                    JSONObject jsonObject = new JSONObject(hq);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray dailySongs = data.getJSONArray("dailySongs");
                    for (int i = 0; i < dailySongs.length(); i++) {
                        JSONObject jsonObject1 = dailySongs.getJSONObject(i);
                        String id = jsonObject1.getString("id");
                        String name = jsonObject1.getString("name");
                        JSONArray ar = jsonObject1.getJSONArray("ar");
                        StringBuilder zz = new StringBuilder();
                        for (int j = 0; j < ar.length(); j++) {
                            zz.append(ar.getJSONObject(j).getString("name")).append(' ');
                        }
                        JSONObject al = jsonObject1.getJSONObject("al");
                        String picUrl = al.getString("picUrl");
                        adapterMp3.list.add(new MP3(id, name, zz.toString(), picUrl));
                    }
                    FilePath.xrwb(FilePath.filesdri + "songs.json", hq);
                    requireActivity().runOnUiThread(() -> {
                        binding.recyclerview2.setAdapter(adapterMp3);
                        binding.recyclerview2Bar.setVisibility(View.GONE);
                    });
                } catch (Exception e) {
                    gj.sc(e);
                }
            }
        }.start();
    }

    public void Gdlist() {

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (adapterMp3 != null && PlaybackService.mediaSession != null) {
            PlaybackService.mediaSession.getPlayer().addListener(adapterMp3.playerListener);
            adapterMp3.notifyDataSetChanged();
            gj.sc("Fragment 可见");
        } else {
            gj.sc("mediaSession==null");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (PlaybackService.mediaSession != null) {
            PlaybackService.mediaSession.getPlayer().removeListener(adapterMp3.playerListener);
            gj.sc("Fragment 不可见");
        }
    }

    @Override
    protected FragmentGdBinding getViewBindingObject(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentGdBinding.inflate(inflater, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        binding.recyclerview2.setAdapter(null);
    }
}
