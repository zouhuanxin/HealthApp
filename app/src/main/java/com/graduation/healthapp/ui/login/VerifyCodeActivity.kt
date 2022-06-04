package com.graduation.healthapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.graduation.healthapp.databinding.ActivityVerifyCodeBinding
import com.graduation.healthapp.ui.BaseActivity
import com.graduation.healthapp.ui.MainActivity

class VerifyCodeActivity : BaseActivity() {
    private lateinit var verifyCodeBinding: ActivityVerifyCodeBinding
    private lateinit var verifyCodeViewModel: VerifyCodeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyCodeBinding = ActivityVerifyCodeBinding.inflate(layoutInflater)
        verifyCodeViewModel = ViewModelProvider(this, ViewModelFactory())
            .get(VerifyCodeViewModel::class.java)
        setContentView(verifyCodeBinding.root)
        initView()
    }

    fun initView() {
        verifyCodeViewModel.verifyResult.observe(this, {

            if (it.error != null) {
                Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                return@observe
            }
            if (it.success != null) {
                when(intent.getSerializableExtra("type")) {
                    PageParameter.LOGIN -> {
                        Toast.makeText(this, it.success, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    PageParameter.REGISTER -> verifyCodeViewModel.register(intent.getStringExtra("phone").toString().trim())
                }
                return@observe
            }
        })
        verifyCodeViewModel.registerResult.observe(this,{
            if (it.error != null) {
                Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                return@observe
            }
            if (it.success != null) {
                Toast.makeText(this, it.success, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return@observe
            }
        })
        verifyCodeBinding.verifyTitlelayout.setLeftOnClick {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        verifyCodeBinding.verifyinfo.text = "验证码已发送至 " + intent.getStringExtra("phone")
        verifyCodeBinding.verifyinput.setCallback {
            verifyCodeViewModel.verify(it, intent.getStringExtra("phone").toString().trim())
        }
    }

}