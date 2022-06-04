package com.graduation.healthapp.data

import com.alibaba.fastjson.JSON
import com.graduation.healthapp.MyApplication
import com.graduation.healthapp.data.model.LaunchPageAd
import com.graduation.healthapp.gen.LaunchPageAdDao.Properties.*

class SplashRepository(val datasource: SplashDataSource) {

    /**
     * 获取所有有效广告启动图
     */
    fun getLaunchPageAd(block: (Result) -> Unit) {
        datasource.getLaunchPageAd {
            block.invoke(it)
            if (it is Result.Success) {
                //缓存数据
                val dao = MyApplication.getApplication().getDaoSession()!!.launchPageAdDao
                val arr = it.data.getJSONArray("data")
                for (index in 0 until arr.length()) {
                    val ad = JSON.parseObject(
                        arr.getJSONObject(index).toString(),
                        LaunchPageAd::class.java
                    )
                    val list = dao.queryBuilder().where(Id.eq(ad.id)).build().list()
                    if (list != null && list.size == 1 && list.get(0) != null) {
                        dao.deleteByKey(list.get(0).keyid)
                    }
                    dao.save(ad)
                }
            }
        }
    }


}