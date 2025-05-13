package com.muqingbfq;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class MP3 implements Serializable {
    public String id, name, zz, url;
    //    音乐的贴图
    public String picurl;
    public byte[] picdata;
    public MP3(){}


    public MP3(String id) {
        this.id = id;
    }

    public MP3(String id, String name, String zz, String picurl) {
        this.id = id;
        this.name = name;
        this.zz = zz;
        this.picurl = picurl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MP3)) return false;
        MP3 mp3 = (MP3) o;
        if (id.equals(mp3.id)) {
            return true;
        }
        return Objects.equals(id, mp3.id) &&
                Objects.equals(name, mp3.name) &&
                Objects.equals(zz, mp3.zz) &&
                Objects.equals(picurl, mp3.picurl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, zz, picurl);
    }

    @NonNull
    @Override
    public String toString() {
        return "MP3{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", zz='" + zz + '\'' +
                ", picurl=" + picurl +
                ",url=" + url +
                '}';
    }
}