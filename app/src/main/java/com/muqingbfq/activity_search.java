package com.muqingbfq;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.search.SearchView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqing.gj;
import com.muqingbfq.databinding.ActivitySearchBinding;
import com.muqingbfq.databinding.ListTextBinding;
import com.muqingbfq.databinding.ViewSearchItemBinding;
import com.muqingbfq.mq.FragmentActivity;
import com.muqingbfq.mq.VH;
import com.muqingbfq.mq.FilePath;
import com.muqingbfq.view.Edit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class activity_search extends FragmentActivity<ActivitySearchBinding> {
    //    private List<String> json_list = new ArrayList<>();
    private final List<String> list = new ArrayList<>();

    public static void start(Activity context, View view) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context,
                view, "edit");
        context.startActivity(new Intent(context, activity_search.class), options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getViewBinding().getRoot());
        setToolbar();
        FlexboxLayoutManager manager = new FlexboxLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        //设置主轴排列方式
        manager.setFlexDirection(FlexDirection.ROW);
        //设置是否换行
        manager.setFlexWrap(FlexWrap.WRAP);
        manager.setAlignItems(AlignItems.STRETCH);//历史记录的LayoutManager
        binding.listRecycler.setLayoutManager(manager);
//        binding.listRecycler.setAdapter(new SearchRecordAdapter());
/*        binding.deleat.setOnClickListener(v -> new MaterialAlertDialogBuilder(
                activity_search.this)
                .setTitle("删除")
                .setMessage("清空历史记录？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialogInterface, ii) -> {
                    int i = 0;
                    Iterator<String> iterator = json_list.iterator();
                    while (iterator.hasNext()) {
                        iterator.next();
                        iterator.remove();
                        binding.listRecycler.getAdapter().notifyItemRemoved(i++);
                    }
                    binding.xxbj1.setVisibility(View.GONE);
                    wj.sc(wj.filesdri + wj.lishi_json);
                })
                .show());*/
        binding.searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.searchRecycler.setAdapter(new search_adapter(list, this::start));
        //设置项点击监听
        final Object o = new Object();
        binding.searchview.getEditText().addTextChangedListener(new Edit.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new Thread() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        synchronized (o) {
                            list.clear();
                            String hq = com.muqingbfq.mq.wl.
                                    hq("/search/suggest",
                                            new String[][]{
                                                    {"keywords", s.toString()},
                                                    {"type", "mobile"}
                                            });
                            try {
                                JSONArray jsonArray = new JSONObject(hq).getJSONObject("result")
                                        .getJSONArray("allMatch");
                                int length = jsonArray.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String keyword = jsonObject.getString("keyword");
                                    list.add(keyword);
                                }
                                activity_search.this.runOnUiThread(() ->
                                        binding.searchRecycler.getAdapter().notifyDataSetChanged());
                            } catch (Exception e) {
                                gj.sc(e);
                            }
                        }
                    }
                }.start();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
