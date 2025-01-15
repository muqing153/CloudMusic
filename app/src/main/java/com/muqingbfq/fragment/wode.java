package com.muqingbfq.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.muqingbfq.R;
import com.muqingbfq.XM;
import com.muqingbfq.api.playlist;
import com.muqingbfq.api.resource;
import com.muqingbfq.databinding.FragmentWdBinding;
import com.muqingbfq.login.user_logs;
import com.muqingbfq.login.visitor;
import com.muqingbfq.main;
import com.muqingbfq.mq.EditViewDialog;
import com.muqingbfq.mq.Fragment;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;
import com.muqingbfq.mq.wl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class wode extends Fragment<FragmentWdBinding> {
    public TextView name, jieshao;
    public ImageView imageView;
    private final Object[][] lista = {
            {R.drawable.mdimusicbox, "最近播放", "mp3_hc.json"},
            {R.drawable.download, "下载音乐", "mp3_xz.json"},
            {R.drawable.mdialbum, "喜欢音乐", "mp3_like.json"},
            {R.drawable.filesearc, "本地搜索", "cd.json"},
            {R.drawable.api, "更换接口", "API"},
            {R.drawable.gd, "导入歌单", "gd"},
            {R.drawable.paihangbang, "排行榜", "排行榜"},
            {R.drawable.ic_launcher_foreground, "开发中", ""}
    };

    @Override
    protected FragmentWdBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWdBinding.inflate(inflater, container, false);
    }

    @Override
    public void setUI(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                      @Nullable Bundle savedInstanceState) {
        name = binding.text1;
        jieshao = binding.text2;
        imageView = binding.imageView;
        binding.cardview.setOnClickListener(new dl());

        binding.recyclerview1.setNestedScrollingEnabled(false);
        binding.recyclerview1.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.recyclerview1.setFocusable(false);
        binding.recyclerview1.setAdapter(new RecyclerView.Adapter<VH>() {
            @NonNull
            @Override
            public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View inflate = View.inflate(getContext(), R.layout.view_button, null);
                return new VH(inflate);
            }

            @Override
            public void onBindViewHolder(@NonNull VH holder, int position) {
                String s = lista[position][1].toString();
                holder.textView.setText(s);
                holder.imageView.setImageResource((Integer) lista[position][0]);
                String data = lista[position][2].toString();
                holder.itemView.setOnClickListener(view -> {
                    switch (data) {
                        case "cd.json":
                        case "mp3_hc.json":
                        case "mp3_xz.json":
                        case "mp3_like.json":
                            Intent a = new Intent(getContext(), com.muqingbfq.fragment.mp3.class);
                            a.putExtra("id", data);
                            a.putExtra("name", s);
                            getContext().startActivity(a);
                            break;
                        case "排行榜":
                            gd.start(getActivity(), new String[]{data, s});
                            break;
                        case "API":
                            EditViewDialog editViewDialog = new EditViewDialog(getContext(), "更换接口API")
                                    .setMessage("当前接口：\n" + main.api);
                            editViewDialog.setPositive(view1 -> {
                                String str = editViewDialog.getEditText();
                                boolean http = str.startsWith("http");
                                if (str.isEmpty() || !http) {
                                    gj.ts(getContext(), "请输入正确的api");
                                } else {
                                    gj.ts(getContext(), "更换成功");
                                    main.api = str;
                                    wj.xrwb(wj.filesdri + "API.mq", main.api);
                                    editViewDialog.dismiss();
                                }
                            }).show();
                            break;
                        case "gd":
                            EditViewDialog editViewDialog1 = new EditViewDialog(getContext(),
                                    "导入歌单")
                                    .setMessage("请用网易云https链接来进行导入或者歌单id");
                            editViewDialog1.setPositive(view1 -> {
                                String str = editViewDialog1.getEditText();
                                // 使用正则表达式提取链接
                                Pattern pattern = Pattern.compile("https?://[\\w./?=&]+");
                                Matcher matcher = pattern.matcher(str);
                                if (matcher.find())
                                    str = matcher.group();
                                if (!str.isEmpty()) {
                                    // 使用截取方法获取歌单 ID
                                    str = str.substring(str.indexOf("id=") + 3, str.indexOf("&"));
                                }
                                String finalStr = str;
                                gj.ts(getContext(), "导入中");
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        String hq = playlist.gethq(finalStr);
                                        if (hq != null) {
                                            try {
                                                XM fh = resource.Playlist_content(finalStr);
                                                JSONObject json = new JSONObject(hq);
                                                json.put("name", fh.name);
                                                json.put("picUrl", fh.picurl);
                                                json.put("message", fh.message);
//                                                        json.put(fh.id, json);
                                                wj.xrwb(wj.gd + finalStr, json.toString());
//                                                addlist(fh);
                                            } catch (JSONException e) {
                                                gj.sc("list gd onclick thear " + e);
                                            }
                                        }
                                    }
                                }.start();
                                editViewDialog1.dismiss();
                            }).show();
                            break;
                    }
                });
            }

            @Override
            public int getItemCount() {
                return lista.length;
            }
        });

        binding.recyclerview2.setNestedScrollingEnabled(false);
        binding.recyclerview2.setLayoutManager(new LinearLayoutManager(getContext()));
