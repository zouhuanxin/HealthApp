package com.graduation.healthapp.ui.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.graduation.healthapp.R

class SplashComponent : View {
    private var raous = 0.0
    //彩色数组
    private val colors = arrayOf("#668B8B","#708090","#FF6347","#FFB90F","#8B8682","#436EEE")

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //画圆
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.parseColor("#04ddcb") //主题色
        paint.style = Paint.Style.FILL
        canvas!!.drawCircle(width / 2f, height / 2f, resources.getDimension(R.dimen.dp_60), paint)
        //写字
        val paint2 = Paint()
        paint2.isAntiAlias = true
        paint2.color = Color.parseColor("#ffffff")
        paint2.textSize = resources.getDimension(R.dimen.sp_25)
        canvas.drawText(
            "100%",
            width / 2f - resources.getDimension(R.dimen.dp_25),
            height / 2f + resources.getDimension(R.dimen.dp_8),
            paint2
        )
        paint2.color = Color.parseColor("#000000")
        paint2.textSize = resources.getDimension(R.dimen.sp_15)
        canvas.drawText(
            "资源加载中...",
            width / 2f - resources.getDimension(R.dimen.dp_40),
            height / 2f + resources.getDimension(R.dimen.dp_130),
            paint2
        )
        //画旁边的小圆圈
        var r = resources.getDimension(R.dimen.dp_80)
        for (i in 1..6) {
            paint.color = Color.parseColor(colors[(i-1)])
            val x = width / 2f + r * Math.sin(Math.toRadians(raous + (i-1)*30))
            val y = height / 2f + r * Math.cos(Math.toRadians(raous + (i-1)*30))
            canvas!!.drawCircle(
                x.toFloat(),
                y.toFloat(),
                resources.getDimension(R.dimen.dp_10),
                paint
            )
        }
        if (radianState == null){
            radianState = RadianState()
            radianState!!.draw()
        }
    }

    private var radianState: RadianState? = null
    inner class RadianState {
        private val dur = 1000L

        fun draw(){
            val mValueAnimator = ValueAnimator.ofInt(3, 3)
            mValueAnimator.setInterpolator(LinearInterpolator())
            mValueAnimator.setDuration(dur)
            mValueAnimator.repeatCount = 10000
            mValueAnimator.addUpdateListener {
                if (raous >= 360.0) raous = 0.0
                raous = raous + it.animatedValue as Int
                invalidate()
            }
            mValueAnimator.start()
        }

    }

}