package com.muqingbfq.mq;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqing.AppCompatActivity;
import com.muqing.gj;
import com.muqingbfq.R;
import com.muqingbfq.databinding.ActivityLlqBinding;
import com.muqingbfq.databinding.ViewDownloadBinding;
import com.muqingbfq.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class llq extends AppCompatActivity<ActivityLlqBinding> {
    WebView web;

    @Override
    protected ActivityLlqBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityLlqBinding.inflate(layoutInflater);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        Intent intent = getIntent();
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        web = binding.webview;
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
//        禁用缓存
        web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setTitle(web.getTitle());
                // 在这里获取到了网页的标题
            }
        });
        web.setDownloadListener((url1, userAgent, contentDisposition, mimetype, contentLength) -> {
            String size = "0B";
            if (contentLength > 0) {
                final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
                int digitGroups = (int) (Math.log10(contentLength) / Math.log10(1024));
                size = String.format(Locale.getDefault(), "%.1f %s", contentLength
                        / Math.pow(1024, digitGroups), units[digitGroups]);
            }
            final String filename = url1.substring(url1.lastIndexOf('/') + 1);
            new MaterialAlertDialogBuilder(llq.this)
                    .setTitle(filename)
                    .setMessage("文件链接：" + url1 +
                            "\n文件大小：" + size)
                    .setNegativeButton("取消", null)
                    .setNegativeButton("下载", (dialogInterface, i) -> {
                        // 检查权限
                        if (ContextCompat.checkSelfPermission(llq.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // 如果没有写入存储的权限，则请求权限
                            ActivityCompat.requestPermissions(llq.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    1);
                        } else {
                            ViewDownloadBinding inflate = ViewDownloadBinding.inflate(getLayoutInflater());
                            AlertDialog show = new MaterialAlertDialogBuilder(llq.this)
                                    .setTitle(String.format(Locale.getDefault()
                                            , "文件名称：%s", filename))
                                    .setMessage(String.format(Locale.getDefault()
                                            , "下载路径：%s", Environment.
                                                    getExternalStoragePublicDirectory(
                                                            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()))
                                    .setView(inflate.getRoot())
                                    .show();
                            OkHttpClient okHttpClient = new OkHttpClient();
                            Request build = new Request.Builder()
                                    .url(url1)
                                    .build();
                            okHttpClient.newCall(build).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    InputStream inputStream = response.body().byteStream();
                                    long l = response.body().contentLength();
                                    File file = new File(Environment.getExternalStoragePublicDirectory(
                                            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),
                                            filename);
                                    FileOutputStream fileOutputStream = null;
                                    try {
                                        int read;
                                        fileOutputStream = new FileOutputStream(file);
                                        byte[] bytes = new byte[2048];
                                        long downloadedSize = 0;
                                        while ((read = inputStream.read(bytes)) != -1) {
                                            fileOutputStream.write(bytes, 0, read);
                                            downloadedSize += read;
                                            int progress = (int) ((100 * downloadedSize) / l);
                                            main.handler.post(() -> inflate.textview.setText(
                                                    String.format(Locale.getDefault(),
                                                            "%d%%", progress)));
                                        }
                                        fileOutputStream.close();
                                    } catch (Exception e) {
                                        gj.sc(e);
                                    } finally {
                                        if (fileOutputStream != null) {
                                            fileOutputStream.close();
                                        }
                                    }
                                    main.handler.post(() -> {
                                        gj.ts(llq.this, "下载完成");
                                        show.dismiss();
                                    });
                                }
                            });
                        }
                    }).show();
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    binding.webViewProgressBar.setVisibility(View.GONE);
                } else {
                    binding.webViewProgressBar.setProgress(newProgress);
                    if (!binding.webViewProgressBar.isShown()) {
                        binding.webViewProgressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        loadUrl(intent.getStringExtra("url"));
    }

    private void loadUrl(String url) {
        web.loadUrl(url);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 权限已授予，执行文件下载操作
            gj.ts(this, "权限已授予，请重新执行文件下载操作");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.llq, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) {
            web.goBack();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.fx) {
            gj.fx(this, web.getUrl());
//                    服务中心
        } else if (itemId == R.id.sx) {
            web.reload();
        } else if (itemId == R.id.menu_web) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(web.getUrl())));
        }
        return super.onOptionsItemSelected(item);
    }

}
