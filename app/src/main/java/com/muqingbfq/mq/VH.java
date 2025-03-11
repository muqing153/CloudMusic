package com.muqingbfq.mq;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.muqingbfq.fragment.wode;

public class VH<bind extends ViewBinding> extends RecyclerView.ViewHolder {

    public bind binding;

    public VH(bind itemView) {
        super(itemView.getRoot());
        binding = itemView;
    }
}
