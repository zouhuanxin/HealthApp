package com.graduation.healthapp.data.adapter;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import java.util.List;

public abstract class BaseAdapter<T, V extends ViewBinding> extends RecyclerView.Adapter<BaseHolder<V>> {
    private List<T> data;

    public BaseAdapter(List<T> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public BaseHolder<V> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseHolder holder = new BaseHolder(onBindingView(parent));
        return holder;
    }

    protected abstract void onBindingData(BaseHolder<V> holder, T t, int position);
    protected abstract V onBindingView(ViewGroup viewGroup);

    @Override
    public void onBindViewHolder(@NonNull BaseHolder<V> holder, int position) {
        onBindingData(holder, data.get(position), position);
    }



    @Override
    public int getItemCount() {
        return data.size();
    }
}
