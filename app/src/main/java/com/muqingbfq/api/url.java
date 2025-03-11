package com.muqingbfq.api;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.muqingbfq.MP3;
import com.muqingbfq.fragment.Media;
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
        MP3 mp3 = new MP3();
        mp3.name = x.name;
        mp3.id = x.id;
        mp3.picurl = x.picurl;
        mp3.zz = x.zz;
        mp3.url = x.url;
        getLrc(x.id);
        Media.loadLyric(bfqkz.lrc);
        try {
            if (wj.cz(mp3.id)) {
                mp3.url = mp3.id;
                return mp3;
            } else if (wj.cz(wj.mp3 + mp3.id)) {
                mp3.url = wj.mp3 + mp3.id;
                return mp3;
            } else if (wj.cz(wj.filesdri + "hc/" + mp3.id)) {
                mp3.url = wj.filesdri + "hc/" + mp3.id;
                return mp3;
            }
            String level = "standard";
            boolean wiFiConnected = gj.isWiFiConnected();
            if (wiFiConnected) {
                level = "exhigh";
            }
            String hq = wl.hq(api,
                    new String[][]{
                            {"id", mp3.id},
                            {"level", level}
                    });
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
            mp3.url = jsonObject.getString("url");
            mp3.picurl = picurl(mp3.id);
            return mp3;
        } catch (JSONException e) {
            gj.sc("url hq :" + e);
        }
        return null;
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
                    com.muqingbfq.bfqkz.lrc = wl.hq("/lyric",new String[][]{
                            {"id", id}
                    });
                }
            } catch (Exception e) {
                gj.sc("url getlrc:" + e);
            }
        } else {
            com.muqingbfq.bfqkz.lrc = wl.hq("/lyric",new String[][]{
                    {"id", id}
            });
        }
    }

    public static String Lrc(String id) {
        return wl.hq("/lyric",new String[][]{
                {"id", id}
        });
    }

    public static String picurl(String id) {
        String hq = wl.hq("/song/detail",new String[][]{
                {"ids", id}
        });
        try {
            return new JSONObject(hq).getJSONArray("songs").getJSONObject(0)
                    .getJSONObject("al").getString("picUrl");
        } catch (Exception e) {
            gj.sc("url picurl:" + e);
        }
        return null;
    }
}