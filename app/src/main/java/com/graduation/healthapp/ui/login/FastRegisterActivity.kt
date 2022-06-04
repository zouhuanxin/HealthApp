package com.graduation.healthapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.graduation.healthapp.R
import com.graduation.healthapp.databinding.ActivityFastRegisterBinding
import com.graduation.healthapp.ui.BaseActivity

class FastRegisterActivity : BaseActivity() {
    private lateinit var binding:ActivityFastRegisterBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFastRegisterBinding.inflate(layoutInflater)
        loginViewModel = ViewModelProvider(this, ViewModelFactory()).get(LoginViewModel::class.java)
        setContentView(binding.root)
        initView()
    }

    fun initView(){
        val phone = binding.phone
        val login = binding.login
        val loading = binding.loading

        phone!!.addTextChangedListener {
            login.isEnabled = it!!.length == 11
        }

        loginViewModel.loginResult.observe(this, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            login.isEnabled = true

            if (loginResult.error != null) {

            }
            if (loginResult.success != null) {

            }
            val intent = Intent(this, VerifyCodeActivity::class.java)
            intent.putExtra("phone",phone.text.toString())
            intent.putExtra("type",PageParameter.REGISTER)
            startActivity(intent)
            finish()
        })

        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            login.isEnabled = false
            loginViewModel.sendSMS(phone!!.text.toString())
        }
    }

}