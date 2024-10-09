package com.muqingbfq.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.muqingbfq.MP3;
import com.muqingbfq.XM;
import com.muqingbfq.adapter.AdapterGdH;
import com.muqingbfq.adapter.AdapterMp3;
import com.muqingbfq.databinding.FragmentSearchBinding;
import com.muqingbfq.databinding.RecyclerVBinding;
import com.muqingbfq.main;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class search extends Fragment {
    public FragmentSearchBinding binding;
    public String string;
    List<Fragment> fragments=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        binding.viewPager.setSaveEnabled(false);
        adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }

            @Override
            public int getItemCount() {
                return fragments.size();
            }
        };
        return binding.getRoot();
    }

    public static class mp3 extends Fragment {
        public static mp3 newInstance(String string) {
            mp3 fragment = new mp3();
            Bundle args = new Bundle();
            args.putString("string", string);
            fragment.setArguments(args);
            return fragment;
        }
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            List<MP3> list = new ArrayList<>();
            String string = getArguments().getString("string");
            RecyclerVBinding binding = RecyclerVBinding.inflate(inflater, container, false);
            binding.recycleview.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recycleview.setAdapter(new AdapterMp3(list));
            list.clear();
            binding.recyclerviewBar.setVisibility(View.VISIBLE);
            binding.recyclerviewText.setVisibility(View.GONE);
            new Thread() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void run() {
                    super.run();
                    mp3(list, string);
                    main.handler.post(() -> {
                        binding.recyclerviewBar.setVisibility(View.GONE);
                        if (list.isEmpty()) {
                            binding.recyclerviewText.setVisibility(View.VISIBLE);
                        } else {
                            binding.recyclerviewText.setVisibility(View.GONE);
                        }
                        binding.recycleview.getAdapter().notifyDataSetChanged();
                    });
                }
            }.start();
            return binding.getRoot();
        }
    }
    public static class gd extends Fragment {
        public static gd newInstance(String string) {
            gd fragment = new gd();
            Bundle args = new Bundle();
            args.putString("string", string);
            fragment.setArguments(args);
            return fragment;
        }
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            List<XM> list = new ArrayList<>();
            String string = getArguments().getString("string");
            RecyclerVBinding binding = RecyclerVBinding.inflate(inflater, container, false);
            binding.recycleview.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recycleview.setAdapter(new AdapterGdH(list));
            list.clear();
            binding.recyclerviewBar.setVisibility(View.VISIBLE);
            binding.recyclerviewText.setVisibility(View.GONE);
            new Thread() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void run() {
                    super.run();
                    gd(list, string);
                    main.handler.post(() -> {
                        binding.recyclerviewBar.setVisibility(View.GONE);
                        if (list.isEmpty()) {
                            binding.recyclerviewText.setVisibility(View.VISIBLE);
                        } else {
                            binding.recyclerviewText.setVisibility(View.GONE);
                        }
                        binding.recycleview.getAdapter().notifyDataSetChanged();
                    });
                }
            }.start();
            return binding.getRoot();
        }
    }
    private FragmentStateAdapter adapter;
    @SuppressLint("NotifyDataSetChanged")
    public void sx(String string) {
        this.string = string;
        fragments.clear();
        fragments.add(mp3.newInstance(string));
        fragments.add(gd.newInstance(string));
        binding.viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        String[] strtab = new String[]{"歌曲", "歌单"};
        //将tabbView绑定到tab
        new TabLayoutMediator(binding.tablayout, binding.viewPager, (tab, position) ->
                tab.setText(strtab[position])).attach();
    }

    private static void mp3(List<MP3> list, String str) {
        try {
            Long.parseLong(str);
            com.muqingbfq.api.playlist.hq(list, str);
            return;
        } catch (NumberFormatException e) {
            gj.sc(e);
        }
        String hq = wl.hq("/search?keywords=" + str + "&type=1");
        try {
            JSONArray jsonArray = new JSONObject(hq).getJSONObject("result")
                    .getJSONArray("songs");
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                JSONArray artists = jsonObject.getJSONArray("artists");
                int length1 = artists.length();
                StringBuilder zz = null;
                for (int j = 0; j < length1; j++) {
                    JSONObject josn = artists.getJSONObject(j);
                    String name_zz = josn.getString("name");
                    if (zz == null) {
                        zz = new StringBuilder(name_zz);
                    } else {
                        zz.append("/").append(name_zz);
                    }
                }
                list.add(new MP3(id, name, zz.toString(), ""));
            }
        } catch (Exception e) {
            gj.sc(e);
        }
    }

    private static void gd(List<XM> list, String str) {
        try {
            Long.parseLong(str);
            String hq = wl.hq("/playlist/detail?id=" + str);
            JSONObject js = new JSONObject(hq).getJSONObject("playlist");
            String id = js.getString("id");
            String name = js.getString("name");
            String coverImgUrl = js.getString("coverImgUrl");
//                gj.sc(name);
            list.add(new XM(id, name, coverImgUrl));
            return;
        } catch (Exception e) {
            gj.sc(e);
        }
        try {
            String hq = wl.hq("/search?keywords=" + str + "&type=1000");
            JSONArray jsonArray = new JSONObject(hq).getJSONObject("result")
                    .getJSONArray("playlists");
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");

                String name = jsonObject.getString("name");
                String coverImgUrl = jsonObject.getString("coverImgUrl");

                long playCount = jsonObject.getLong("playCount");
                String formattedNumber = String.valueOf(playCount);
                if (playCount > 9999) {
                    DecimalFormat df = new DecimalFormat("#,###.0万");
                    formattedNumber = df.format(playCount / 10000);
                }
                String s = jsonObject.getInt("trackCount") + "首，"
                        + "by " + jsonObject.getJSONObject("creator").getString("nickname")
                        + "，播放"
                        + formattedNumber + "次";
                list.add(new XM(id, name, s, coverImgUrl));
            }
        } catch (Exception e) {
            gj.sc(e);
        }
    }
}
