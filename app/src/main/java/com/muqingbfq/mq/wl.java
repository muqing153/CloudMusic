package com.muqingbfq.mq;


import android.content.Context;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.database.DatabaseProvider;
import androidx.media3.database.StandaloneDatabaseProvider;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.cache.Cache;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;

import com.muqingbfq.main;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class wl {
    public static String Cookie;

    public static void setcookie(String cookie) {
        wl.Cookie = cookie;
        main.edit.putString("Cookie", cookie);
        main.edit.commit();
    }
    public static void getCookie(){
        Cookie = main.sp.getString("Cookie", "");
    }

    public static String hq(String url) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(main.api + url)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                return response.body().string();
            }
        } catch (Exception e) {
            gj.sc("wl hq(Strnig)  " + e);
        }
        return null;
    }

    public static String post(String str, String[][] a) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
//        MediaType mediaType = MediaType.parse("text/plain");
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (String[] b : a) {
            builder.addFormDataPart(b[0], b[1]);
        }
        builder.addFormDataPart("cookie", Cookie);

        Request request = new Request.Builder()
                .url(main.api + str)
                .method("POST", builder.build())
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("Accept", "*/*")
                .addHeader("Host", "139.196.224.229:3000")
                .addHeader("Connection", "keep-alive")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                return response.body().string();
            }
        } catch (Exception e) {
            gj.sc(e);
        }
        return null;
    }

    public static String get(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                return response.body().string();
            }
        } catch (Exception e) {
            gj.sc("wl get(Strnig)  " + e);
        }
        return null;
    }


    @OptIn(markerClass = UnstableApi.class)
    public static DataSource.Factory DownMp3() {
        Context context = main.application;

        DatabaseProvider databaseProvider = new StandaloneDatabaseProvider(context);
        // 创建一个 File 对象来指定缓存目录
        File downloadDirectory = new File(wj.mp3);
        // 如果缓存目录不存在，则创建它
        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdirs();
        }
        Cache cache =
                new SimpleCache(
                        downloadDirectory, new LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024), databaseProvider);

        CacheDataSource.Factory httpDataSourceFactory = new CacheDataSource.Factory();
        return new CacheDataSource.Factory()
                        .setCache(cache)
                        .setUpstreamDataSourceFactory(httpDataSourceFactory);
    }

}
