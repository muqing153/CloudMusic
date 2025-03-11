package com.muqingbfq.api;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.media3.common.MediaItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.muqingbfq.main;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;

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
    Activity context;

    public FileDownloader(Activity context) {
        this.context = context;
        textView = new TextView(context);
        dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("下载音乐")
                .setView(textView)
                .show();
    }

    long fileSizeDownloaded = 0;

    public void downloadFile(String url, MediaItem x) {
        if (wj.cz(new File(wj.mp3, x.mediaId).toString())) {
            dialog.dismiss();
            dialog = new MaterialAlertDialogBuilder(context)
                    .setTitle("下载音乐").setMessage("已存在")
                    .setPositiveButton("确定", null)
                    .show();
            return;
        }

        Request request = new Request.Builder()
                .url(url)
                .build();
        // 创建通知渠道（仅适用于Android 8.0及以上版本）
        // 发起请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                gj.sc("下载失败 ：" + e);
                // 下载失败处理
            }

            /** @noinspection ResultOfMethodCallIgnored*/
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 下载失败处理
                    return;
                }
                File outputFile = new File(wj.mp3, x.mediaId + ".mp3");
                File parentFile = outputFile.getParentFile();
                if (!parentFile.isDirectory()) {
                    parentFile.mkdirs();
                }
                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                try {
                    CharSequence title = x.mediaMetadata.title;
                    CharSequence artist = x.mediaMetadata.artist;
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
                                    textView.setText(title + ":" +
                                            (int) ((fileSizeDownloaded * 100) / fileSize)));
                        }
                    }
                    try {
                        Mp3File mp3file = new Mp3File(outputFile);
                        if (mp3file.hasId3v2Tag()) {
                            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                            // 设置新的ID值
//                            gj.sc(x.name);
                            id3v2Tag.setTitle(title.toString());
                            id3v2Tag.setArtist(artist.toString());
                            id3v2Tag.setAlbum(artist.toString());
                            id3v2Tag.setLyrics(com.muqingbfq.api.url.Lrc(x.mediaId));
                            ByteArrayOutputStream o = new ByteArrayOutputStream();
                            String artworkUri = x.mediaMetadata.artworkUri.toString();
                            Request build = new Request.Builder().url(artworkUri)
                                    .build();
                            Response execute = client.newCall(build).execute();
                            if (execute.isSuccessful()) {
                                id3v2Tag.setAlbumImage(execute.body().bytes()
                                        , "image/jpeg");
                            }
                            o.close();
                            mp3file.save(wj.mp3 + x.mediaId);
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
                    gj.sc("下载处理失败：" + e);
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
        context.runOnUiThread(() -> dialog.dismiss());
    }
}
