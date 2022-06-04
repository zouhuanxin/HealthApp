package com.graduation.healthapp.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.graduation.healthapp.databinding.ActivityPassWordLoginBinding
import com.graduation.healthapp.ui.BaseActivity
import com.graduation.healthapp.ui.MainActivity
import com.graduation.healthapp.util.ToastUtil

class PassWordLoginActivity : BaseActivity() {
    private lateinit var binding: ActivityPassWordLoginBinding
    private lateinit var passWordLoginViewModel: PassWordLoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassWordLoginBinding.inflate(layoutInflater)
        passWordLoginViewModel =
            ViewModelProvider(this, ViewModelFactory()).get(PassWordLoginViewModel::class.java)
        setContentView(binding.root)
        initView()
    }

    fun initView() {
        val titlebar = binding.titlebar
        val phone = binding.phone
        val pass = binding.password
        val login = binding.login

        titlebar.setLeftOnClick {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        passWordLoginViewModel.viewStatusResult.observe(this, {
            if (it.error != null) {
                ToastUtil.toast(this,it.error)
                return@observe
            }
            if (it.success != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })

        login.setOnClickListener {
            passWordLoginViewModel.login(pass.text.toString(),phone.text.toString())
        }
    }
}