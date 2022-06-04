package com.graduation.healthapp.ui.my

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.graduation.healthapp.R
import com.graduation.healthapp.databinding.FragmentMyBinding
import android.widget.BaseAdapter
import androidx.annotation.RequiresApi
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.databinding.MyGridItemBinding
import com.graduation.healthapp.ui.login.LoginActivity
import com.graduation.healthapp.ui.web.X5WebViewActivity
import com.graduation.healthapp.util.StatusBarColorUtil
import com.graduation.healthapp.util.TempStoreUtil
import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.utils.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class MyFragment : Fragment() {
    private lateinit var binding: FragmentMyBinding
    private lateinit var mData: ArrayList<Icon>
    private var mAdapter: BaseAdapter? = null
    private lateinit var punchData: ArrayList<String>
    private val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBinding.inflate(layoutInflater)
        initView()
        getPunchData()
        getCalorieData()
        getPunchCardRank()
        return binding.root
    }


    fun initView() {
        val gridview = binding.gridview

        mData = ArrayList()
        mData.add(Icon(R.mipmap.jbzl, "基本信息"))
        mData.add(Icon(R.mipmap.order, "我的话题"))
        mData.add(Icon(R.mipmap.wddt, "我的视频"))
        mData.add(Icon(R.mipmap.wdsw, "喜爱食谱"))
        //mData.add(Icon(R.mipmap.wdsc, "我的收藏"))
        mAdapter = MyBaseAdapter.Build<Icon>()
            .setData(mData)
            .setLayoutId(R.layout.my_grid_item)
            .addBindView { itemView, postition, itemData ->
                val bind = MyGridItemBinding.bind(itemView)
                bind.imgIcon.setImageResource(itemData.ResId)
                bind.txtIcon.text = itemData.text
                bind.imgIcon.setOnClickListener {
                    when (postition) {
                        0 -> {
                            //基本信息更新
                            startActivity(Intent(activity, UpdateUserInfoActivity::class.java))
                        }
                        1 -> {
                            val myintent = Intent(activity, MyTopicAndVideoActivity::class.java)
                            myintent.putExtra("type",0)
                            myintent.putExtra("title","我的话题")
                            startActivity(myintent)
                        }
                        2 -> {
                            val myintent = Intent(activity, MyTopicAndVideoActivity::class.java)
                            myintent.putExtra("type",1)
                            myintent.putExtra("title","我的视频")
                            startActivity(myintent)
                        }
                        3 -> {
                            startActivity(Intent(activity, FoodListFavourActivity::class.java))
                        }
                    }
                }
            }
            .create()
        gridview.adapter = mAdapter

        val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!
        binding.username.text = userinfo.getString("username")
        Glide.with(this).load(userinfo.getString("portrait")).into(binding.portrait)

        //饮食报告分析
        binding.ysbgsf.setOnClickListener {
            startActivity(Intent(activity,NurtrtionAnalysisActivity::class.java))
        }
        binding.rqsp.setOnClickListener {
            startActivity(Intent(activity,FoodListActivity::class.java))
        }
        //知识竞答
        binding.zsjd.setOnClickListener {
            val myIntent = Intent(activity, X5WebViewActivity::class.java)
            myIntent.putExtra("title","知识竞答")
            myIntent.putExtra("url","https://one.boohee.com/store/pages/everyday_question?id=${(1..190).random()}&app_device=Android&os_version=10&app_version=8.0.9&version_code=248&channel=xiaomi&app_key=one&token=AsRpZuPXrKy6xFoxkUR5NkhRFxCJXmMM")
            startActivity(myIntent)
        }
        binding.cancelSystem.setOnClickListener {
            showCancelLogin()
        }
    }

    fun showCancelLogin() {
        val adBd = AlertDialog.Builder(activity)
        adBd.setTitle("提示")
        adBd.setMessage("确认退出登陆吗？")
        adBd.setPositiveButton("确定") { dialog, which ->
            //清除缓存
            TempStoreUtil.clear()
            dialog.dismiss()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity!!.finish()
        }
        adBd.create().show()
    }

    /**
     * 查询打卡记录
     */
    fun getPunchData() {
        NetRequest.Instance.get(MyUrl.getPunchCard + "?phone=" + userinfo.getString("phone")) {
            if (it is Result.Success) {
                punchData = ArrayList<String>()
                val arr = JSON.parseArray(it.data.getJSONArray("data").toString())
                for (i in 0 until arr.size) {
                    punchData.add(arr.getJSONObject(i).getString("day"))
                }
                CoroutineScope(Dispatchers.Main).launch {
                    binding.ljdk.setText(punchData.size.toString() + "次\n累计打卡")
                }
            } else {
                ToastUtils.showToast(activity, "获取打卡数据失败")
            }
        }
    }

    /**
     * 查询排名
     */
    fun getPunchCardRank() {
        NetRequest.Instance.get(MyUrl.getPunchCardRank + "?phone=" + userinfo.getString("phone")) {
            if (it is Result.Success) {
                val arr = JSON.parseArray(it.data.getJSONArray("data").toString())
                for (i in 0 until arr.size) {
                    if (arr.getJSONObject(i).getString("user")
                            .equals(userinfo.getString("phone"))
                    ) {
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.pm.setText("第" + i+1 + "名\n排名")
                        }
                    }
                }
            } else {
                ToastUtils.showToast(activity, "获取排名数据失败")
            }
        }
    }

    /**
     * 根据用户名和时间获取食物信息
     */
    fun getCalorieData() {
        NetRequest.Instance.get(
            MyUrl.byUserAndTimeData + "" +
                    "?user=${userinfo.getString("phone")}&time=${getTime()}"
        ) {
            if (it is Result.Success) {
                var cal = 0
                val arr = JSON.parseArray(it.data.getJSONArray("data").toString())
                for (i in 0 until arr.size) {
                    cal += arr.getJSONObject(i).getString("calorie").toString().toInt()
                }
                CoroutineScope(Dispatchers.Main).launch {
                    binding.jrkll.setText("$cal\n今日卡路里")
                }
            } else {
                ToastUtils.showToast(context, "获取饮食数据失败")
            }
        }
    }

    fun getTime(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(System.currentTimeMillis())
        return formatter.format(date)
    }

}

data class Icon(val ResId: Int, val text: String)