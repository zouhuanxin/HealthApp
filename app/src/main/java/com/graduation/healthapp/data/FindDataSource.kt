package com.graduation.healthapp.data

import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest

class FindDataSource {

    fun getAllSmalClass(block: (result: Result) -> Unit) {
        NetRequest.Instance.get(MyUrl.getAllSmalClass) {
            NetRequest.Instance.IsResult(block, it)
        }
    }

    fun getPageData(page: Int, limit: Int, user: String, block: (result: Result) -> Unit) {
        NetRequest.Instance.get(MyUrl.getPageData + "?page=$page&limit=$limit&user=$user&type=0") {
            NetRequest.Instance.IsResult(block, it)
        }
    }

    fun getPageData2(page: Int, limit: Int, user: String,typeid:Int, block: (result: Result) -> Unit) {
        NetRequest.Instance.get(MyUrl.getPageData2 + "?page=$page&limit=$limit&user=$user&typeid=$typeid") {
            NetRequest.Instance.IsResult(block, it)
        }
    }

    fun favour(id: String, user: String, block: (result: Result) -> Unit) {
        NetRequest.Instance.get(MyUrl.favour + "?id=$id&user=$user") {
            NetRequest.Instance.IsResult(block, it)
        }
    }

    fun collect(id: String, user: String, block: (result: Result) -> Unit) {
        NetRequest.Instance.get(MyUrl.collect + "?id=$id&user=$user") {
            NetRequest.Instance.IsResult(block, it)
        }
    }

}