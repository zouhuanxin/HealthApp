package com.graduation.healthapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduation.healthapp.data.LoginRepository
import com.graduation.healthapp.data.Result

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<ViewStatus>()
    val loginResult: LiveData<ViewStatus> = _loginResult

    fun sendSMS(phone: String) {
        loginRepository.sendSMS(phone) {
            if (it is Result.Success) {
                _loginResult.postValue(ViewStatus(success = "发送成功"))
            } else {
                _loginResult.postValue(ViewStatus(error = "发送失败"))
            }
        }
    }

}