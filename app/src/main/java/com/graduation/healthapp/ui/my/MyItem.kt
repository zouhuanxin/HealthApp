package com.graduation.healthapp.ui.my

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.graduation.healthapp.R
import com.graduation.healthapp.util.DensityUtil

class MyItem : LinearLayout {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        orientation = HORIZONTAL
        val array = context!!.obtainStyledAttributes(attrs, R.styleable.MyItem) ?: return
        //左右图标
        val LeftImageparams = LayoutParams(
            resources.getDimension(R.dimen.dp_20).toInt(),
            resources.getDimension(R.dimen.dp_20).toInt()
        )
        LeftImageparams.gravity = 0x11
        val RightImageparams = LayoutParams(
            resources.getDimension(R.dimen.dp_15).toInt(),
            resources.getDimension(R.dimen.dp_15).toInt()
        )
        RightImageparams.gravity = 0x11
        val leftView = ImageView(context)
        if (array.getDrawable(R.styleable.MyItem_leftIcon) != null) {
            leftView.setImageDrawable(array.getDrawable(R.styleable.MyItem_leftIcon))

        }
        val rightView = ImageView(context)
        rightView.setImageResource(R.mipmap.right_arrow)
        if (array.getDrawable(R.styleable.MyItem_rightIcon) != null) {
            rightView.setImageDrawable(array.getDrawable(R.styleable.MyItem_rightIcon))
        }
        //中部文字 默认无
        val Textparams = LayoutParams(0, LayoutParams.MATCH_PARENT)
        Textparams.weight = 3f
        val centerView = TextView(context)
        centerView.setTextColor(Color.parseColor("#000000"))
        centerView.gravity = Gravity.CENTER or Gravity.LEFT
        centerView.textSize = DensityUtil.dip2px(context, 4.5f)
        if (array.getString(R.styleable.MyItem_text) != null) {
            centerView.setPadding(resources.getDimension(R.dimen.dp_15).toInt())
            centerView.text = array.getString(R.styleable.MyItem_text)
        }
        //旁边文字
        val Textparams2 = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        val centerrightView = TextView(context)
        centerrightView.gravity = Gravity.CENTER
        centerrightView.textSize = DensityUtil.dip2px(context, 4f)
        if (array.getString(R.styleable.MyItem_righttext) != null) {
            centerView.setPadding(0)
            centerrightView.setPadding(resources.getDimension(R.dimen.dp_10).toInt())
            centerrightView.text = array.getString(R.styleable.MyItem_righttext)
        }
        val centerleftView = TextView(context)
        centerleftView.gravity = Gravity.CENTER
        centerleftView.textSize = DensityUtil.dip2px(context, 4f)
        if (array.getString(R.styleable.MyItem_lefttext) != null) {
            centerleftView.setPadding(resources.getDimension(R.dimen.dp_10).toInt())
            centerleftView.text = array.getString(R.styleable.MyItem_lefttext)
        }

        addView(leftView, LeftImageparams)
        addView(centerleftView, Textparams2)
        addView(centerView, Textparams)
        addView(centerrightView, Textparams2)
        addView(rightView, RightImageparams)
    }

}