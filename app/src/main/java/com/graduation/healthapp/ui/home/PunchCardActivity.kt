package com.graduation.healthapp.ui.home

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.alibaba.fastjson.JSON
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.databinding.ActivityPunchCardBinding
import com.graduation.healthapp.ui.BaseActivity
import com.graduation.healthapp.util.TempStoreUtil
import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import com.luck.picture.lib.utils.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.necer.painter.InnerPainter


class PunchCardActivity : BaseActivity() {
    private lateinit var binding: ActivityPunchCardBinding
    private val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPunchCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initView() {
        binding.titlebar.setLeftOnClick {
            finish()
        }
        binding.punch.setOnClickListener {
            punch()
        }
        initData()
    }

    /**
     * 查询打卡记录
     */
    fun initData() {
        NetRequest.Instance.get(MyUrl.getPunchCard+"?phone="+userinfo.getString("phone")) {
            if (it is Result.Success) {
                val data = ArrayList<String>()
                val arr = JSON.parseArray(it.data.getJSONArray("data").toString())
                for (i in 0 until arr.size) {
                    data.add(arr.getJSONObject(i).getString("day"))
                }
                CoroutineScope(Dispatchers.Main).launch {
                    PointCalender(data)
                }
            } else {
                ToastUtils.showToast(this, "获取打卡数据失败")
            }
        }
    }

    //设置日历圆点
    fun PointCalender(pointList: List<String>) {
        val pa = binding.miui10Calendar.getCalendarPainter() as InnerPainter
        pa.setPointList(pointList)
    }

    /**
     * 打卡
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun punch() {
        val req = JSONObject().apply {
            put("user", userinfo.getString("phone"))
            put("day", getTime())
        }
        NetRequest.Instance.post(MyUrl.addPunchCard, req) {
            if (it is Result.Success) {
                ToastUtils.showToast(this, "打卡成功")
            } else {
                ToastUtils.showToast(this, "打卡失败")
            }
        }
    }

    //获取今天日期
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): String {
        val date: LocalDate = LocalDate.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter);
    }

}