/*        if (binding.toolbar.collapse(contextualToolbar, binding.barlayout)) {
            // Clear selection.
            return;
        }*/
        binding.searchview.inflateMenu(R.menu.search);
        binding.searchview.setOnMenuItemClickListener(
                menuItem -> {
                    binding.searchview.hide();
                    start(binding.searchview.getText().toString());
                    return true;
                });

        binding.searchview.addTransitionListener(
                (searchView, previousState, newState) -> {
                    if (newState == SearchView.TransitionState.SHOWING ||
                            newState == SearchView.TransitionState.SHOWN) {
                        searchView.setText(binding.toolbar.getText());
                    } else if (newState == SearchView.TransitionState.HIDING ||
                            newState == SearchView.TransitionState.HIDDEN) {
                        binding.toolbar.setText(searchView.getText());
                    }
                });
        binding.searchview
                .getEditText()
                .setOnEditorActionListener(
                        (v, actionId, event) -> {
                            binding.toolbar.setText(binding.searchview.getText());
                            binding.searchview.hide();
                            start(binding.searchview.getText().toString());
                            return false;
                        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            //搜索
            start(binding.toolbar.getText().toString());
            return true;
        });
    }

    @FunctionalInterface
    public interface TaskAction<T> {
        void execute(T t);
    }

    public static class search_adapter extends RecyclerView.Adapter<VH<ViewSearchItemBinding>> {
        public List<String> list;
        TaskAction<String> taskAction;

        public search_adapter(List<String> list, TaskAction<String> taskAction) {
            this.taskAction = taskAction;
            this.list = list;
        }

        @NonNull
        @Override
        public VH<ViewSearchItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH<>(ViewSearchItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            String s = list.get(position);
            ((TextView) holder.itemView).setText(s);
            holder.itemView.setOnClickListener(v -> taskAction.execute(s));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }

    public void dismiss() {
        binding.searchview.hide();
    }

    public static void addSearchRecord(String name, List<String> json_list, SearchRecordAdapter adapter) {
        try {
            int existingIndex = json_list.indexOf(name);
            if (existingIndex != -1) {
                // 交换两个元素的位置
                json_list.remove(name);
                json_list.add(0, name);
                adapter.notifyItemMoved(existingIndex, 0);
            } else {
//                json_list.remove(name);
                json_list.add(0, name);
                adapter.notifyItemInserted(0);
            }
            FilePath.xrwb(FilePath.filesdri + FilePath.lishi_json, new Gson().toJson(json_list));
        } catch (Exception e) {
            gj.sc(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem sousuo = menu.add("搜索");
        sousuo.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected ActivitySearchBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivitySearchBinding.inflate(layoutInflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            end();
        }
        return true;
    }

    public void start(String name) {
        dismiss();
        if (!TextUtils.isEmpty(name)) {
//            search sea = (search) getSupportFragmentManager().findFragmentById(R.id.search_fragment);
//            binding.searchFragment.setVisibility(View.VISIBLE);
//            sea.sx(name);
//            addSearchRecordd(name);
        }
    }

    public static class SearchRecordAdapter extends RecyclerView.Adapter<VH<ListTextBinding>> {
        SearchView searchView;
        public List<String> json_list = new ArrayList<>();

        public SearchRecordAdapter(SearchView searchView) {
            this.searchView = searchView;
            String dqwb = FilePath.dqwb(FilePath.filesdri + FilePath.lishi_json);
            if (dqwb != null) {
                try {
                    json_list = new Gson().fromJson(dqwb, new TypeToken<List<String>>() {
                    }.getType());
                } catch (Exception e) {
                    FilePath.sc(FilePath.filesdri + FilePath.lishi_json);
//                    yc.start(activity_search.this, e);
                }
            }
            gj.sc(json_list.size());
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

        @NonNull
        @Override
        public VH<ListTextBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH<>(ListTextBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH<ListTextBinding> holder, int position) {
            String keyword = json_list.get(position);
            holder.binding.getRoot().setText(keyword);
            holder.binding.getRoot().setOnClickListener(v -> {
                searchView.setText(keyword);
//                start(keyword);
            });
            holder.binding.getRoot().setOnCloseIconClickListener(view -> {
                json_list.remove(keyword);
                notifyItemRemoved(holder.getBindingAdapterPosition());
                FilePath.xrwb(FilePath.filesdri + FilePath.lishi_json, new Gson().toJson(json_list));
            });
        }

        @Override
        public int getItemCount() {
            return json_list.size();
        }
    }

//    @Override
//    public void onBackPressed() {
//        end();
//    }

    private void end() {
        if (binding.searchview.isShowing()) {
            binding.searchview.hide();
            return;
        }
        if (binding.searchFragment.getVisibility() == View.VISIBLE) {
            binding.searchFragment.setVisibility(View.GONE);
        } else {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            ActivityCompat.finishAffinity(this);
        }
    }

    @Override
    public void finish() {
        super.finish();
    }
}