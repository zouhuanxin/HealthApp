package com.graduation.healthapp.ui.login

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import com.graduation.healthapp.R
import com.graduation.healthapp.util.DensityUtil

class TitleLayout : LinearLayout {
    private lateinit var LeftOnClick: () -> Unit
    private lateinit var RightOnClick: () -> Unit
    private lateinit var centerView: TextView

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        orientation = HORIZONTAL
        val array = context!!.obtainStyledAttributes(attrs, R.styleable.TitleLayout) ?: return
        //左右图标
        val Imageparams = LayoutParams(0, LayoutParams.MATCH_PARENT)
        Imageparams.weight = 1f
        val leftView = ImageView(context)
        leftView.setPadding(resources.getDimension(R.dimen.dp_15).toInt())
        if (array.getDrawable(R.styleable.TitleLayout_leftIcon) != null) {
            leftView.setImageDrawable(array.getDrawable(R.styleable.TitleLayout_leftIcon))
            leftView.setOnClickListener {
                LeftOnClick.invoke()
            }
        }
        val rightView = ImageView(context)
        rightView.setPadding(resources.getDimension(R.dimen.dp_15).toInt())
        if (array.getDrawable(R.styleable.TitleLayout_rightIcon) != null) {
            rightView.setImageDrawable(array.getDrawable(R.styleable.TitleLayout_rightIcon))
            rightView.setOnClickListener {
                RightOnClick.invoke()
            }
        }
        //中部文字 默认无
        val Textparams = LayoutParams(0, LayoutParams.MATCH_PARENT)
        Textparams.weight = 3f
        centerView = TextView(context)
        centerView.gravity = 0x11
        if (array.getString(R.styleable.TitleLayout_text) != null) {
            centerView.text = array.getString(R.styleable.TitleLayout_text)
            centerView.textSize = DensityUtil.dip2px(context, 5f)
        }
        addView(leftView, Imageparams)
        addView(centerView, Textparams)
        addView(rightView, Imageparams)
    }

    fun setText(msg: String) {
        centerView.text = msg
    }

    fun setLeftOnClick(block: () -> Unit) {
        LeftOnClick = block
    }

    fun setRightOnClick(block: () -> Unit) {
        RightOnClick = block
    }

}