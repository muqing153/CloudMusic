package com.muqingbfq;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqingbfq.fragment.wode;
import com.muqingbfq.login.user_logs;
import com.muqingbfq.mq.EditViewDialog;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wl;

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
//        Cookie();
        One();

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
            }else if (which == 1) {
                dlintent.launch(new Intent(home, user_logs.class));
            }
        });
        materialAlertDialogBuilder.show();
    }

    /**
     * 验证Cookie弹窗
     */
    public void Cookie() {
        EditViewDialog editViewDialog = new EditViewDialog(home, "登陆");
        editViewDialog.setMessage("请输入Cookie:");
//        editViewDialog.setPositive()
        editViewDialog.show();

    }
    public void Yes(){

    }
}
