package com.graduation.healthapp.util.net

import com.graduation.healthapp.data.Result
import com.graduation.healthapp.util.TempStoreUtil
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

open class NetRequest {
    object Instance : NetRequest()

    private var okHttpClient: OkHttpClient
    private var ERRORMSG = JSONObject()
    private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

    init {
        okHttpClient = OkHttpClient()
        ERRORMSG.put("code", 400)
        ERRORMSG.put("message", "请求失败")
        ERRORMSG.put("data", null)
    }

    fun get(url: String, block: (result: Result) -> Unit) =
        CoroutineScope(Dispatchers.IO).launch {
            val store = TempStoreUtil.get(TempStoreUtil.USERNAME)
            val request = Request.Builder()
                .header("token", if (store == null) "" else store.getString("token"))
                .url(url)
                .get()
                .build()
            var response: Response? = null
            try {
                response = okHttpClient.newCall(request).execute()
            } catch (e: Exception) {
                block.invoke(Result.Error(ERRORMSG))
                e.printStackTrace()
                return@launch
            }
            if (response.code == 200) {
                val str = response.body!!.string()
                println("$url:$str")
                val r = JSONObject(str)
                if (r.getInt("code") == 200) {
                    block.invoke(Result.Success(r))
                } else {
                    block.invoke(Result.Error(ERRORMSG))
                }
            } else {
                block.invoke(Result.Error(ERRORMSG))
            }
        }

    fun post(url: String, params: JSONObject, block: (result: Result) -> Unit) =
        CoroutineScope(Dispatchers.IO).launch {
            val store = TempStoreUtil.get(TempStoreUtil.USERNAME)
            val requestBody = okhttp3.RequestBody.create(JSON, params.toString())
            val request = Request.Builder()
                .header("token", if (store == null) "" else store.getString("token"))
                .url(url)
                .post(requestBody)
                .build()
            var response: Response? = null
            try {
                response = okHttpClient.newCall(request).execute()
            } catch (e: Exception) {
                block.invoke(Result.Error(ERRORMSG))
                e.printStackTrace()
                return@launch
            }
            println("响应状态码:${response!!.code}")
            if (response!!.code == 200) {
                val str = response!!.body!!.string()
                println("$url:$str")
                val r = JSONObject(str)
                if (r.getInt("code") == 200) {
                    block.invoke(Result.Success(r))
                } else {
                    block.invoke(Result.Error(ERRORMSG))
                }
            } else {
                block.invoke(Result.Error(ERRORMSG))
            }
        }

    fun IsResult(block: (Result) -> Unit, it: Result) {
        if (it is Result.Success) {
            block.invoke(
                if (it.data.getInt("code") == 200) Result.Success(it.data) else Result.Error(
                    it.data
                )
            )
        } else if (it is Result.Error) {
            block.invoke(Result.Error(it.data))
        }
    }

}