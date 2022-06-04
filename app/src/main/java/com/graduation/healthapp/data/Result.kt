package com.graduation.healthapp.data

import org.json.JSONObject

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result {

    data class Success(val data: JSONObject) : Result()
    data class Error(val data: JSONObject) : Result()

}