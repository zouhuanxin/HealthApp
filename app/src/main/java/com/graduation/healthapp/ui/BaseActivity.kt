package com.graduation.healthapp.ui

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.graduation.healthapp.util.StatusBarColorUtil

open class BaseActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarColorUtil().setAndroidNativeLightStatusBar(this, true)
    }

}