package com.graduation.healthapp.ui.find

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.graduation.healthapp.R
import com.graduation.healthapp.data.FindDataSource
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.data.adapter.BaseRecyAdapter
import com.graduation.healthapp.databinding.ActivitySmalclassBinding
import com.graduation.healthapp.ui.BaseActivity
import com.luck.picture.lib.utils.ToastUtils
import org.json.JSONObject

class SmalclassActivity : BaseActivity() {
    private lateinit var binding: ActivitySmalclassBinding
    private lateinit var adapter: BaseRecyAdapter<JSONObject>
    private var datas: MutableList<JSONObject> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmalclassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    fun initView() {
        val dataSource = FindDataSource()
        adapter = BaseRecyAdapter(R.layout.smalclass_item, datas)
        adapter.setBind { itemview, position ->
            val item = itemview.findViewById<LinearLayout>(R.id.item)
            val icon = itemview.findViewById<ImageView>(R.id.icon)
            val name = itemview.findViewById<TextView>(R.id.name)
            val introduce = itemview.findViewById<TextView>(R.id.introduce)
            Glide.with(icon.context).load(datas.get(position).getString("url")).centerCrop().into(icon)
            name.text = datas.get(position).getString("name")
            introduce.text = datas.get(position).getString("introduce")
            item.setOnClickListener {
                val intent = Intent()
                intent.putExtra("data",datas.get(position).toString())
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
        }
        dataSource.getAllSmalClass {
            if (it is Result.Success) {
                val json = it.data
                val array = json.getJSONArray("data")
                for (i in 0 until array.length()){
                    datas.add(array.getJSONObject(i))
                }
                runOnUiThread {
                    binding.content.adapter = adapter
                    binding.content.layoutManager = LinearLayoutManager(this)
                }
            } else {
                ToastUtils.showToast(this,"加载失败，请重试")
                finish()
            }
        }
    }

}