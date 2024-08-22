package com.muqingbfq.login;

import com.google.gson.Gson;
import com.muqingbfq.R;
import com.muqingbfq.main;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;

public class user_message{
    public String name,qiangming, picurl;

    public user_message() {
        wj.sc(wj.filesdri + "user.mq");
    }
    public user_message(String nickname, String signature, String avatarUrl) {
        name = nickname;
        qiangming = signature;
        picurl = avatarUrl;
        String s = new Gson().toJson(new user_logs.USER(name, qiangming, picurl));
        wj.xrwb(wj.filesdri + "user.mq", s);
    }


    public string get() throws Exception {
/*        JSONObject post = wl.jsonpost("/php/user.php?action=getSpaceInfo",
                new String[]{
                        "account"
                },
                new String[]{
                        main.account
                });
        gj.sc(post);
        if (!TextUtils.isEmpty(post.toString()) &&
                post.getInt("code") == 0) {
            JSONObject data = post.getJSONObject("data");
            String headIcon = data.getString("headIcon");//头像
            String account = data.getString("account");//账号
            String userName = data.getString("userName");//名称
            String introduce = data.getString("introduce");//签名
            String cover = data.getString("cover");//背景
            if (headIcon.startsWith("..")) {
                headIcon = "https://rust.coldmint.top" + headIcon.substring(2);
            }
            if (cover.startsWith("..")) {
                cover = "https://rust.coldmint.top" + cover.substring(2);
            }
            String gender = data.getString("gender");*/
        return new string(new String[]{
                name, qiangming, picurl
        });
    }

    public static class string {
        private final String[] strings;
        public string(String[] strings) {
            this.strings = strings;
        }

        public String picurl() {
            return strings[2];
        }

        public String qianming() {
            return strings[1];
        }

        public String userName() {
            return strings[0];
        }
    }
}
