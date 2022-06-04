package com.graduation.healthapp.ui.find

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.healthapp.R
import com.graduation.healthapp.data.adapter.BaseRecyAdapter
import com.graduation.healthapp.databinding.ActivitySmalclassDetailsBinding
import com.graduation.healthapp.ui.BaseActivity
import com.graduation.healthapp.ui.login.ViewModelFactory
import com.luck.picture.lib.utils.ToastUtils
import org.json.JSONObject

class SmalclassDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivitySmalclassDetailsBinding
    private lateinit var viewModel: FindViewModel
    private lateinit var adapter: BaseRecyAdapter<JSONObject>
    private var data = mutableListOf<JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmalclassDetailsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory()).get(FindViewModel::class.java)
        setContentView(binding.root)
        var json = JSONObject(intent.getStringExtra("data"))
        Glide.with(this).load(json.getString("url")).into(binding.back)
        Glide.with(this).load(json.getString("url")).into(binding.icon)
        binding.typeName.text = json.getString("name")

        viewModel.contents.observe(this){
            data = it as MutableList<JSONObject>
            adapter = BaseRecyAdapter(R.layout.find_content_item, data)
            adapter.setBind { itemview, position ->
                val portrait = itemview.findViewById<ImageView>(R.id.portrait)
                val username = itemview.findViewById<TextView>(R.id.name)
                val ht = itemview.findViewById<TextView>(R.id.ht)
                val sc = itemview.findViewById<ImageView>(R.id.sc)
                val dz = itemview.findViewById<ImageView>(R.id.dz)
                val content = itemview.findViewById<TextView>(R.id.content)
                val imaegs = itemview.findViewById<RecyclerView>(R.id.imaegs)
                Glide.with(this).load(data.get(position).getString("portrait")).into(portrait)
                username.text = data.get(position).getString("username")
                content.text = data.get(position).getString("introduce")
                ht.text = "# ${data.get(position).getString("name")}"
                content.setOnClickListener {
                    val intent = Intent(this,TopicDetailsActivity::class.java)
                    intent.putExtra("data",data.get(position).toString())
                    startActivity(intent)
                }
                dz.setOnClickListener {
                    viewModel.favouropter(
                        data[position].getInt("id").toString(),
                        position
                    )
                }
                sc.setOnClickListener {
                    viewModel.collectopter(
                        data[position].getInt("id").toString(),
                        position
                    )
                }
                dz.setImageResource(
                    if (data[position].getBoolean("dz")) R.mipmap.dz2 else R.mipmap.dz1
                )
                sc.setImageResource(
                    if (data[position].getBoolean("sc")) R.mipmap.sc2 else R.mipmap.sc1
                )
                viewModel.favourResult.observe(this) {
                    if (it.position != position){
                        return@observe
                    }
                    if (it.status == null){
                        ToastUtils.showToast(this,"操作失败")
                        return@observe
                    }
                    if (it.status == true) {
                        dz.setImageResource(R.mipmap.dz2)
                    } else {
                        dz.setImageResource(R.mipmap.dz1)
                    }
                }
                viewModel.collectResult.observe(this) {
                    if (it.position != position){
                        return@observe
                    }
                    if (it.status == null){
                        ToastUtils.showToast(this,"操作失败")
                        return@observe
                    }
                    if (it.status == true) {
                        sc.setImageResource(R.mipmap.sc2)
                    } else {
                        sc.setImageResource(R.mipmap.sc1)
                    }
                }
                //加载图片
                val str = data.get(position).getString("urls")
                if (str == "[]") return@setBind
                val urls = str.substring(1, str.length - 1).split(",")
                val list = mutableListOf<String>()
                for (element in urls) {
                    list.add(element.trim())
                }
                val adapter2 = BaseRecyAdapter<String>(R.layout.push_image_item, list)
                adapter2.setBind { itemview, position ->
                    val imageview = itemview.findViewById<ImageView>(R.id.push_image_item)
                    val delte = itemview.findViewById<ImageView>(R.id.delete)
                    Glide.with(imageview.context).load(list[position]).centerCrop().into(imageview)
                    delte.visibility = View.GONE
                }
                imaegs.adapter = adapter2
                imaegs.layoutManager = GridLayoutManager(this, 3)
            }
            binding.recy.adapter = adapter
            binding.recy.layoutManager = LinearLayoutManager(this)
        }

        viewModel.getPageData2(1,10,json.getInt("id"))
    }

}