package com.graduation.healthapp.ui.find

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import com.graduation.healthapp.data.FindRepository
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.util.TempStoreUtil
import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import com.stx.xhb.androidx.entity.BaseBannerInfo
import org.json.JSONObject

class FindViewModel(private val repository: FindRepository) : ViewModel() {
    val urls = MutableLiveData<List<BannerInfo>>()
    val smalclass = MutableLiveData<List<JSONObject>>()
    val contents = MutableLiveData<List<JSONObject>>()
    val favourResult = MutableLiveData<FavourResult>()
    val collectResult = MutableLiveData<FavourResult>()
    private val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!

    init {
        urls.value = arrayListOf(
            BannerInfo("https://img.alicdn.com/imgextra/i2/O1CN01TLgpSz1UQ5D8q3aIH_!!6000000002511-0-tps-2880-1070.jpg", ""),
            BannerInfo("https://img.alicdn.com/imgextra/i1/O1CN01NAnI6W1Vf1kZScscd_!!6000000002679-0-tps-2880-1070.jpg", ""),
            BannerInfo("https://img.alicdn.com/imgextra/i1/O1CN01NAnI6W1Vf1kZScscd_!!6000000002679-0-tps-2880-1070.jpg", "") ,
            BannerInfo("https://staticweb.keepcdn.com/staticShow/images/newhome/app_bg2-f7a530833b.png","")
        )
    }

    fun getAllSmalClass() {
        repository.getAllSmalClass {
            if (it is Result.Success) {
                val json = it.data
                val array = json.getJSONArray("data")
                val list: MutableList<JSONObject> = mutableListOf()
                for (i in 0 until array.length()) {
                    list.add(array.getJSONObject(i))
                }
                smalclass.postValue(list)
            } else {

            }
        }
    }

    //获取信息
    fun getPageData(page: Int, limit: Int) {
        repository.getPageData(
            page,
            limit,
            userinfo.getString("phone")
        ) {
            if (it is Result.Success) {
                val json = it.data
                val array = json.getJSONArray("data")
                val list = mutableListOf<JSONObject>()
                for (i in 0 until array.length()) {
                    list.add(array.getJSONObject(i))
                }
                contents.postValue(list)
            } else {

            }
        }
    }

    //获取信息
    fun getPageData2(page: Int, limit: Int,typeid:Int) {
        val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!
        repository.getPageData2(
            page,
            limit,
            userinfo.getString("phone"),
            typeid
        ) {
            if (it is Result.Success) {
                val json = it.data
                val array = json.getJSONArray("data")
                val list = mutableListOf<JSONObject>()
                for (i in 0 until array.length()) {
                    list.add(array.getJSONObject(i))
                }
                contents.postValue(list)
            } else {

            }
        }
    }

    //点赞
    fun favouropter(id: String, position: Int) {
        repository.favour(id, userinfo.getString("phone")) {
            if (it is Result.Success) {
                if (it.data.getString("message") == "取消点赞成功") {
                    favourResult.postValue(FavourResult(false,position))
                }
                if (it.data.getString("message") == "点赞成功") {
                    favourResult.postValue(FavourResult(true,position))
                }
            } else {
                favourResult.postValue(FavourResult(null,position))
            }
        }
    }

    //收藏
    fun collectopter(id: String, position: Int) {
        repository.collect(id, userinfo.getString("phone")) {
            if (it is Result.Success) {
                if (it.data.getString("message") == "取消收藏成功") {
                    collectResult.postValue(FavourResult(false,position))
                }
                if (it.data.getString("message") == "收藏成功") {
                    collectResult.postValue(FavourResult(true,position))
                }
            } else {
                collectResult.postValue(FavourResult(null,position))
            }
        }
    }


    class BannerInfo(private var url: String, private var title: String) : BaseBannerInfo {

        override fun getXBannerUrl(): Any {
            return url
        }

        override fun getXBannerTitle(): String {
            return title
        }

    }

    data class FavourResult(var status:Boolean?,var position:Int)

}