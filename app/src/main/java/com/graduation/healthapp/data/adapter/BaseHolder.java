package com.graduation.healthapp.data.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public class BaseHolder <V extends ViewBinding> extends RecyclerView.ViewHolder {
    private V viewbinding;

    public BaseHolder(@NonNull V item) {
        super(item.getRoot());
        viewbinding = item;
    }

    public V getViewbinding() {
        return viewbinding;
    }
}
