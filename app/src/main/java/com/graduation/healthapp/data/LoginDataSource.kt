package com.graduation.healthapp.data

import com.graduation.healthapp.data.model.LoggedInUser
import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.CoroutineContext

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun sendSMS(phone: String,block:(Result) -> Unit) {
        val req = JSONObject().apply {
            put("phone",phone)
        }
        NetRequest.Instance.post(MyUrl.sendsms,req){
            NetRequest.Instance.IsResult(block, it)
        }
    }

}