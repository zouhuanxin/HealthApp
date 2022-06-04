package com.graduation.healthapp.ui.my

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.graduation.healthapp.databinding.ActivityNurtrtionAnalysisBinding
import com.github.mikephil.charting.components.YAxis

import com.github.mikephil.charting.formatter.ValueFormatter

import com.github.mikephil.charting.components.XAxis

import android.graphics.Color
import android.view.View
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.github.mikephil.charting.data.RadarData

import com.github.mikephil.charting.data.RadarDataSet

import com.github.mikephil.charting.data.RadarEntry
import com.graduation.healthapp.R
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.ui.BaseActivity
import com.graduation.healthapp.util.TempStoreUtil
import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import com.luck.picture.lib.utils.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 饮食分析报告
 */
class NurtrtionAnalysisActivity : BaseActivity() {
    private lateinit var binding : ActivityNurtrtionAnalysisBinding
    private val list: MutableList<RadarEntry> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNurtrtionAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.titlebar.setLeftOnClick {
            finish()
        }
        initChart()
        getData()
    }

    /**
     * 一些轴上的设置等等
     */
    private fun initChart() {
        val chart = binding.chart
        //设置web线的颜色(即就是外面包着的那个颜色)
        chart.setWebColorInner(Color.BLACK)
        //设置中心线颜色(也就是竖着的线条)
        chart.setWebColor(Color.BLACK)
        chart.setWebAlpha(50)
        val xAxis: XAxis = chart.getXAxis()
        //设置x轴标签字体颜色
        xAxis.setLabelCount(4, true)
        xAxis.axisMaximum = 4f
        xAxis.axisMinimum = 0f
       // xAxis.textSize = 20f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val weekStrs = arrayOf("热量(大卡)", "碳水化合物(克)", "脂肪(克)", "蛋白质(克)", "纤维素(克)")
                return weekStrs[value.toInt()]
            }
        }
        val yAxis: YAxis = chart.getYAxis()
        //设置y轴的标签个数
        yAxis.setLabelCount(5, true)
        //设置y轴从0f开始
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 100f
        //启用绘制Y轴顶点标签，这个是最新添加的功能
        //yAxis.setDrawTopYLabelEntry(false)
        //设置字体大小
        //yAxis.textSize = 15f
        //设置字体颜色
        yAxis.textColor = Color.RED

        //启用线条，如果禁用，则无任何线条
        chart.setDrawWeb(true)

        //禁用图例和图表描述
        chart.getDescription().setEnabled(false)
        chart.getLegend().setEnabled(false)
    }

    /**
     * 设置数据
     */
    private fun setData() {
        binding.progress.visibility = View.GONE
        val chart = binding.chart
        val set = RadarDataSet(list, "Petterp")

        //禁用标签
        set.setDrawValues(false)
        //设置填充颜色
        set.fillColor = Color.BLUE
        //设置填充透明度
        set.fillAlpha = 40
        //设置启用填充
        set.setDrawFilled(true)
        //设置点击之后标签是否显示圆形外围
        set.isDrawHighlightCircleEnabled = true
        //设置点击之后标签圆形外围的颜色
        set.highlightCircleFillColor = Color.RED
        //设置点击之后标签圆形外围的透明度
        set.highlightCircleStrokeAlpha = 40
        //设置点击之后标签圆形外围的半径
        set.highlightCircleInnerRadius = 20f
        //设置点击之后标签圆形外围内圆的半径
        set.highlightCircleOuterRadius = 10f
        val data = RadarData(set)
        chart.setData(data)
        chart.invalidate()
    }

    /**
     * 请求数据
     */
    fun getData(){
        binding.progress.visibility = View.VISIBLE
        NetRequest.Instance.get(MyUrl.analysis+"?phone="+ TempStoreUtil.getUserInfo().getString("phone")) {
            if (it is Result.Success) {
                //识别成功
                var a:Float = 0.0f
                var b:Float = 0.0f
                var c:Float = 0.0f
                var d:Float = 0.0f
                var e:Float = 0.0f
                val data = ArrayList<JSONObject>()
                val arr = JSON.parseArray(it.data.getJSONArray("data").toString())
                for (i in 0 until arr.size) {
                    data.add(arr.getJSONObject(i))
                    val temp = arr.getJSONObject(i).getJSONArray("info")
                    //热量(大卡)
                    a += temp.getJSONObject(1).getString("info").replace("热量(大卡)", "").toFloat()
                    //碳水化合物(克)
                    b += temp.getJSONObject(2).getString("info").replace("碳水化合物(克)","").toFloat()
                    //脂肪(克)
                    c += temp.getJSONObject(3).getString("info").replace("脂肪(克)","").toFloat()
                    //蛋白质(克)
                    d += temp.getJSONObject(4).getString("info").replace("蛋白质(克)","").toFloat()
                    //纤维素(克)
                    e += temp.getJSONObject(5).getString("info").replace("纤维素(克)","").toFloat()
                }
                CoroutineScope(Dispatchers.Main).launch {
                    if (a < 2000){
                        binding.item1.setFixText("热量(大卡) ", R.color.red)
                    }
                    if (b < 130){
                        binding.item2.setFixText("碳水化合物(克) ", R.color.red)
                    }
                    if (c < 300){
                        binding.item3.setFixText("脂肪(克) ", R.color.red)
                    }
                    if (d < 75){
                        binding.item4.setFixText("蛋白质(克) ", R.color.red)
                    }
                    if (e < 30){
                        binding.item5.setFixText("纤维素(克) ", R.color.red)
                    }
                    binding.item1.setEdtHint("热量(大卡) ", a.toString())
                    binding.item2.setEdtHint("碳水化合物(克) ", b.toString())
                    binding.item3.setEdtHint("脂肪(克) ", c.toString())
                    binding.item4.setEdtHint("蛋白质(克) ", d.toString())
                    binding.item5.setEdtHint("纤维素(克) ", e.toString())
                }
                //换算成百分比
                list.add(RadarEntry(a/2000 * 100))
                list.add(RadarEntry(b/130 * 100))
                list.add(RadarEntry(c/300 * 100))
                list.add(RadarEntry(d/75 * 100))
                list.add(RadarEntry(e/30 * 100))
                CoroutineScope(Dispatchers.Main).launch {
                    setData()
                }
            } else {
                ToastUtils.showToast(this, "获取饮食分析报告失败")
            }
        }
    }
}