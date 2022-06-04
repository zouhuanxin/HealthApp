package com.graduation.healthapp.ui.home

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.data.adapter.BaseAdapter
import com.graduation.healthapp.data.adapter.BaseHolder
import com.graduation.healthapp.databinding.ActivitySearchBinding
import com.graduation.healthapp.databinding.HomeQueryItemBinding
import com.graduation.healthapp.ui.BaseActivity
import com.graduation.healthapp.util.TempStoreUtil
import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import com.luck.picture.lib.utils.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchActivity : BaseActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.titlebar.setLeftOnClick {
            finish()
        }
        binding.bt1.setOnClickListener{
            if (TextUtils.isEmpty(binding.input.text)){
                ToastUtils.showToast(this, "请输入")
                return@setOnClickListener
            }
            search(binding.input.text.toString())
        }
    }

    /**
     * 文本搜索菜品
     */
    fun search(name: String) {
        openProgressBar()
        NetRequest.Instance.get(MyUrl.dishText + "?name=" + name) {
            if (it is Result.Success) {
                //识别成功
                val data = ArrayList<JSONObject>()
                val arr = JSON.parseArray(it.data.getJSONArray("data").toString())
                for (i in 0 until arr.size) {
                    data.add(arr.getJSONObject(i))
                }
                CoroutineScope(Dispatchers.Main).launch {
                    initAdapter(data)
                }
            } else {
                ToastUtils.showToast(this, "识别失败")
            }
            closeProgressBar()
        }
    }

    //开启加载等待条
    fun openProgressBar() {
        CoroutineScope(Dispatchers.Main).launch {
            binding.progress.visibility = View.VISIBLE
        }
    }

    //开启加载等待条
    fun closeProgressBar() {
        CoroutineScope(Dispatchers.Main).launch {
            binding.progress.visibility = View.GONE
        }
    }

    //加载查询菜品结果适配器
    fun initAdapter(data: ArrayList<JSONObject>) {
        binding.queryres.layoutManager = LinearLayoutManager(this)
        binding.queryres.adapter = object : BaseAdapter<JSONObject, HomeQueryItemBinding>(data) {
            override fun onBindingData(
                holder: BaseHolder<HomeQueryItemBinding>?,
                t: JSONObject?,
                position: Int
            ) {
                val bind = holder!!.viewbinding
                Glide.with(this@SearchActivity).load(t!!.getString("image")).into(bind.image)
                bind.text.text = t!!.getString("name") + "\n" + t!!.getString("introduce")
                bind.bt1.setOnClickListener {
                    //添加完毕清空数据并关闭页面
                    addRecord(t)
                }
            }

            override fun onBindingView(viewGroup: ViewGroup?): HomeQueryItemBinding {
                return HomeQueryItemBinding.inflate(
                    LayoutInflater.from(viewGroup!!.context),
                    viewGroup, false
                )
            }
        }
    }

    /**
     * 添加一条饮食记录
     */
    fun addRecord(ob: JSONObject) {
        val req = org.json.JSONObject().apply {
            //切割名字
            put("name", ob.getString("name").split("，")[0].split("(")[0])
            //拼接内容
            put("introduce", ob.getString("name") + " " + ob.getString("introduce"))
            //切割卡路里信息
            put("calorie", ob.getString("introduce").replace(" 大卡","").replace("热量：", "").split("(")[0])
            //菜品图片
            put("url", ob.getString("image"))
            //hrf
            put("href", ob.getString("href"))
            //用户手机号
            put("user", userinfo.getString("phone"))
        }
        NetRequest.Instance.post(MyUrl.addCalorie, req) {
            if (it is Result.Success) {
                ToastUtils.showToast(this,"添加成功")
                finish()
            } else {
                ToastUtils.showToast(this,"添加失败")
            }
        }
    }

}