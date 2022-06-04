package com.graduation.healthapp.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.data.SplashRepository
import com.graduation.healthapp.ui.login.ViewStatus

class SplashViewModel(val repository: SplashRepository) : ViewModel() {
    var adData = MutableLiveData<ViewStatus>()

    fun getLaunchPageAd() {
        repository.getLaunchPageAd {
            if (it is Result.Success) {
                adData.postValue(ViewStatus(success = "资源缓存成功"))
            } else {
                adData.postValue(ViewStatus(success = "资源缓存失败"))
            }
        }
    }

}