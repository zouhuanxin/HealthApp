package com.graduation.healthapp.ui

import android.os.Bundle
import com.graduation.healthapp.R
import com.graduation.healthapp.databinding.ActivityMainBinding
import com.graduation.healthapp.ui.course.CourseFragment
import com.graduation.healthapp.ui.find.FindFragment
import com.graduation.healthapp.ui.home.HomeFragment
import com.graduation.healthapp.ui.my.MyFragment
import com.graduation.healthapp.ui.record.RecordFragment
import com.graduation.healthapp.util.StatusBarColorUtil

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val statusBarColorUtil = StatusBarColorUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.framlayout, HomeFragment()).commit()
        ClickItem(0)
        initView()
    }

    fun initView() {
        val home = binding.home
        val find = binding.fx
        val record = binding.jl
        val course = binding.kt
        val my = binding.my

        home.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.framlayout, HomeFragment())
                .commit()
            ClickItem(0)
            statusBarColorUtil.initStatus(this,R.color.white2,true)
        }
        find.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.framlayout, FindFragment())
                .commit()
            ClickItem(1)
            statusBarColorUtil.initStatus(this,R.color.white2,true)
        }
        record.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.framlayout, RecordFragment())
                .commit()
            ClickItem(2)
            statusBarColorUtil.initStatus(this,R.color.white2,true)
        }
        course.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.framlayout, CourseFragment())
                .commit()
            ClickItem(3)
            statusBarColorUtil.initStatus(this,R.color.white2,true)
        }
        my.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.framlayout, MyFragment())
                .commit()
            ClickItem(4)
            statusBarColorUtil.initStatus(this,R.color.purple_200,false)
        }
    }

    fun ClickItem(item: Int) {
        binding.homeImage.setImageResource(if (item == 0) R.mipmap.sy2 else R.mipmap.sy1)
        binding.fxImage.setImageResource(if (item == 1) R.mipmap.fx2 else R.mipmap.fx1)
        binding.jlImage.setImageResource(if (item == 2) R.mipmap.jl2 else R.mipmap.jl1)
        binding.ktImage.setImageResource(if (item == 3) R.mipmap.kt2 else R.mipmap.kt1)
        binding.myImage.setImageResource(if (item == 4) R.mipmap.wd2 else R.mipmap.wd1)
    }

}