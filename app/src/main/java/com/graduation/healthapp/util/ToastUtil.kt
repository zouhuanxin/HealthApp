package com.graduation.healthapp.util

import android.content.Context
import android.widget.Toast

class ToastUtil {

    companion object{

        fun toast(context: Context? = null,msg:String? = "",type:Int? = Toast.LENGTH_SHORT){
            Toast.makeText(context,msg, type!!).show()
        }

    }

}