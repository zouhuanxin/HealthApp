package com.graduation.healthapp.ui.login

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduation.healthapp.data.LoginRepository
import com.graduation.healthapp.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PassWordLoginViewModel(private val repository: LoginRepository) : ViewModel() {
    val viewStatusResult: MutableLiveData<ViewStatus> = MutableLiveData()

    fun login(pass: String, phone: String) = CoroutineScope(Dispatchers.IO).launch {
        if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(phone)){
            viewStatusResult.value = ViewStatus(error = "登陆失败")
            return@launch
        }
        repository.loginPass(pass, phone){
            if (it is Result.Success) {
                viewStatusResult.postValue(ViewStatus(success = "登陆成功"))
            } else {
                viewStatusResult.postValue(ViewStatus(error = "登陆失败"))
            }
        }
    }

}