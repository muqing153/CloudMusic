package com.muqingbfq.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.muqing.AppCompatActivity;
import com.muqing.gj;
import com.muqingbfq.databinding.ActivityUserLogsBinding;
import com.muqingbfq.main;
import com.muqingbfq.mq.FilePath;
import com.muqingbfq.mq.wl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
public class user_logs extends AppCompatActivity<ActivityUserLogsBinding> {
    @Override
    protected ActivityUserLogsBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityUserLogsBinding.inflate(layoutInflater);
    }

    public static class USER {
        public String name, qianming;
        public Object picUrl;

        public USER(String user, String qianming, Object picUrl) {
            this.name = user;
            this.qianming = qianming;
            this.picUrl = picUrl;
        }
    }

    erweima thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.login.setOnClickListener(view1 -> {
            if (!TextUtils.isEmpty(binding.editUser.getText())) {
                wl.setcookie(binding.editUser.getText().toString());
            }
            new Thread(() -> {
//                gj.sc(wl.Cookie);
                String hq = wl.hq("/login/status", null);
                try {
                    JSONObject jsonObject = new JSONObject(hq);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONObject account1 = data.getJSONObject("account");
                    finish(true);

                } catch (JSONException e) {
                    gj.sc(e);
                    runOnUiThread(() -> {
                        gj.ycjp(binding.editUser);
                        Toast.makeText(user_logs.this, "不成功的Cookie登陆", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });
        binding.button1.setOnClickListener(view -> {
            if (binding.layout1.getVisibility() == View.VISIBLE) {
                binding.layout1.setVisibility(View.GONE);
                binding.layout2.setVisibility(View.VISIBLE);
                binding.button1.setText("账号");
                thread = new erweima();
                thread.start();
            } else {
                thread.interrupt();
                binding.button1.setText("二维码");
                binding.layout1.setVisibility(View.VISIBLE);
                binding.layout2.setVisibility(View.GONE);
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(false);
            }
        });
    }
    //some statement

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(false);
        }
        return super.onOptionsItemSelected(item);
    }

    public void finish(boolean aBoolean) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("bool", aBoolean);
        setResult(RESULT_OK, resultIntent);
        super.finish();
    }


    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            gj.sc(e);
        }
        return bitmap;
    }

    public String account, password;

    class CloudUser extends Thread {
        public CloudUser() {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View v = getWindow().peekDecorView();
            if (null != v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            user_logs.this.account = binding.editUser.getText().toString();
//            gj.xcts(user_logs.this, "设置成功");
            wl.setcookie(account);
            finish();
//            start();
        }

        @Override
        public void run() {
            super.run();
            try {
                String hq = wl.hq("/login/cellphone", "phone=" + account + "&password=" + password, false);
                if (TextUtils.isEmpty(hq)) {
                    return;
                }
                JSONObject jsonObject = new JSONObject(hq);
                int code = jsonObject.getInt("code");
                if (code == 200) {
                    JSONObject data = jsonObject.getJSONObject("profile");
                    String nickname = data.getString("nickname");//用户名
                    String avatarUrl = data.getString("avatarUrl");//用户头像
                    String signature = data.getString("signature");//用户签名
                    String cookie = jsonObject.getString("cookie");

                    String s = new Gson().toJson(new user_logs.USER(nickname, signature, avatarUrl));
                    FilePath.xrwb(FilePath.filesdri + "user.mq", s);
                    user_logs.this.finish(true);
                } else if (code == 502) {
                    String nickname = jsonObject.getString("message");
                    runOnUiThread(() -> gj.ts(user_logs.this, nickname));
                } else {
                    runOnUiThread(() -> gj.ts(user_logs.this, "找不到此账号"));
                }
            } catch (Exception e) {
                gj.sc(e);
            }
        }
    }

    class erweima extends Thread {
        int code = 800;
        String unikey, qrimg, hq;
        private long time = 0;

        public erweima() {
            binding.text1.setText("请使用网易云音乐扫码");
        }

        @Override
        public void run() {
            super.run();
            while (code != 0 && !Thread.currentThread().isInterrupted()) {
                gj.sc(code);
                try {
                    hq = wl.hq("/login/qr/check", "key=" + unikey + Time(), false);
                    if (hq != null) {
                        JSONObject json = new JSONObject(hq);
                        code = json.getInt("code");
                        switch (code) {
                            case 800:
                            case 400:
                                setwb("二维码过期");
                                hqkey();
                                break;
                            case 801:
                                setwb("等待扫码");
                                break;
                            case 802:
                                setwb("等待确认");
                                break;
                            case 803:
                                setwb("登录成功");
//                                wl.setcookie(json.getString("cookie"));
                                code = 0;
                                user_logs.this.finish(true);
                                break;
                            default:
                                code = 0;
                                // 默认情况下的操作
                                break;
                        }
                    }
                    sleep(1000);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    gj.sc(e);
                }
            }
        }

        private void hqkey() throws Exception {
            unikey = new JSONObject(Objects.requireNonNull(wl.hq("/login/qr/key", null, false))).
                    getJSONObject("data").getString("unikey");
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(wl.hq("/login/qr/create", "key=" +
                    unikey +
                    "&qrimg=base64", false)));
            qrimg = jsonObject.getJSONObject("data").getString("qrimg");
            main.handler.post(() -> binding.image.setImageBitmap(user_logs.stringToBitmap(qrimg)));
        }

        private String Time() {
            if (time < System.currentTimeMillis() - 1000) {
                time = System.currentTimeMillis();
            }
            return "&timestamp" + time;
        }

        private void setwb(String wb) {
            main.handler.post(() -> binding.text1.setText(wb));
        }
    }

}