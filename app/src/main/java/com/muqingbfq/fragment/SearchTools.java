package com.muqingbfq.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.muqingbfq.MP3;
import com.muqingbfq.XM;
import com.muqingbfq.adapter.AdapterGdH;
import com.muqingbfq.adapter.AdapterMp3;
import com.muqingbfq.databinding.RecyclerVBinding;
import com.muqingbfq.main;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchTools {
    public String string;
    TabLayout tablayout;
    ViewPager2 viewPager2;
    FragmentActivity activity;

    public SearchTools(FragmentActivity activity, TabLayout tabLayout, ViewPager2 viewPager2) {
        this.tablayout = tabLayout;
        this.viewPager2 = viewPager2;
        this.activity = activity;
        gj.sc("SearchTools:" + tabLayout + " " + viewPager2 + activity);
        tabLayoutMediator = new TabLayoutMediator(tablayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("歌曲");
                    break;
                case 1:
                    tab.setText("歌单");
                    break;
            }
        });
    }

    public static class mp3 extends Fragment {
        public static mp3 newInstance(String string) {
            mp3 fragment = new mp3();
            Bundle args = new Bundle();
            args.putString("string", string);
            fragment.setArguments(args);
            return fragment;
        }

        RecyclerVBinding binding;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            String string = getArguments().getString("string");
            binding = RecyclerVBinding.inflate(inflater, container, false);
            binding.recycleview.setLayoutManager(new LinearLayoutManager(getContext()));
            AdapterMp3 adapterMp3 = new AdapterMp3();
            binding.recyclerviewBar.setVisibility(View.VISIBLE);
            binding.recyclerviewText.setVisibility(View.GONE);
            new Thread() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void run() {
                    super.run();
                    mp3(adapterMp3.list, string);
                    main.handler.post(() -> {
                        binding.recyclerviewBar.setVisibility(View.GONE);
                        if (adapterMp3.list.isEmpty()) {
                            binding.recyclerviewText.setVisibility(View.VISIBLE);
                        } else {
                            binding.recyclerviewText.setVisibility(View.GONE);
                        }
                        binding.recycleview.setAdapter(adapterMp3);
                    });
                }
            }.start();
            return binding.getRoot();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding.recycleview.setAdapter(null);
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
                        binding.recycleview.setAdapter(new AdapterGdH(list));
                    });
                }
            }.start();
            return binding.getRoot();
        }
    }

    TabLayoutMediator tabLayoutMediator;

    public void sx(String string) {
        this.string = string;
        activity.runOnUiThread(() -> {
            delete();
            if (viewPager2 != null) {
                viewPager2.setAdapter(new FragmentStateAdapter(activity) {
                    @NonNull
                    @Override
                    public Fragment createFragment(int position) {
                        switch (position) {
                            case 0:
                                return mp3.newInstance(string);
                            case 1:
                                return gd.newInstance(string);
                        }
                        return null;
                    }

                    @Override
                    public int getItemCount() {
                        return 2;
                    }
                });
            }
// 确保只有一个 TabLayoutMediator 被附加
            if (!tabLayoutMediator.isAttached()) {
                tabLayoutMediator.attach();  // 仅在没有附加时才附加
            }
        });
    }

    public void delete() {
        if (tabLayoutMediator != null && tabLayoutMediator.isAttached()) {
            tabLayoutMediator.detach();
        }
        if (viewPager2 != null) {
            viewPager2.setAdapter(null);
        }
    }

    private static void mp3(List<MP3> list, String str) {
        try {
            Long.parseLong(str);
            com.muqingbfq.api.playlist.hq(list, str);
            return;
        } catch (NumberFormatException e) {
            gj.sc(e);
        }
        String hq = wl.hq("/search", "keywords=" + str + "&type=1", false);
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
            String hq = wl.hq("/playlist/detail", "id=" + str, false);
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
            String hq = wl.hq("/search", "keywords=" + str + "&type=1000", false);
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
