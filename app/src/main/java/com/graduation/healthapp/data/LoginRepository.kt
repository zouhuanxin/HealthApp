package com.graduation.healthapp.data

import com.graduation.healthapp.data.model.LoggedInUser
import com.graduation.healthapp.util.TempStoreUtil

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource, val dataSourceVerify: VerifyCodeDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        user = null
    }

    fun sendSMS(phone: String, block: (Result) -> Unit) {
        dataSource.sendSMS(phone) {
            block.invoke(it)
        }
    }

    //验证码验证
    fun verify(code: String, phone: String, block: (Result) -> Unit) {
        dataSourceVerify.verify(code, phone) {
            block.invoke(it)
            if (it is Result.Success) {
                setLoggedInUser(
                    LoggedInUser(
                        phone = phone,
                        token = it.data.getString("token"),
                        username = it.data.getJSONObject("data").getString("username"),
                        portrait = it.data.getJSONObject("data").getString("portrait")
                    )
                )
            }
        }
    }

    //注册账号
    fun register(phone: String, block: (Result) -> Unit) {
        dataSourceVerify.register(phone) {
            block.invoke(it)
        }
    }

    //密码登陆
    fun loginPass(pass: String, phone: String, block: (Result) -> Unit) {
        dataSourceVerify.loginPass(pass, phone) {
            block.invoke(it)
            if (it is Result.Success) {
                setLoggedInUser(
                    LoggedInUser(
                        phone = phone,
                        token = it.data.getString("token"),
                        username = it.data.getJSONObject("data").getString("username"),
                        portrait = it.data.getJSONObject("data").getString("portrait")
                    )
                )
            }
        }
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        TempStoreUtil.save(TempStoreUtil.USERNAME, this.user)
    }
}