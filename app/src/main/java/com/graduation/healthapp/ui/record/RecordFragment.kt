package com.graduation.healthapp.ui.record

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.data.adapter.BaseAdapter
import com.graduation.healthapp.data.adapter.BaseHolder
import com.graduation.healthapp.databinding.FragmentRecordBinding
import com.graduation.healthapp.databinding.RecordItemBinding
import com.graduation.healthapp.util.TempStoreUtil
import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import com.luck.picture.lib.utils.ToastUtils
import com.necer.calendar.BaseCalendar
import com.necer.enumeration.DateChangeBehavior
import com.necer.listener.OnCalendarChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.R
import android.R.attr
import com.github.mikephil.charting.data.Entry

import com.github.mikephil.charting.formatter.PercentFormatter

import com.github.mikephil.charting.data.PieData

import com.github.mikephil.charting.utils.ColorTemplate

import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import android.R.attr.y
import android.graphics.Color


class RecordFragment : Fragment() {
    private lateinit var binding: FragmentRecordBinding
    private val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!
    private val colors = arrayOf(
        "#668B8B",
        "#708090",
        "#FF6347",
        "#FFB90F",
        "#8B8682",
        "#436EEE",
        "#668B8B",
        "#708090",
        "#FF6347",
        "#FFB90F",
        "#8B8682",
        "#436EEE",
        "#668B8B",
        "#708090",
        "#FF6347",
        "#FFB90F",
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initView() {
        Glide.with(context!!).load("https://dss2.bdstatic.com/8_V1bjqh_Q23odCf/pacific/1994254330.jpg").into(binding.tjItem1)
        Glide.with(context!!).load("https://dss2.bdstatic.com/8_V1bjqh_Q23odCf/pacific/1994250498.jpg").into(binding.tjItem2)
        Glide.with(context!!).load("https://dss2.bdstatic.com/8_V1bjqh_Q23odCf/pacific/1994257886.jpg").into(binding.tjItem3)
        binding.miui10Calendar.setOnCalendarChangedListener(object : OnCalendarChangedListener {
            override fun onCalendarChange(
                baseCalendar: BaseCalendar?,
                year: Int,
                month: Int,
                localDate: org.joda.time.LocalDate?,
                dateChangeBehavior: DateChangeBehavior?
            ) {
                getData(localDate.toString())
            }

        })
        getData(getTime())
    }

    /**
     * 根据用户名和时间获取食物信息
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getData(time: String) {
        NetRequest.Instance.get(
            MyUrl.byUserAndTimeData + "" +
                    "?user=${userinfo.getString("phone")}&time=${time}"
        ) {
            if (it is Result.Success) {
                val data = ArrayList<JSONObject>()
                val sets = HashSet<String>()
                val arr = JSON.parseArray(it.data.getJSONArray("data").toString())
                for (i in 0 until arr.size) {
                    if (!sets.contains(arr.getJSONObject(i).getString("name"))){
                        sets.add(arr.getJSONObject(i).getString("name"))
                        data.add(arr.getJSONObject(i))
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    initChart(data)
                    initAadapter(data)
                }
            } else {
                ToastUtils.showToast(context, "获取饮食数据失败")
            }
        }
    }

    /**
     * 加载适配器数据
     */
    fun initAadapter(data: ArrayList<JSONObject>) {
        binding.foodlist.layoutManager = LinearLayoutManager(activity)
        binding.foodlist.adapter = object : BaseAdapter<JSONObject, RecordItemBinding>(data) {
            override fun onBindingData(
                holder: BaseHolder<RecordItemBinding>?,
                t: JSONObject?,
                position: Int
            ) {
                val bind = holder!!.viewbinding
                Glide.with(context!!).load(t!!.getString("url")).into(bind.image)
                bind.text.text = t!!.getString("introduce")
            }

            override fun onBindingView(viewGroup: ViewGroup?): RecordItemBinding {
                return RecordItemBinding.inflate(
                    LayoutInflater.from(viewGroup!!.context),
                    viewGroup, false
                )
            }

        }
    }

    /**
     * 获取当前时间
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): String {
        val date: LocalDate = LocalDate.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter);
    }

    /**
     * 加载饼状图
     */
    fun initChart(data: ArrayList<JSONObject>) {
        val mChart = binding.chart
        val pie = ArrayList<PieEntry>()
        val co = IntArray(data.size)
        for (i in 0 until data.size) {
            co[i] = Color.parseColor(colors[i])
            pie.add(PieEntry(data[i].getString("calorie").toFloat(), data[i].getString("name")))
        }
        PieChartUtils(mChart, "卡路里").setPieData(pie, co, Color.GRAY)
    }

}