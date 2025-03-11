package com.muqingbfq.api;

import static com.muqingbfq.mq.MediaItemAdapter.type;

import android.app.Activity;
import android.os.Environment;

import androidx.media3.common.MediaItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.muqingbfq.MP3;
import com.muqingbfq.mq.MediaItemAdapter;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;
import com.muqingbfq.mq.wl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class playlist extends Thread {
    public static final String api = "/playlist/track/all";

    public static String gethq(String uid) {
        return wl.hq(api,new String[][]{
                {"id", uid},
                {"limit", "100"}
        });
    }

    public static boolean hq(List<MP3> list, String uid) {
        switch (uid) {
            case "mp3_xz.json":
                return playlist.hq_xz(list);
            case "mp3_like.json":
                return playlist.hq_like(list);
        }
        list.clear();
        try {
            String hq = wj.dqwb(wj.gd + uid);
            if (hq == null || hq.isEmpty()) {
                hq = gethq(uid);
            }
            list.clear();
            JSONObject json = new JSONObject(hq);
            JSONArray songs = json.getJSONArray("songs");
            int length = songs.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = songs.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                try {
                    String tns = jsonObject.getString("tns");
                    tns = tns.replace("[\"", "(");
                    tns = tns.replace("\"]", ")");
                    name += tns;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JSONObject al = jsonObject.getJSONObject("al");
                JSONArray ar = jsonObject.getJSONArray("ar");
                StringBuilder zz = new StringBuilder();
                int length_a = ar.length();
                for (int j = 0; j < length_a; j++) {
                    zz.append(ar.getJSONObject(j).getString("name"))
                            .append("/");
                }
                zz.append("-").append(al.getString("name"));
                String picUrl = al.getString("picUrl");
                list.add(new MP3(id, name, zz.toString(), picUrl));
            }
            return true;
        } catch (Exception e) {
            gj.sc("失败的错误 " + e);
        }
        return false;
    }

    public static List<MP3> hq(String uid) {
        List<MP3> list = new ArrayList<>();
        try {
            String hq = wj.dqwb(wj.gd + uid);
            if (hq == null || hq.isEmpty()) {
                hq = gethq(uid);
            }
            JSONObject json = new JSONObject(hq);
            JSONArray songs = json.getJSONArray("songs");
            int length = songs.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = songs.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                try {
                    String tns = jsonObject.getString("tns");
                    tns = tns.replace("[\"", "(");
                    tns = tns.replace("\"]", ")");
                    name += tns;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JSONObject al = jsonObject.getJSONObject("al");
                JSONArray ar = jsonObject.getJSONArray("ar");
                StringBuilder zz = new StringBuilder();
                int length_a = ar.length();
                for (int j = 0; j < length_a; j++) {
                    zz.append(ar.getJSONObject(j).getString("name"))
                            .append("/");
                }
                zz.append("-").append(al.getString("name"));
                String picUrl = al.getString("picUrl");
                list.add(new MP3(id, name, zz.toString(), picUrl));
            }
            return list;
        } catch (Exception e) {
            gj.sc("失败的错误 " + e);
        }
        return list;
    }

    public static boolean hq_like(List<MP3> list) {
        try {
            String dqwb = wj.dqwb(wj.gd + "mp3_like.json");
            if (dqwb == null) {
                return false;
            }
            Gson gson = new GsonBuilder().registerTypeAdapter(MediaItem.class, new MediaItemAdapter())
                    .create();
            List<MediaItem> o = gson.fromJson(dqwb, type);
            for (MediaItem mediaItem : o) {
                if (mediaItem.mediaMetadata.title != null && mediaItem.mediaMetadata.artist != null && mediaItem.mediaMetadata.artworkUri != null) {
                    list.add(new MP3(mediaItem.mediaId, mediaItem.mediaMetadata.title.toString(),
                            mediaItem.mediaMetadata.artist.toString(),
                            mediaItem.mediaMetadata.artworkUri.toString()));
                }
            }
            return true;
        } catch (Exception e) {
            gj.sc("失败的错误 " + e);
        }
        return false;
    }

    public static boolean hq_xz(List<MP3> list) {
        try {
            File file = new File(wj.mp3);
            File[] files = file.listFiles();
            list.clear();
            for (File value : files) {
                ID3v2 mp3File = new Mp3File(value).getId3v2Tag();
                String id = value.getName();
                String name = mp3File.getTitle();
                String zz = mp3File.getArtist();
//                Bitmap bitmap = null;
//                // 获取专辑封面图像的二进制数据
//                byte[] imageData = mp3File.getAlbumImage();
//                if (imageData != null) {
////                    Log.d("ImageData", "专辑封面图像大小: " + imageData.length + " 字节");
//                    // 将二进制数据保存为文件或显示
//                    bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
//                }
                list.add(new MP3(id, name, zz, null));
            }
            return true;
        } catch (Exception e) {
            gj.sc("失败的错误 " + e);
        }
        return false;
    }

    public static void hq_listHistory(List<MP3> list) {
        try {
            String dqwb = wj.dqwb(wj.gd + "mp3_listHistory.json");
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(MediaItem.class, new MediaItemAdapter()) // 绑定适配器
                    .create();
            List<MediaItem> listHistory = gson.fromJson(dqwb, type);
            if (listHistory != null) {
                int size = listHistory.size();
                for (int i = 0; i < size; i++) {
                    MediaItem mediaItem = listHistory.get(i);
                    if (mediaItem.mediaMetadata.title != null && mediaItem.mediaMetadata.artist != null && mediaItem.mediaMetadata.artworkUri != null) {
                        list.add(new MP3(mediaItem.mediaId, mediaItem.mediaMetadata.title.toString(),
                                mediaItem.mediaMetadata.artist.toString(),
                                mediaItem.mediaMetadata.artworkUri.toString()));
                    }
                }
            }
        } catch (Exception e) {
            gj.sc(e);
        }
    }


    public static void hq_cd(Activity context, List<MP3> list) {
        boolean cd = wj.isCD(context);
        if (!cd) {
            return;
        }
        list.clear();
        try {
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            CD(new File(absolutePath), list);
        } catch (Exception e) {
            gj.sc("失败的错误 " + e);
        }
    }

    private static void CD(File file, List<MP3> list) {
        for (File a : file.listFiles()) {
            if (a.isFile()) {
                try {
                    // 创建一个 Mp3File 对象，用于读取 MP3 文件
                    Mp3File mp3file = new Mp3File(a);
                    // 检查是否存在 ID3v2 标签
                    if (mp3file.hasId3v2Tag()) {
                        // 获取 ID3v2 标签实例
                        ID3v2 id3v2tag = mp3file.getId3v2Tag();
                        MP3 mp3 = new MP3(a.toString(), id3v2tag.getTitle(),
                                id3v2tag.getArtist()
                                , a.toString());
                        list.add(mp3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (a.isDirectory()) {
                String string = a.getName();
                if (string.startsWith(".") || string.equals("Android") || string.equals("data")) {
                    continue;
                }
                CD(a, list);
            }
        }
    }
}