//        adaper = new baseadapter();
//        binding.recyclerview2.setAdapter(adaper);
//        sx();
        new threadLogin().start();
    }

    ActivityResultLauncher<Intent> dlintent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // 处理返回结果
                    Intent data = result.getData();
                    boolean bool = data.getBooleanExtra("bool", false);
                    if (bool) {
//                        gj.sc("dl");
                        new threadLogin().start();
                    }
                    // ...
                }
            });

    class dl implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            File file = new File(wj.filesdri, "user.mq");
            if (file.exists()) {
                String[] a = new String[]{"退出登录"};
                new MaterialAlertDialogBuilder(getContext())
                        .setItems(a, (dialogInterface, i) -> {
                            boolean delete = file.delete();
                            if (delete) {
                                wj.sc(wj.filesdri + "user.mq");
                                binding.text1.setText(getString(R.string.app_name));
                                binding.text2.setText(getString(R.string.app_name));
                                imageView.setImageResource(R.drawable.ic_launcher_foreground);
                                new visitor();//游客模式
                                wj.sc(wj.filesdri + "user.mq");
//                                new com.muqingbfq.login.user_message();
                            }
                        }).show();
            } else {
                dlintent.launch(new Intent(getContext(), user_logs.class));
            }
        }
    }


    class VH extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public VH(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.text1);
        }
    }

    //登陆 获取用户信息 头像 昵称 签名
    class threadLogin extends Thread {

        public void run() {
            String hq = wl.hq("/user/account?cookie=" + wl.Cookie);
            if (hq != null) {
                try {
                    JSONObject jsonObject = new JSONObject(hq);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        JSONObject profile = jsonObject.getJSONObject("profile");
                        String nickname = profile.getString("nickname");
                        String avatarUrl = profile.getString("avatarUrl");
                        String signature = profile.getString("signature");
                        requireActivity().runOnUiThread(() -> {
                            binding.text1.setText(nickname);
                            binding.text2.setText(signature);
                            Glide.with(getContext())
                                    .load(avatarUrl)
                                    .error(R.drawable.ic_launcher_foreground)
                                    .into(binding.imageView);
                        });
                        wj.xrwb(wj.filesdri + "user.mq", new Gson().toJson(new user_logs.USER(nickname, signature, avatarUrl)));
                    }
                } catch (Exception e) {
                    gj.sc(e);
                }
            } else {
                if (!wj.cz(wj.filesdri + "user.mq")) {
                    return;
                }
                String dqwb = wj.dqwb(wj.filesdri + "user.mq");
                user_logs.USER user = new Gson().fromJson(dqwb, user_logs.USER.class);
                requireActivity().runOnUiThread(() -> {
                    binding.text1.setText(user.name);
                    binding.text2.setText(user.qianming);
                    Glide.with(getContext())
                            .load(user.picUrl)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(binding.imageView);
                });
            }
        }
    }
}
