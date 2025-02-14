package com.muqingbfq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqingbfq.Dialog.DialogEditText;
import com.muqingbfq.login.user_logs;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wl;
import com.muqingbfq.view.Edit;
import org.json.JSONObject;
import java.util.Objects;

public class HomeSteer {
    home home;
    ActivityResultLauncher<Intent> dlintent;

    Runnable runnable;

    public HomeSteer(home home, Runnable runnable) {
        this.runnable = runnable;
        this.home = home;
        dlintent = home.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 处理返回结果
                        Intent data = result.getData();
                        boolean bool = Objects.requireNonNull(data).getBooleanExtra("bool", false);
                        if (bool) {
                            runnable.run();
                            return;
                        }
                    }
                    One();
                });
        SetIP();
//        One();

    }

    public void One() {
        wl.getCookie();
        if (wl.Cookie.isEmpty()) {
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(home);
            materialAlertDialogBuilder.setTitle("引导登陆");
            materialAlertDialogBuilder.setItems(new String[]{"游客", "登陆"}, (dialog, which) -> {
                if (which == 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            //获取游客Cookie
                            String hq = wl.hq("/register/anonimous", null);
                            try {
                                JSONObject jsonObject = new JSONObject(hq);
                                wl.setcookie(jsonObject.getString("cookie"));
                                home.runOnUiThread(runnable);
                            } catch (Exception e) {
                                home.runOnUiThread(() -> Toast.makeText(home, "游客登陆失败:" + e.getMessage(), Toast.LENGTH_SHORT).show());
                                gj.sc(e);
                            }
                        }
                    }.start();
                } else if (which == 1) {
                    dlintent.launch(new Intent(home, user_logs.class));
                }
            });
            materialAlertDialogBuilder.show();
        } else {
            runnable.run();
        }
    }


    String[] stringIp = new String[]{"https://ncm.nekogan.com", "https://api.csm.sayqz.com"};

    /**
     * 设置IP地址
     */
    public void SetIP() {
        SharedPreferences nickname = home.getSharedPreferences("Set_up", Context.MODE_PRIVATE);
        main.api = nickname.getString("IP", "");
        if (TextUtils.isEmpty(main.api)) {

            DialogEditText dialogEditText = getDialogEditText(nickname);
            dialogEditText.binding.edittext.addTextChangedListener(new Edit.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence var1, int var2, int var3, int var4) {

                }

                @Override
                public void onTextChanged(CharSequence var1, int var2, int var3, int var4) {
                    //正则表达式检查是否为 https://api.csm.sayqz.com这样的
                    dialogEditText.binding.Yes.setEnabled(var1.toString().matches("^(https?://).+[^/]"));
                }

                @Override
                public void afterTextChanged(Editable var1) {

                }
            });
            dialogEditText.show();
        } else {
            One();
        }
    }

    @NonNull
    private DialogEditText getDialogEditText(SharedPreferences nickname) {
        DialogEditText dialogEditText = new DialogEditText(home, stringIp);
        dialogEditText.setTitle("IP");
        dialogEditText.setMessage("请输入部署了NeteaseCloudMusicApi的服务器地址");
        dialogEditText.setPositiveButton((view) -> {
            main.api = dialogEditText.binding.edittext.getText().toString();
            nickname.edit().putString("IP", main.api).apply();
            One();
            dialogEditText.alertDialog.dismiss();

        });
        dialogEditText.binding.Yes.isEnabled();
        dialogEditText.setNegativeButton((view) -> home.finish());
        return dialogEditText;
    }
}
