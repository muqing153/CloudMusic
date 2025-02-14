package com.muqingbfq.api;


import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.muqingbfq.MP3;
import com.muqingbfq.main;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;
import com.muqingbfq.mq.wl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileDownloader {
    OkHttpClient client = new OkHttpClient();
    AlertDialog dialog;
    TextView textView;
    Context context;
    public FileDownloader(Context context) {
        this.context = context;
        main.handler.post(() -> {
            textView = new TextView(context);
            dialog = new MaterialAlertDialogBuilder(context)
                    .setTitle("下载中...")
                    .setView(textView)
                    .show();
        });
    }
    public void downloadFile(MP3 x) {
        Request request = new Request.Builder()
                .url(main.api + url.api + "?id=" + x.id + "&level=" +
                        "standard")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                // 下载失败处理
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (!response.isSuccessful()) {
                    // 下载失败处理
                    return;
                }
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray data = json.getJSONArray("data");
                    JSONObject jsonObject = data.getJSONObject(0);
                    String url = jsonObject.getString("url");
                    downloadFile(url, x);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    long fileSizeDownloaded = 0;
    public void downloadFile(String url, MP3 x) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        // 创建通知渠道（仅适用于Android 8.0及以上版本）
        // 发起请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                // 下载失败处理
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 下载失败处理
                    return;
                }
                File outputFile = new File(wj.mp3, x.id + ".mp3");
                File parentFile = outputFile.getParentFile();
                if (!parentFile.isDirectory()) {
                    parentFile.mkdirs();
                }
                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                try {
                    byte[] buffer = new byte[4096];
                    long fileSize = response.body().contentLength();
                    inputStream = response.body().byteStream();
                    outputStream = new FileOutputStream(outputFile);

                    int read;
                    fileSizeDownloaded = 0;
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                        fileSizeDownloaded += read;
                        // 更新通知栏进度
//                        updateNotificationProgress(context, fileSize, fileSizeDownloaded);
                        if (textView != null) {
                            main.handler.post(() ->
                                    textView.setText(x.name + ":" +
                                            (int) ((fileSizeDownloaded * 100) / fileSize)));
                        }
                    }
                    try {
                        Mp3File mp3file = new Mp3File(outputFile);
                        if (mp3file.hasId3v2Tag()) {
                            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                            // 设置新的ID值
                            gj.sc(x.name);
                            id3v2Tag.setTitle(x.name);
                            id3v2Tag.setArtist(x.zz);
                            id3v2Tag.setAlbum(x.zz);
                            id3v2Tag.setLyrics(com.muqingbfq.api.url.Lrc(x.id));
                            ByteArrayOutputStream o = new ByteArrayOutputStream();
                            if (x.picurl instanceof String) {
                                Request build = new Request.Builder().url(x.picurl)
                                        .build();
                                Response execute = client.newCall(build).execute();
                                if (execute.isSuccessful()) {
                                    id3v2Tag.setAlbumImage(execute.body().bytes()
                                            , "image/jpeg");
                                }
                            }
                            o.close();
                            mp3file.save(wj.mp3 + x.id);
                            outputFile.delete();
                        }
                        // 保存修改后的音乐文件，删除原来的文件
                    } catch (Exception e) {
                        gj.sc(e);
                        outputFile.delete();
                    }
                    dismiss();
                    // 下载完成处理
                } catch (IOException e) {
                    e.printStackTrace();
                    // 下载失败处理
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    dismiss();
                }
            }
        });
    }

    public void dismiss() {
        if (dialog == null) {
            return;
        }

        main.handler.post(() -> dialog.dismiss());
    }
}
