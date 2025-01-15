package com.muqingbfq.login;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.muqingbfq.mq.wj;
import com.muqingbfq.mq.wl;
import com.muqingbfq.yc;

import org.json.JSONException;
import org.json.JSONObject;

public class visitor extends Thread {
    public visitor() {
        start();
    }
    @Override
    public void run() {
        super.run();
        String hq = wl.hq("/register/anonimous");
        try {
            JSONObject jsonObject = new JSONObject(hq);
            wl.setcookie(jsonObject.getString("cookie"));
        } catch (Exception e) {
            com.muqingbfq.mq.gj.sc(e);
        }
    }
}
