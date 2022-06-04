package com.graduation.healthapp.ui.my

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class MyBaseAdapter<T>() : BaseAdapter() {

    private var data: ArrayList<T>? = null
    private var layoutId: Int? = null
    private var addBindView: ((itemView: View, postition: Int, itemData: T) -> Unit)? = null

    override fun getCount(): Int {
        return data!!.size
    }

    override fun getItem(position: Int): T {
        return data!!.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view = LayoutInflater.from(parent!!.context).inflate(layoutId!!, parent, false)
        addBindView!!.invoke(view,position,data!!.get(position))
        return view
    }

    class Build<B> {
        private var adapter: MyBaseAdapter<B> = MyBaseAdapter()

        fun setData(data: ArrayList<B>): Build<B> {
            adapter.data = data
            return this
        }

        fun setLayoutId(layoutId: Int): Build<B> {
            adapter.layoutId = layoutId
            return this
        }

        fun addBindView(itemBind: (itemView: View, postition: Int, itemData: B) -> Unit): Build<B> {
            adapter.addBindView = itemBind
            return this
        }

        fun create(): MyBaseAdapter<B> {
            return adapter
        }

    }

}