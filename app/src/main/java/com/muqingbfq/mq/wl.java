package com.muqingbfq.mq;


import com.muqingbfq.XM;
import com.muqingbfq.main;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    public static JSONObject jsonpost(String str, String[][] a) {
        try {
            return new JSONObject(post(str, a));
        } catch (JSONException e) {
            gj.sc(e);
            return null;
        }
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

    public static class xz extends Thread {
        String url;
        XM x;

        public xz(String url, XM x) {
            this.url = url;
            this.x = x;
            start();
        }

    }

}
