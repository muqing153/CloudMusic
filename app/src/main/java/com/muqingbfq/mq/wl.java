package com.muqingbfq.mq;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.muqing.gj;
import com.muqingbfq.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
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

    public static void getCookie() {
        Cookie = main.sp.getString("Cookie", "");
    }

    public static final ConcurrentHashMap<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();


    // 自定义 CookieJar 实现
    private static class CustomCookieJar implements CookieJar {
        @Override
        public void saveFromResponse(HttpUrl url, @NonNull List<Cookie> cookies) {
            cookieStore.put(url.host(), cookies);
        }

        @NonNull
        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            return Objects.requireNonNull(cookieStore.getOrDefault(url.host(), new ArrayList<>()));
        }
    }

    public final static OkHttpClient client = new OkHttpClient();

    public static String hq(String url, String[][] strings) {
        try {
            StringBuilder stringBuffer = new StringBuilder();
            if (strings != null) {
                for (String[] b : strings) {
                    stringBuffer.append(b[0]).append("=").append(b[1]).append("&");
                }
            }
            stringBuffer.append("cookie").append("=").append(Cookie);
            Request request = new Request.Builder()
                    .url(main.api + url + "?" + stringBuffer)
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

    @Nullable
    public static String hq(String url, String strings, boolean bool) {
        try {
            Request request = new Request.Builder()
                    .url(main.api + url + "?" + strings)
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
//        builder.addFormDataPart("cookie", Cookie);

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

}
