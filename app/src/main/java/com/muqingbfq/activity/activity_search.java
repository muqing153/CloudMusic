package com.muqingbfq.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.core.graphics.Insets;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqing.AppCompatActivity;
import com.muqing.BaseAdapter;
import com.muqing.Dialog.DialogEditText;
import com.muqing.gj;
import com.muqing.wj;
import com.muqingbfq.databinding.ActivitySearchBinding;
import com.muqingbfq.databinding.ListTextBinding;
import com.muqingbfq.databinding.ViewSearchItemBinding;
import com.muqingbfq.fragment.SearchTools;
import com.muqingbfq.mq.FilePath;
import com.muqingbfq.mq.TaskAction;
import com.muqingbfq.mq.wl;
import com.muqingbfq.view.Edit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class activity_search extends AppCompatActivity<ActivitySearchBinding> {
    private final List<String> list = new ArrayList<>();

    public static void start(Activity context, View view) {
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(context, view, "edit");
        context.startActivity(new Intent(context, activity_search.class), options.toBundle());
    }
    public void setOnApplyWindowInsetsListener(Insets systemBars, View v) {
//        v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        binding.endBar.setOnClickListener((DialogEditText.OnClickListener) view -> BackPressed());
        binding.searchList.setLayoutManager(new LinearLayoutManager(this));
        int getbackgroundColor = gj.getbackgroundColor(this);
        binding.searchList.setBackgroundColor(getbackgroundColor);
        binding.line3.setBackgroundColor(getbackgroundColor);
        binding.edit.addTextChangedListener(new Edit.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence var1, int var2, int var3, int var4) {

            }

            @Override
            public void onTextChanged(CharSequence var1, int var2, int var3, int var4) {

            }

            @Override
            public void afterTextChanged(Editable var1) {
                if (TextUtils.isEmpty(var1.toString()) || var1.toString().equals(search_str)) {
                    binding.searchList.setVisibility(View.GONE);
                    return;
                }
                new Thread(() -> {
                    list.clear();
                    String hq = wl.hq("/search/suggest", new String[][]{
                            new String[]{"keywords", var1.toString()}
                            , new String[]{"type", "mobile"}
                    });
                    try {
                        JSONObject jsonObject = new JSONObject(hq);
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray allMatch = result.getJSONArray("allMatch");
                        for (int i = 0; i < allMatch.length(); i++) {
                            String s = allMatch.getJSONObject(i).getString("keyword");
                            list.add(s);
                        }
                        runOnUiThread(() -> {
                            if (!list.isEmpty()) {
                                binding.searchList.setVisibility(View.VISIBLE);
                                binding.searchList.setAdapter(new SearchAdapter(activity_search.this, list, s -> SearchStart(s)));
                            }
                        });
                    } catch (JSONException e) {
                        gj.sc(e.getMessage());
                    }
                }).start();

            }
        });
        binding.lishiList.setLayoutManager(new FlexboxLayoutManager(this));
        searchTools = new SearchTools(this, binding.tablayout, binding.viewpager);
        binding.lishiList.setAdapter(searchRecordAdapter = new SearchRecordAdapter(this, new ArrayList<>(), binding.edit) {
            @Override
            public void Click(String data) {
                SearchStart(data);
            }
        });
        binding.edit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = binding.edit.getText().toString().trim();
                SearchStart(keyword);
                return true;
            }
            return false;
        });

    }

    SearchRecordAdapter searchRecordAdapter;

    SearchTools searchTools;
    private String search_str;//当前搜索的字符串

    /**
     * 搜索开始
     */
    private void SearchStart(String s) {
        search_str = s;
        binding.edit.setText(s);
        list.clear();
        binding.searchList.setVisibility(View.GONE);
        binding.relative.setVisibility(View.GONE);
        binding.line3.setVisibility(View.VISIBLE);
//                                    取消edit焦点
        binding.edit.clearFocus();
        searchTools.sx(search_str);
        if (searchRecordAdapter != null) {
            List<String> list = searchRecordAdapter.dataList;
            int position = list.indexOf(s);
            if (position != -1) {
                if (position != 0) {
                    list.remove(position);
                    list.add(0, s);
                    searchRecordAdapter.notifyItemMoved(position, 0);
                }
                // 如果 position == 0，则不需要动
            } else {
                list.add(0, s);
                searchRecordAdapter.notifyItemInserted(0);
                if (!list.isEmpty()) {
                    searchRecordAdapter.notifyItemRangeChanged(position, list.size() - position);
                }
            }
            // 保存到本地 JSON 文件
            wj.xrwb(new File(FilePath.filesdri, FilePath.lishi_json), new Gson().toJson(list));
        }

    }

    @Override
    protected ActivitySearchBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivitySearchBinding.inflate(layoutInflater);
    }

    public static class SearchAdapter extends BaseAdapter<ViewSearchItemBinding, String> {
        TaskAction<String> taskAction;

        public SearchAdapter(Context context, List<String> dataList, TaskAction<String> taskAction) {
            super(context, dataList);
            this.taskAction = taskAction;
        }

        @Override
        protected ViewSearchItemBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
            return ViewSearchItemBinding.inflate(inflater, parent, false);
        }

        @Override
        protected void onBindView(String data, ViewSearchItemBinding viewBinding, ViewHolder<ViewSearchItemBinding> viewHolder, int position) {
            viewBinding.text1.setText(data);
            viewBinding.getRoot().setOnClickListener(v -> taskAction.execute(data));
        }
    }

    public static class SearchRecordAdapter extends BaseAdapter<ListTextBinding, String> {
        EditText searchView;

        public SearchRecordAdapter(Context context, List<String> list, EditText searchView) {
            super(context, list);
            this.searchView = searchView;
            String dqwb = FilePath.dqwb(FilePath.filesdri + FilePath.lishi_json);
            if (dqwb != null) {
                try {
                    dataList.clear();
                    dataList.addAll(new Gson().fromJson(dqwb, new TypeToken<List<String>>() {
                    }.getType()));
                } catch (Exception e) {
                    FilePath.sc(FilePath.filesdri + FilePath.lishi_json);
                }
            }
//            RecyclerView.ItemAnimator animator = new DefaultItemAnimator() {
//                @Override
//                public boolean animateRemove(RecyclerView.ViewHolder holder) {
//                    ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(holder.itemView, "alpha", 1f, 0f);
//                    fadeAnimator.setDuration(getRemoveDuration());
//                    fadeAnimator.addListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            dispatchRemoveFinished(holder);
//                            holder.itemView.setAlpha(1f);
//                        }
//                    });
//                    fadeAnimator.start();
//                    return false;
//                }
//            };
//            binding.listRecycler.setItemAnimator(animator);
        }

        @Override
        protected ListTextBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
            return ListTextBinding.inflate(inflater, parent, false);
        }

        @Override
        protected void onBindView(String data, ListTextBinding viewBinding, ViewHolder<ListTextBinding> viewHolder, int position) {
            viewBinding.getRoot().setText(data);
            viewBinding.getRoot().setOnClickListener(v -> Click(data));
            viewBinding.getRoot().setOnCloseIconClickListener(view -> {
                dataList.remove(data);
                this.notifyItemRemoved(position);
                if (position != dataList.size()) {
                    notifyItemRangeChanged(position, dataList.size() - position);
                }
//                notifyItemRangeChanged(position, dataList.size() - position); // 可选：刷新后续位置
                FilePath.xrwb(FilePath.filesdri + FilePath.lishi_json, new Gson().toJson(dataList));
            });
        }

        public void Click(String data) {
        }
    }

    @Override
    public void BackPressed() {
        if (binding.line3.getVisibility() == View.VISIBLE) {
            binding.line3.setVisibility(View.GONE);
            binding.relative.setVisibility(View.VISIBLE);
            return;
        }
        finishAfterTransition();
    }

    @Override
    public void finishAfterTransition() {
//        binding.relative.setVisibility(View.GONE);
        super.finishAfterTransition();
    }
}