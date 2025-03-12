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
import com.google.gson.reflect.TypeToken;
import com.muqing.gj;
import com.muqingbfq.R;
import com.muqingbfq.XM;
import com.muqingbfq.adapter.AdapterGd;
import com.muqingbfq.adapter.AdapterGdH;
import com.muqingbfq.databinding.FragmentWdBinding;
import com.muqingbfq.login.user_logs;
import com.muqingbfq.main;
import com.muqingbfq.mq.EditViewDialog;
import com.muqingbfq.mq.Fragment;
import com.muqingbfq.mq.FilePath;
import com.muqingbfq.mq.wl;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class wode extends Fragment<FragmentWdBinding> {
    public TextView name, jieshao;
    public ImageView imageView;
    private final Object[][] lista = {
            {R.drawable.mdimusicbox, "最近播放", "mp3_listHistory.json"},
            {R.drawable.download, "下载音乐", "mp3_xz.json"},
            {R.drawable.mdialbum, "喜欢音乐", "mp3_like.json"},
            {R.drawable.filesearc, "本地搜索", "cd.json"},
            {R.drawable.api, "更换接口", "API"},
            {R.drawable.gd, "导入歌单", "gd"},
            {R.drawable.paihangbang, "排行榜", "排行榜"},
            {R.drawable.menu, "开发中", ""}
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
                        case "mp3_listHistory.json":
                        case "mp3_xz.json":
                        case "mp3_like.json":
                            Intent a = new Intent(getContext(), com.muqingbfq.fragment.mp3.class);
                            a.putExtra("id", data);
                            a.putExtra("name", s);
                            requireContext().startActivity(a);
                            break;
                        case "排行榜":
                            gd.start(getActivity(), new String[]{data, s});
                            break;
                        case "API":
                            EditViewDialog editViewDialog = new EditViewDialog(requireContext(), "更换接口API")
                                    .setMessage("当前接口：\n" + main.api);
                            editViewDialog.setPositive(view1 -> {
                                String str = editViewDialog.getEditText();
                                boolean http = str.startsWith("http");
                                if (str.isEmpty() || !http) {
                                    gj.ts(getContext(), "请输入正确的api");
                                } else {
                                    gj.ts(getContext(), "更换成功");
                                    main.api = str;
                                    FilePath.xrwb(FilePath.filesdri + "API.mq", main.api);
                                    editViewDialog.dismiss();
                                }
                            }).show();
                            break;
                        case "gd":
                            EditViewDialog editViewDialog1 = new EditViewDialog(requireContext(),
                                    "导入歌单")
                                    .setMessage("请用网易云https链接来进行导入或者歌单id");
                            editViewDialog1.setPositive(view1 -> {
                                view1.setEnabled(false);
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
                                new AdapterGd.baocun(finalStr){
                                    @Override
                                    public void Yes() {
                                        requireActivity().runOnUiThread(() -> {
                                            gj.ts(getContext(), "导入成功");
                                            editViewDialog1.dismiss();
                                        });
                                    }
                                };
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
        load = new Load(this);
        LoadPlaylists();
        new threadLogin().start();
    }

    public static Load load;
    ActivityResultLauncher<Intent> dlintent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // 处理返回结果
                    Intent data = result.getData();
                    if (data != null) {
                        boolean bool = data.getBooleanExtra("bool", false);
                        if (bool) {
                            new threadLogin().start();
                        }
                    }
                }
            });

    class dl implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            File file = new File(FilePath.filesdri, "user.mq");
            if (file.exists()) {
                String[] a = new String[]{"退出登录"};
                new MaterialAlertDialogBuilder(requireContext())
                        .setItems(a, (dialogInterface, i) -> {
                            boolean delete = file.delete();
                            if (delete) {
                                FilePath.sc(FilePath.filesdri + "user.mq");
                                binding.text1.setText(getString(R.string.app_name));
                                binding.text2.setText(getString(R.string.app_name));
                                imageView.setImageResource(R.drawable.ic_launcher_foreground);
                                FilePath.sc(FilePath.filesdri + "user.mq");
//                                new com.muqingbfq.login.user_message();
                            }
                        }).show();
            } else {
                dlintent.launch(new Intent(getContext(), user_logs.class));
            }
        }
    }


    private static class VH extends RecyclerView.ViewHolder {
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
            String hq = wl.hq("/user/account", null);
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
                            Glide.with(requireContext())
                                    .load(avatarUrl)
                                    .error(R.drawable.ic_launcher_foreground)
                                    .into(binding.imageView);
                        });
                        FilePath.xrwb(FilePath.filesdri + "user.mq", new Gson().toJson(new user_logs.USER(nickname, signature, avatarUrl)));
                    }
                } catch (Exception e) {
                    gj.sc(e);
                }
            } else {
                if (!FilePath.cz(FilePath.filesdri + "user.mq")) {
                    return;
                }
                String dqwb = FilePath.dqwb(FilePath.filesdri + "user.mq");
                user_logs.USER user = new Gson().fromJson(dqwb, user_logs.USER.class);
                requireActivity().runOnUiThread(() -> {
                    binding.text1.setText(user.name);
                    binding.text2.setText(user.qianming);
                    Glide.with(requireContext())
                            .load(user.picUrl)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(binding.imageView);
                });
            }
        }
    }


    public static class Load{
        private final wode wode;
        public Load(wode wode) {
            this.wode = wode;
        }
        public void run(){
            wode.LoadPlaylists();
        }
    }
    public void LoadPlaylists() {
        File file = new File(FilePath.gd_xz);
        if (file.exists()) {
            String dqwb = FilePath.dqwb(FilePath.gd_xz);
            Gson gson = new Gson();
            TypeToken<List<XM>> typeToken = new TypeToken<List<XM>>() {
            };
            AdapterGdH adapterGdH = new AdapterGdH();
            adapterGdH.list = gson.fromJson(dqwb, typeToken.getType());
            requireActivity().runOnUiThread(() -> {
                binding.recyclerview2.setAdapter(adapterGdH);
                binding.recyclerview2Text.setVisibility(adapterGdH.list.isEmpty() ? View.VISIBLE : View.GONE);
            });
        }else{
            requireActivity().runOnUiThread(() -> binding.recyclerview2Text.setVisibility(View.VISIBLE));
        }
    }

    //在每次可见的时候加载
    @Override
    public void onResume() {
        super.onResume();
//        LoadPlaylists();
    }
}
