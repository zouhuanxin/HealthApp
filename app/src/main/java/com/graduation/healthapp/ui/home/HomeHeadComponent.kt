package com.graduation.healthapp.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.graduation.healthapp.R

import android.graphics.BitmapFactory
import android.view.MotionEvent
import android.view.animation.LinearInterpolator

class HomeHeadComponent(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (searchRight == 0f) {
            searchRight = width - resources.getDimension(R.dimen.dp_15)
            searchRightstart = width - resources.getDimension(R.dimen.dp_15)
            searchRightend = width - resources.getDimension(R.dimen.dp_100)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    //health字体的坐标线
    private var healthX: Float = resources.getDimension(R.dimen.dp_20)
    private var healthY: Float = resources.getDimension(R.dimen.dp_30)
    private var healthTransparency: Int = 255

    //搜索框的left top right bottom
    private var searchLeft = resources.getDimension(R.dimen.dp_15)
    private var searchTop = resources.getDimension(R.dimen.dp_60)
    private var searchTopstart = resources.getDimension(R.dimen.dp_60)
    private var searchTopend = resources.getDimension(R.dimen.dp_7)
    private var searchRight = 0f
    private var searchRightstart = 0f
    private var searchRightend = 0f
    private var searchBottom = resources.getDimension(R.dimen.dp_97)

    //搜索框文字的内容坐标线
    private var searchTextX = resources.getDimension(R.dimen.dp_60)
    private var searchTextY = resources.getDimension(R.dimen.dp_84)
    private var searchTextYstart = resources.getDimension(R.dimen.dp_84)
    private var searchTextYend = resources.getDimension(R.dimen.dp_30)

    //搜索框Icon位移
    private var searchIconX = resources.getDimension(R.dimen.dp_150)
    private var searchIconY =
        resources.getDimension(R.dimen.dp_370) + resources.getDimension(R.dimen.dp_10)
    private var searchIconYstart =
        resources.getDimension(R.dimen.dp_370) + resources.getDimension(R.dimen.dp_10)
    private var searchIconYend =
        resources.getDimension(R.dimen.dp_85)

    private var ViewStatus = true

    @SuppressLint("ResourceAsColor")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //写字
        val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.parseColor("#000000")
        paint.strokeWidth = resources.getDimension(R.dimen.dp_0_5)
        paint.isAntiAlias = true
        paint.alpha = healthTransparency
        paint.textSize = resources.getDimension(R.dimen.sp_20)
        canvas!!.drawText("爱健康", healthX, healthY, paint)

        //画未读信息
        val paint2: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val bitmap1 = BitmapFactory.decodeResource(resources, R.mipmap.punch_card)
        val m = Matrix()
        m.setTranslate(
            width * 4 - resources.getDimension(R.dimen.dp_100),
            resources.getDimension(R.dimen.dp_50)
        )
        m.postScale(0.23F, 0.23F)
        canvas.drawBitmap(bitmap1, m, paint2)
        //画打卡
        val bitmap2 = BitmapFactory.decodeResource(resources, R.mipmap.punch_card)
        val m2 = Matrix()
        m2.setTranslate(
            width * 4 - resources.getDimension(R.dimen.dp_240),
            resources.getDimension(R.dimen.dp_50)
        )
        val t = (bitmap1.height * 0.23) / bitmap2.height
        m2.postScale(t.toFloat(), t.toFloat())
       // canvas.drawBitmap(bitmap2, m2, paint2)
        //画搜索框
        val paint3 = Paint(Paint.ANTI_ALIAS_FLAG)
        paint3.color = Color.parseColor("#eeeff2")
        paint3.isAntiAlias = true
        paint3.strokeWidth = resources.getDimension(R.dimen.dp_2)
        val searchRectF = RectF()
        searchRectF.set(searchLeft, searchTop, searchRight, searchBottom)
        canvas.drawRoundRect(searchRectF, 100F, 100F, paint3)
        val paint4: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint4.color = Color.parseColor("#8c92a1")
        paint4.strokeWidth = resources.getDimension(R.dimen.dp_0_5)
        paint4.isAntiAlias = true
        paint4.textSize = resources.getDimension(R.dimen.sp_13)
        canvas!!.drawText("请输入搜索内容", searchTextX, searchTextY, paint4)
        //画搜索图标
        val searchIcon = BitmapFactory.decodeResource(resources, R.mipmap.search)
        val m3 = Matrix()
        m3.setTranslate(searchIconX, searchIconY)
        m3.postScale(0.18F, 0.18F)
        canvas.drawBitmap(searchIcon, m3, paint2)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x > resources.getDimension(R.dimen.dp_270) &&
                    event.x < resources.getDimension(R.dimen.dp_300) &&
                    event.y > resources.getDimension(R.dimen.dp_10) &&
                    event.y < resources.getDimension(R.dimen.dp_40)
                ) {
                    //打卡点击事件
                    this.pcc.invoke()
                } else if (event.x > resources.getDimension(R.dimen.dp_310) &&
                    event.x < resources.getDimension(R.dimen.dp_334) &&
                    event.y > resources.getDimension(R.dimen.dp_10) &&
                    event.y < resources.getDimension(R.dimen.dp_40)
                ) {
                    //通知点击事件
                    this.ifc.invoke()
                } else if (event.x > searchLeft &&
                    event.x < searchRight &&
                    event.y > searchTop &&
                    event.y < searchBottom
                ) {
                    //输入框点击事件
                    this.sc.invoke()
                }
            }
            else -> println()
        }
        return super.onTouchEvent(event)
    }

    fun open() {
        if (ViewStatus) return
        ViewStatus = true
        if (healthState == null) healthState = HealthState()
        healthState!!.drawReset()
    }

    fun close() {
        if (!ViewStatus) return
        ViewStatus = false
        if (healthState == null) healthState = HealthState()
        healthState!!.drawStart()
    }

    private lateinit var pcc: () -> Unit
    private lateinit var ifc: () -> Unit
    private lateinit var sc: () -> Unit
    fun PunchCardClick(pcc: () -> Unit): Unit {
        this.pcc = pcc
    }

    fun InFormClick(ifc: () -> Unit): Unit {
        this.ifc = ifc
    }

    fun SearchClick(sc: () -> Unit): Unit {
        this.sc = sc
    }

    private var healthState: HealthState? = null

    //Health渐变隐藏，搜索框变短移动到上方
    inner class HealthState {
        private val dur = 100L

        fun drawStart() {
            val mValueAnimator = ValueAnimator.ofInt(255, 0)
            mValueAnimator.setInterpolator(LinearInterpolator())
            mValueAnimator.setDuration(dur)
            mValueAnimator.addUpdateListener {
                healthTransparency = it.animatedValue as Int
                invalidate()
            }
            mValueAnimator.start()
            val mValueAnimator2 = ValueAnimator.ofFloat(searchRight, searchRightend)
            mValueAnimator2.setInterpolator(LinearInterpolator())
            mValueAnimator2.setDuration(dur)
            mValueAnimator2.addUpdateListener {
                searchRight = it.animatedValue as Float
                invalidate()
            }
            mValueAnimator2.start()
            val mValueAnimator3 = ValueAnimator.ofFloat(searchTop, searchTopend)
            mValueAnimator3.setInterpolator(LinearInterpolator())
            mValueAnimator3.setDuration(dur)
            mValueAnimator3.addUpdateListener {
                searchBottom = searchBottom - searchTop + it.animatedValue as Float
                searchTop = it.animatedValue as Float
                invalidate()
            }
            mValueAnimator3.start()
            val mValueAnimator4 = ValueAnimator.ofFloat(searchTextY, searchTextYend)
            mValueAnimator4.setInterpolator(LinearInterpolator())
            mValueAnimator4.setDuration(dur)
            mValueAnimator4.addUpdateListener {
                searchTextY = it.animatedValue as Float
                invalidate()
            }
            mValueAnimator4.start()
            val mValueAnimator5 = ValueAnimator.ofFloat(searchIconY, searchIconYend)
            mValueAnimator5.setInterpolator(LinearInterpolator())
            mValueAnimator5.setDuration(dur)
            mValueAnimator5.addUpdateListener {
                searchIconY = it.animatedValue as Float
                invalidate()
            }
            mValueAnimator5.start()
            mValueAnimator5.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    layoutParams.height = resources.getDimension(R.dimen.dp_50).toInt()
                    requestLayout()
                }
            })
        }

        fun drawReset() {
            val mValueAnimator = ValueAnimator.ofInt(0, 255)
            mValueAnimator.setInterpolator(LinearInterpolator())
            mValueAnimator.setDuration(dur)
            mValueAnimator.addUpdateListener {
                healthTransparency = it.animatedValue as Int
                invalidate()
            }
            mValueAnimator.start()
            val mValueAnimator2 = ValueAnimator.ofFloat(searchRightend, searchRightstart)
            mValueAnimator2.setInterpolator(LinearInterpolator())
            mValueAnimator2.setDuration(dur)
            mValueAnimator2.addUpdateListener {
                searchRight = it.animatedValue as Float
                invalidate()
            }
            mValueAnimator2.start()
            val mValueAnimator3 = ValueAnimator.ofFloat(searchTopend, searchTopstart)
            mValueAnimator3.setInterpolator(LinearInterpolator())
            mValueAnimator3.setDuration(dur)
            mValueAnimator3.addUpdateListener {
                searchBottom = searchBottom - searchTop + it.animatedValue as Float
                searchTop = it.animatedValue as Float
                invalidate()
            }
            mValueAnimator3.start()
            val mValueAnimator4 = ValueAnimator.ofFloat(searchTextYend, searchTextYstart)
            mValueAnimator4.setInterpolator(LinearInterpolator())
            mValueAnimator4.setDuration(dur)
            mValueAnimator4.addUpdateListener {
                searchTextY = it.animatedValue as Float
                invalidate()
            }
            mValueAnimator4.start()
            val mValueAnimator5 = ValueAnimator.ofFloat(searchIconYend, searchIconYstart)
            mValueAnimator5.setInterpolator(LinearInterpolator())
            mValueAnimator5.setDuration(dur)
            mValueAnimator5.addUpdateListener {
                searchIconY = it.animatedValue as Float
                invalidate()
            }
            mValueAnimator5.start()
            mValueAnimator5.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    layoutParams.height = resources.getDimension(R.dimen.dp_100).toInt()
                    requestLayout()
                }
            })
        }

    }

}