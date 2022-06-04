package com.graduation.healthapp.ui.login

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.graduation.healthapp.ui.MainActivity
import com.graduation.healthapp.databinding.ActivityLoginBinding
import com.graduation.healthapp.ui.BaseActivity
import com.graduation.healthapp.util.TempStoreUtil


class LoginActivity : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phone = binding.phone
        val login = binding.login
        val loading = binding.loading

        phone!!.addTextChangedListener {
            login.isEnabled = it!!.length == 11
        }

        loginViewModel = ViewModelProvider(this, ViewModelFactory()).get(LoginViewModel::class.java)

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            login.isEnabled = true

            if (loginResult.error != null) {

            }
            if (loginResult.success != null) {

            }
            val intent = Intent(this@LoginActivity, VerifyCodeActivity::class.java)
            intent.putExtra("phone",phone.text.toString())
            intent.putExtra("type",PageParameter.LOGIN)
            startActivity(intent)
            finish()
        })

        binding.passLogin!!.setOnClickListener {
            startActivity(Intent(this,PassWordLoginActivity::class.java))
            finish()
        }
        binding.fastRegister!!.setOnClickListener {
            startActivity(Intent(this,FastRegisterActivity::class.java))
            finish()
        }

        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            login.isEnabled = false
            loginViewModel.sendSMS(phone!!.text.toString())
        }
    }

}

