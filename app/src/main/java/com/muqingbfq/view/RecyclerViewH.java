package com.muqingbfq.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewH extends RecyclerView {
    public RecyclerViewH(@NonNull Context context) {
        super(context);
    }

    public RecyclerViewH(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewH(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
//    private boolean disallowIntercept = false;

    private int startX = 0;
    private int startY = 0;
    boolean isDispatch = true;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isDispatch) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) ev.getX();
                    startY = (int) ev.getY();
                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int endX = (int) ev.getX();
                    int endY = (int) ev.getY();
                    int disX = Math.abs(endX - startX);
                    int disY = Math.abs(endY - startY);
                    if (Math.abs(disY) > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                        // 当前手指移动距离大于系统认定的最小滚动距离时，不允许父容器拦截触摸事件
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        break;
                    }
                        /*
                    if (disX > disY) {
                        //为了解决RecyclerView嵌套RecyclerView时横向滑动的问题
                        if (disallowIntercept) {
                            getParent().requestDisallowInterceptTouchEvent(disallowIntercept);
                        } else {
                            getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(startX - endX));
                        }
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }*/
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

}
