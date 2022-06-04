package com.graduation.healthapp.ui.my

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.graduation.healthapp.databinding.ActivityFoodListBinding
import com.graduation.healthapp.databinding.HomeQueryItemBinding
import com.graduation.healthapp.util.TempStoreUtil
import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import com.luck.picture.lib.utils.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 喜爱食谱
 */
class FoodListFavourActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.titlebar.setLeftOnClick {
            finish()
        }
        binding.titlebar.setText("喜爱食谱")
        byUserFoodlist()
    }

    /**
     * 获取喜爱食谱信息
     */
    fun byUserFoodlist() {
        NetRequest.Instance.get(MyUrl.byUserFoodlist+"?phone="+TempStoreUtil.getUserInfo().getString("phone")) {
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
                ToastUtils.showToast(this, "获取人气食谱数据失败")
            }
        }
    }

    fun initAdapter(data: ArrayList<JSONObject>) {
        binding.recy.layoutManager = LinearLayoutManager(this)
        binding.recy.adapter = object : BaseAdapter<JSONObject, HomeQueryItemBinding>(data) {
            override fun onBindingData(
                holder: BaseHolder<HomeQueryItemBinding>?,
                t: JSONObject?,
                position: Int
            ) {
                val bind = holder!!.viewbinding
                Glide.with(this@FoodListFavourActivity).load(t!!.getString("image")).into(bind.image)
                bind.text.text = t!!.getString("introduce")
                bind.bt1.visibility = View.GONE
            }

            override fun onBindingView(viewGroup: ViewGroup?): HomeQueryItemBinding {
                return HomeQueryItemBinding.inflate(
                    LayoutInflater.from(viewGroup!!.context),
                    viewGroup, false
                )
            }
        }
    }

}