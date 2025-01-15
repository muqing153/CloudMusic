package com.muqingbfq.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muqingbfq.MP3;
import com.muqingbfq.R;
import com.muqingbfq.XM;
import com.muqingbfq.activity_search;
import com.muqingbfq.adapter.AdapterGd;
import com.muqingbfq.adapter.AdapterMp3;
import com.muqingbfq.api.resource;
import com.muqingbfq.databinding.FragmentGdBinding;
import com.muqingbfq.databinding.ListMp3ImageBinding;
import com.muqingbfq.main;
import com.muqingbfq.mq.Fragment;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;
import com.muqingbfq.mq.wl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class gd_adapter extends Fragment<FragmentGdBinding> {
    List<XM> list = new ArrayList<>();
    AdapterMp3 adapterMp3 = new AdapterMp3();



    @Override
    protected FragmentGdBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentGdBinding.inflate(inflater, container, false);
    }

    AdapterGd adapterGd = new AdapterGd();
    @Override
    public void setUI(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        binding.recyclerview1.setHasFixedSize(true);
        binding.recyclerview1.setNestedScrollingEnabled(false);
        binding.recyclerview1.setLayoutManager(linearLayoutManager);
        adapterGd.list = list;
        binding.recyclerview1.setAdapter(adapterGd);
        new Thread() {
            @Override
            public void run() {
                super.run();
                resource.recommend(list);
                main.handler.post(new sx());
            }
        }.start();

        mp3list();
        binding.recyclerview2.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerview2.setNestedScrollingEnabled(false);
        binding.recyclerview2.setAdapter(adapterMp3);
//        requireActivity().findViewById(R.id.linearLayout4).post(new Runnable() {
//            @Override
//            public void run() {
//                binding.recyclerview2.setPadding(0, 0, 0,
//                        requireActivity().findViewById(R.id.linearLayout4).getHeight());
//            }
//        });
    }
    private class sx implements Runnable {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void run() {
            binding.recyclerview1.getAdapter().notifyDataSetChanged();
            binding.recyclerview1Bar.setVisibility(View.GONE);
        }
    }

    public void mp3list() {
        new Thread() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                super.run();
                String hq = wl.hq("/recommend/songs" + "?cookie=" + wl.Cookie);
                if (hq == null) {
                    hq = wj.dqwb(wj.filesdri + "songs.json");
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
                    wj.xrwb(wj.filesdri + "songs.json", hq);
                    requireActivity().runOnUiThread(() -> {
                        adapterMp3.notifyDataSetChanged();
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
}
