package com.muqingbfq.fragment;

import com.dirror.lyricviewx.LyricViewX;
import com.muqingbfq.mq.gj;

import org.json.JSONObject;

public class Media {

    public static String[] loadLyric() {
        if (com.muqingbfq.bfqkz.lrc == null) {
            return null;
        }
        JSONObject jsonObject;
        String a = null, b = null;
        try {
            jsonObject = new JSONObject(com.muqingbfq.bfqkz.lrc);
            a = jsonObject.getJSONObject("lrc").getString("lyric");
            b = jsonObject.getJSONObject("tlyric").getString("lyric");
        } catch (Exception e) {
            gj.sc("Media loadLyric " + e);
        }
        LyricViewX.lrc(a, b);
//        gj.sc(LyricViewX.lyricEntryList.get(0).text);

        return new String[]{a, b};
    }

}
