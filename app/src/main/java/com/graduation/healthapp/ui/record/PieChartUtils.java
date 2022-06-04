package com.graduation.healthapp.ui.record;

import android.graphics.Color;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.List;

public class PieChartUtils {
    private static final String TAG = "PieChartUtils";
    private PieChart pieChart;
    private Legend legend;
    private String title;

    public PieChartUtils(PieChart pieChart,String title){
        this.pieChart = pieChart;
        this.title = title;
        initSetting();
    }

    /**
     常用设置
     */
    private void initSetting() {
        pieChart.setHoleColor(Color.TRANSPARENT);//设置中间的颜色
        pieChart.setHoleRadius(30f);//设置饼状的半径
        pieChart.setTransparentCircleRadius(54f);//设置半透明圈的半径
        pieChart.getDescription().setText("");//设置描述
        pieChart.setDrawCenterText(true);//设置饼状中间的文字是否显示
        pieChart.setDrawHoleEnabled(true);
        pieChart.setCenterText(title);//设置饼状中间文字
        pieChart.setCenterTextColor(Color.RED);
        pieChart.setCenterTextSize(16);
        pieChart.setRotationAngle(90);//设置初始旋转角度
        pieChart.setRotationEnabled(true);//设置是否可以手动旋转
        pieChart.setUsePercentValues(true);//设置是否显示成百分比

        legend = pieChart.getLegend();
        legend.setEnabled(true);//设置是否显示比例图
        legend.setForm(Legend.LegendForm.CIRCLE); //设置比例图样式 圆
        legend.setTextColor(Color.RED);

        //设置比例图位置
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        pieChart.animateXY(1000,1500);//设置xy轴的动画
    }

    /**
     * 设置数据
     * @param pieEntries 数据
     * @param colors 每一块显示的颜色
     * @param lableColor  显示在每一块中文字的颜色
     */
    public void setPieData(List<PieEntry> pieEntries, int[] colors, int lableColor ) {
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setSelectionShift(10f);//设置选中后弹出的比例
        pieDataSet.setSliceSpace(0);//设置间距
        pieDataSet.setValueTextColor(lableColor); //设置显示百分比数据的颜色
        pieDataSet.setValueTextSize(16);//设置显示百分比数据的大小
        pieDataSet.setValueLineVariableLength(true);
        pieDataSet.setValueLineColor(lableColor);
        pieDataSet.setValueLinePart1Length(0.4f);
        pieDataSet.setValueLinePart2Length(0.2f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValues(pieEntries);
        pieChart.setEntryLabelColor(lableColor);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
    }
}
