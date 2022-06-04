package com.graduation.healthapp.ui.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.graduation.healthapp.MyApplication
import com.graduation.healthapp.databinding.ActivitySplashBinding
import com.graduation.healthapp.ui.BaseActivity
import com.graduation.healthapp.ui.MainActivity
import com.graduation.healthapp.ui.login.LoginActivity
import com.graduation.healthapp.ui.login.ViewModelFactory
import com.graduation.healthapp.ui.login.ViewStatus
import com.graduation.healthapp.util.TempStoreUtil
import com.graduation.healthapp.util.ToastUtil
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        //全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setBackgroundDrawable(null)
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        splashViewModel =
            ViewModelProvider(this, ViewModelFactory()).get(SplashViewModel::class.java)

        reqpermission()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initView() {
        //检查本地是否有广告资源
        val data = MyApplication.getApplication().getDaoSession()!!.launchPageAdDao.loadAll()
        if (data != null && data.size > 0) {
            var sta = false
            //默认取第一个有效的广告资源
            for (item in data) {
                val currentday = Calendar.getInstance()
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val date = sdf.parse(item.createdtime)
                val c2 = Calendar.getInstance()
                c2.setTime(date)
                c2.add(Calendar.DAY_OF_MONTH, item.effectiveday)
                if (currentday.before(c2)) {
                    //找到资源开始展示
                    sta = true
                    binding.load.visibility = View.GONE
                    Glide
                        .with(this)
                        .load(item.url)
                        .centerCrop()
                        //.placeholder(R.drawable.loading_spinner)
                        .into(binding.back)
                    go(3000)
                }
            }
            //没有找到任何有效期的广告
            if (!sta){
                go(3000)
            }
        }

        splashViewModel.adData.observe(this) {
            if (it.success != null) {
                ToastUtil.toast(this, it.success)
            }
            if (it.error != null) {
                ToastUtil.toast(this, it.error)
            }
            if (data == null || data.size == 0) {
                go(0)
            }
        }

        splashViewModel.getLaunchPageAd()

    }

    //启动倒计时
    fun go(time: Long) {
        MainScope().launch {
            delay(time)
            //检查是否已经登陆
            if (TempStoreUtil.get(TempStoreUtil.USERNAME) != null) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
                return@launch
            }
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }
    }


    private fun reqpermission() {
        XXPermissions.with(this)
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .permission(Permission.READ_EXTERNAL_STORAGE)
            .request(object : OnPermissionCallback {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onGranted(permissions: List<String>, all: Boolean) {
                    if (all) {
                        initView()
                    } else {
                        ToastUtil.toast(this@SplashActivity, "获取部分权限成功，但部分权限未正常授予")
                    }
                }

                override fun onDenied(permissions: List<String>, never: Boolean) {
                    if (never) {
                        ToastUtil.toast(this@SplashActivity, "被永久拒绝授权，请手动授予权限")
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(this@SplashActivity, permissions)
                    } else {
                        ToastUtil.toast(this@SplashActivity, "获取权限失败")
                    }
                }
            })
    }
}