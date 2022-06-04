package com.graduation.healthapp.data.model

import org.json.JSONObject

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val phone: String,
    val token: String,
    val username: String? = "",
    val portrait: String? = ""
) {
    override fun toString(): String {
        val json = JSONObject()
        json.put("phone", phone)
        json.put("token", token)
        json.put("username", username)
        json.put("portrait", portrait)
        return json.toString()
    }
}