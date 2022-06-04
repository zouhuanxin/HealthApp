package com.graduation.healthapp.data

import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest

class SplashDataSource {

    fun getLaunchPageAd(block: (Result) -> Unit) {
        NetRequest.Instance.get(MyUrl.getEffectiveAd) {
            block.invoke(it)
        }
    }

}