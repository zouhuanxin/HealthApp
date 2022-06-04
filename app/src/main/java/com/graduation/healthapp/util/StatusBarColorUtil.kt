package com.graduation.healthapp.util

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.graduation.healthapp.R
import com.gyf.immersionbar.ImmersionBar

class StatusBarColorUtil {

    /**
     * 谷歌原生
     * 字体黑色传true反之白色传false
     */
    fun setAndroidNativeLightStatusBar(activity: Activity, dark: Boolean): Unit {
        val decor = activity.window.decorView
        if (dark) {
            decor.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            decor.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    fun initStatus(context: Fragment, color: Int,dark: Boolean):StatusBarColorUtil {
        ImmersionBar.with(context)
            .statusBarDarkFont(dark)
            .statusBarColor(color) //状态栏颜色，不写默认透明色
            .init() //必须调用方可应用以上所配置的参数
        return this
    }

    fun initStatus(context: Activity, color: Int,dark: Boolean):StatusBarColorUtil {
        ImmersionBar.with(context)
            .statusBarDarkFont(dark)
            .statusBarColor(color) //状态栏颜色，不写默认透明色
            .init() //必须调用方可应用以上所配置的参数
        return this;
    }

    //小米

    //魅族

    //华为

    //oppo

    //vivo

}