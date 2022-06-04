package com.graduation.healthapp.data

import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import org.json.JSONObject

class VerifyCodeDataSource {

    fun verify(code: String, phone: String, block: (Result) -> Unit) {
        val req = JSONObject().apply {
            put("phone", phone)
            put("account", code)
        }
        NetRequest.Instance.post(MyUrl.verify,req){
            NetRequest.Instance.IsResult(block, it)
        }
    }

    fun register(phone: String, block: (Result) -> Unit) {
        val req = JSONObject().apply {
            put("phone", phone)
            put("source", "app注册")
        }
        NetRequest.Instance.post(MyUrl.register,req){
            NetRequest.Instance.IsResult(block, it)
        }
    }

    //密码登陆
    fun loginPass(pass: String, phone: String, block: (Result) -> Unit) {
        val req = JSONObject().apply {
            put("phone", phone)
            put("password", pass)
        }
        NetRequest.Instance.post(MyUrl.login, req) {
            NetRequest.Instance.IsResult(block, it)
        }
    }

}