package com.muqingbfq;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqingbfq.fragment.wode;
import com.muqingbfq.login.user_logs;
import com.muqingbfq.mq.EditViewDialog;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wl;
import com.muqingbfq.view.Edit;

import org.json.JSONObject;

public class HomeSteer {
    home home;
    ActivityResultLauncher<Intent> dlintent;

    public HomeSteer(home home) {
        this.home = home;
        dlintent = home.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 处理返回结果
                        Intent data = result.getData();
                        boolean bool = data.getBooleanExtra("bool", false);
                        if (bool) {
                            Yes();
                            return;
                        }
                    }
                    One();
                });
        SetIP();
//        One();

    }

    public void One() {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(home);
        materialAlertDialogBuilder.setTitle("引导登陆");
        materialAlertDialogBuilder.setItems(new String[]{"游客", "登陆"}, (dialog, which) -> {
            if (which == 0) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        //获取游客Cookie
                        String hq = wl.hq("/register/anonimous");
                        try {
                            JSONObject jsonObject = new JSONObject(hq);
                            wl.setcookie(jsonObject.getString("cookie"));
                            home.runOnUiThread(() -> Yes());
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
    }

    public void Yes() {

    }

    /**
     * 设置IP地址
     */
    public void SetIP() {
        SharedPreferences nickname = home.getSharedPreferences("Set_up", Context.MODE_PRIVATE);
        if (nickname.getString("IP", "").isEmpty()) {
            EditViewDialog editViewDialog = new EditViewDialog(home, "IP");
            editViewDialog.setMessage("请输入部署了NeteaseCloudMusicApi的服务器地址，\n例如" +
                    "https://api.csm.sayqz.com");
//        editViewDialog.setPositive()
            editViewDialog.buttonb.setEnabled(false);
            editViewDialog.editText.setMaxLines(1);
            editViewDialog.editText.addTextChangedListener(new Edit.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence var1, int var2, int var3, int var4) {

                }

                @Override
                public void onTextChanged(CharSequence var1, int var2, int var3, int var4) {
                    //正则表达式检查是否为 https://api.csm.sayqz.com这样的
                    editViewDialog.buttonb.setEnabled(var1.toString().matches("^(https?://).+[^/]"));
                }

                @Override
                public void afterTextChanged(Editable var1) {

                }
            });
            editViewDialog.setPositive(v -> {
                main.api = editViewDialog.getEditText();
                nickname.edit().putString("IP", editViewDialog.getEditText()).apply();
                One();
                editViewDialog.dismiss();
            });
            editViewDialog.show();
        } else {
            main.api = nickname.getString("IP", "");
            One();
        }
    }
}
