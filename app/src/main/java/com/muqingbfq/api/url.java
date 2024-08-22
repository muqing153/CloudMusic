package com.muqingbfq.api;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.muqingbfq.MP3;
import com.muqingbfq.fragment.Media;
import com.muqingbfq.home;
import com.muqingbfq.main;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;
import com.muqingbfq.mq.wl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class url extends Thread {
    public static String api = "/song/url/v1";
    MP3 x;

    public url(MP3 x) {
        this.x = x;
        start();
    }

    public static MP3 hq(MP3 x) {
//        gj.sc(x.id);
        getLrc(x.id);
        Media.loadLyric();
        try {
            if (wj.cz(x.id)) {
                x.url = x.id;
                return x;
            } else if (wj.cz(wj.mp3 + x.id)) {
                x.url = wj.mp3 + x.id;
                return x;
            } else if (wj.cz(wj.filesdri + "hc/" + x.id)) {
                x.url = wj.filesdri + "hc/" + x.id;
                return x;
            }
            String level = "standard";
            boolean wiFiConnected = gj.isWiFiConnected();
            if (wiFiConnected) {
                level = "exhigh";
            }
            String hq = wl.hq(api + "?id=" + x.id + "&level=" +
                    level + "&cookie=" + wl.Cookie);
            if (hq == null) {
                return null;
            }
            JSONObject json = new JSONObject(hq);
//            gj.sc(json);
            if (json.getInt("code") == -460) {
                String message = json.getString("message");
                gj.sc(message);
                return null;
            }
            JSONArray data = json.getJSONArray("data");
            JSONObject jsonObject = data.getJSONObject(0);
//            gj.sc(jsonObject.getString("url"));
            x.url = jsonObject.getString("url");
            return x;
        } catch (JSONException e) {
            gj.sc("url hq :" + e);
        }
        return null;
    }

    @Override
    public void run() {
        super.run();
        com.muqingbfq.bfqkz.mp3(hq(x));
    }


    public static void getLrc(String id) {
        String file = wj.mp3 + id;
        boolean cz = wj.cz(id);
        if (cz) {
            file = id;
        }
        if (cz || wj.cz(file)) {
            try {
                Mp3File mp3file = new Mp3File(file);
                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                    com.muqingbfq.bfqkz.lrc = id3v2Tag.getLyrics();
                }
                if (com.muqingbfq.bfqkz.lrc == null) {
                    com.muqingbfq.bfqkz.lrc = wl.hq("/lyric?id=" + id);
                }
            } catch (Exception e) {
                gj.sc("url getlrc:" + e);
            }
        } else {
            com.muqingbfq.bfqkz.lrc = wl.hq("/lyric?id=" + id);
        }
    }

    public static String Lrc(String id) {
        return wl.hq("/lyric?id=" + id);
    }

    public static String picurl(String id) {
        String hq = wl.hq("/song/detail?ids=" + id);
        try {
            return new JSONObject(hq).getJSONArray("songs").getJSONObject(0)
                    .getJSONObject("al").getString("picUrl");
        } catch (Exception e) {
            gj.sc("url picurl:" + e);
        }
        return null;
    }
}