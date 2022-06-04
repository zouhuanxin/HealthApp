package com.graduation.healthapp.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduation.healthapp.data.LoginRepository
import com.graduation.healthapp.data.Result
import kotlinx.coroutines.runBlocking

class VerifyCodeViewModel(private val repository: LoginRepository) : ViewModel() {
    val verifyResult: MutableLiveData<ViewStatus> = MutableLiveData<ViewStatus>()
    val registerResult: MutableLiveData<ViewStatus> = MutableLiveData<ViewStatus>()

    fun verify(code: String, phone: String) = runBlocking {
        repository.verify(code, phone){
            if (it is Result.Success) {
                verifyResult.postValue(ViewStatus(success = "验证成功"))
            } else {
                verifyResult.postValue(ViewStatus(error = "验证失败"))
            }
        }
    }

    fun register(phone: String){
        repository.register(phone){
            if (it is Result.Success) {
                registerResult.postValue(ViewStatus(success = "注册成功"))
            } else {
                registerResult.postValue(ViewStatus(error = "注册失败"))
            }
        }
    }

}