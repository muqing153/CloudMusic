package com.muqingbfq.api;

import android.text.TextUtils;

import com.muqing.gj;
import com.muqingbfq.main;
import com.muqingbfq.mq.FilePath;
import com.muqingbfq.mq.wl;
import com.muqingbfq.XM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

public class resource {

    public static void recommend(List<XM> list) {
        try {
            list.clear();
            JSONObject json;
            String hq = wl.hq("/recommend/resource",null);
            if (hq == null) {
                hq = FilePath.dqwb(FilePath.gd_json);
                if (hq != null) {
                    json = new JSONObject(hq);
                    if (json.getInt("code") == 200) {
                        FilePath.xrwb(FilePath.gd_json, hq);
                        JSONArray recommend = json.getJSONArray("recommend");
                        int length = recommend.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject jsonObject = recommend.getJSONObject(i);
                            add(jsonObject, list);
                        }
                    }
                }
                return;
            }
            json = new JSONObject(hq);
            if (json.getInt("code") == 200) {
                FilePath.xrwb(FilePath.gd_json, hq);
                JSONArray recommend = json.getJSONArray("recommend");
                int length = recommend.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = recommend.getJSONObject(i);
                    add(jsonObject, list);
                }
            }
        } catch (Exception e) {
            gj.sc("resource tuijian" + e);
        }
    }


    public static XM Playlist_content(String UID) throws JSONException {
        String hq = wl.get(main.api + "/playlist/detail?id=" + UID);
        JSONObject js = new JSONObject(hq).getJSONObject("playlist");
        String id = js.getString("id");
        String name = js.getString("name");
        String coverImgUrl = js.getString("coverImgUrl");

        long playCount = js.getLong("playCount");
        String formattedNumber = String.valueOf(playCount);
        if (playCount > 9999) {
            DecimalFormat df = new DecimalFormat("#,###.0万");
            formattedNumber = df.format(playCount / 10000);
        }
        String s = js.getInt("trackCount") + "首，"
                + "by " + js.getJSONObject("creator").getString("nickname")
                + "，播放"
                + formattedNumber + "次";
        return new XM(id, name, s, coverImgUrl);
    }

//    排行榜
    public static void leaderboard(List<XM> list) {
        String hq;
        try {
            if (FilePath.cz(FilePath.gd_phb)) {
                hq = FilePath.dqwb(FilePath.gd_phb);
            } else {
                hq = wl.hq("/toplist", null);
                if (hq == null) {
                    return;
                }
                FilePath.xrwb(FilePath.gd_phb, hq);
            }
            JSONObject jsonObject = new JSONObject(hq);
            if (jsonObject.getInt("code") == 200) {
                JSONArray list_array = jsonObject.getJSONArray("list");
                int length = list_array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject get = list_array.getJSONObject(i);
                    String id = get.getString("id");
                    String name = get.getString("name") + "\n";
                    String description = get.getString("description");
                    if (!TextUtils.isEmpty(description) && !description.equals("null")) {
                        name += description;
                    }
                    String coverImgUrl = get.getString("coverImgUrl");
                    list.add(new XM(id, name, coverImgUrl));
                }
            }
        } catch (Exception e) {
            gj.sc(e);
        }
    }

    private static void add(JSONObject jsonObject, List<XM> list) throws Exception {
        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String picUrl = jsonObject.getString("picUrl");
        list.add(new XM(id, name, picUrl));
    }
}
