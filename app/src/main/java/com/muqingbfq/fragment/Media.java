package com.muqingbfq.fragment;
import com.dirror.lyricviewx.LyricViewX;
import com.google.common.base.Strings;
import com.muqing.gj;

import org.json.JSONObject;
public class Media {
    public static String[] loadLyric(String lrc) {
        if (Strings.isNullOrEmpty(lrc)) {
            LyricViewX.lrc("", "");
            return null;
        }
        JSONObject jsonObject;
        String a = null, b = null;
        try {
            jsonObject = new JSONObject(lrc);
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
