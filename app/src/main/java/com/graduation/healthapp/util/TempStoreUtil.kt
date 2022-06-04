package com.graduation.healthapp.util

import android.content.Context
import android.content.SharedPreferences
import com.graduation.healthapp.MyApplication
import org.json.JSONObject

class TempStoreUtil {

    companion object {
        private val HEALTHAPP = "HEALTHAPP"
        //个人信息key值,临时保存本地数据
        val USERNAME = "userinfo"

        fun getSharedPreferences(): SharedPreferences {
            return MyApplication.getApplication()
                .getSharedPreferences(HEALTHAPP, Context.MODE_PRIVATE)
        }

        fun SharedPreferences.open(block: SharedPreferences.Editor.() -> Unit) {
            val editor = edit()
            editor.block()
            editor.apply()
        }

        fun save(key: String, o: Any?) {
            val json = JSONObject(o.toString())
            getSharedPreferences().open {
                putString(key, json.toString())
                commit()
            }
        }

        fun get(key: String): JSONObject?{
            val json = getSharedPreferences().getString(key, "")
            return if (json.equals("")) null else JSONObject(json)
        }

        fun getUserInfo(): JSONObject{
            return get(USERNAME)!!
        }

        fun clear(){
            getSharedPreferences().open {
                remove(USERNAME)
                commit()
            }
        }

    }

}