package com.muqingbfq.mq;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.muqingbfq.MP3;
import com.muqingbfq.home;
import com.muqingbfq.yc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class wj {
    public static String filesdri;
    public static String mp3 = "mp3/";
    public static String lishi_json = "lishi.json";
    public static String gd = "gd/";
    public static String tx = "image/";
    public static String gd_json = "gd.json", gd_xz = "gd_xz.json",
            gd_phb = "gd_phb.json", mp3_like = "mp3_like.json";

    public wj(Context context) {
        try {
            wj.filesdri = context.getExternalFilesDir("").getAbsolutePath() + "/";
//                context.getFilesDir().toString() + "/";
            gd_json = filesdri + gd_json;
            mp3 = filesdri + mp3;
            gd = filesdri + gd;
            gd_xz = filesdri + gd_xz;
            gd_phb = filesdri + gd_phb;
            mp3_like = gd + mp3_like;
            tx = filesdri + tx;
        } catch (Exception e) {
            yc.start(context, e);
        }
    }

    /*
     * 这里定义的是一个文件保存的方法，写入到文件中，所以是输出流
     * */
    public static boolean xrwb(String url, String text) {
        if (text == null) {
            text = "";
        }
        File file = new File(url);
//如果文件不存在，创建文件
        try {
            File parentFile = file.getParentFile();
            if (!parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            if (!file.exists())
                file.createNewFile();
//创建FileOutputStream对象，写入内容
            FileOutputStream fos = new FileOutputStream(file);
//向文件中写入内容
            fos.write(text.getBytes());
            fos.close();
        } catch (Exception e) {
            gj.sc(e);
        }
        return false;
    }

    public static String dqwb(String url) {
        try {
            File file = new File(url);
            if (!file.exists()) {
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder str = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                str.append(line);
            }
            br.close();
            fis.close();
            return str.toString();
        } catch (Exception e) {
            gj.sc(e);
        }
        return null;
    }

    public static boolean cz(String url) {
        return new File(url).exists();
    }


    public static boolean sc(String url) {
        File file = new File(url);
        return file.delete();
    }

    public static void sc(File url) {
        if (url.exists()) {
            File[] files = url.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 递归调用，删除子文件夹及其内容
                        sc(file);
                    } else {
                        file.delete(); // 删除文件
                    }
                }
            }
            url.delete(); // 删除当前文件夹
        }
    }


    public String convertToMd5(String url) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(url.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte value : messageDigest) {
                String hex = Integer.toHexString(0xFF & value);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void fz(String sourceFilePath, String targetFilePath) {
        File sourceFile = new File(sourceFilePath);
        File targetFile = new File(targetFilePath);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try (InputStream in = Files.newInputStream(sourceFile.toPath());
                 OutputStream out = Files.newOutputStream(targetFile.toPath())) {
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buf)) > 0) {
                    out.write(buf, 0, bytesRead);
                }
                // 文件复制完成
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 保存MP3对象到文件
    public static void setMP3ToFile(MP3 mp3) {
        if (mp3 == null) {
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(mp3);
        xrwb(filesdri + "mp3.dat", json);
    }

    // 从文件中加载MP3对象
    public static MP3 getMP3FromFile() {
        Gson gson = new Gson();
        MP3 mp3 = null;
        try {
            File file = new File(filesdri + "mp3.dat");
            if (file.exists() && file.length() > 0) {
                FileReader reader = new FileReader(file);
                mp3 = gson.fromJson(reader, MP3.class);
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mp3;
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static boolean isCD(Activity context) {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                context.startActivity(intent);
            } else {
                Log.i("swyLog", "Android 11以上，当前已有权限");
                return true;
            }
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    ActivityCompat.requestPermissions(context
                            , PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                } else {
                    Log.i("swyLog", "Android 6.0以上，11以下，当前已有权限");
                    return true;
                }
            } else {
                Log.i("swyLog", "Android 6.0以下，已获取权限");
                return true;
            }
        }
        return false;
    }
}
