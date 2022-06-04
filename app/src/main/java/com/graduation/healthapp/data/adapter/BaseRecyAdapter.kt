package com.graduation.healthapp.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BaseRecyAdapter<T> : RecyclerView.Adapter<BaseRecyAdapter.ViewHolder> {
    private var layoutId: Int = 0
    private var data: MutableList<T>
    private lateinit var bind: (itemview: View, position: Int) -> Unit

    fun setBind(b: (itemview: View, position: Int) -> Unit) {
        bind = b
    }

    constructor(layoutId: Int, data: MutableList<T>) {
        this.layoutId = layoutId
        this.data = data
    }

    fun Updata(data: MutableList<T>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        val viewholder = ViewHolder(view)
        return viewholder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bind.let {
            bind.invoke(holder.itemView, position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}