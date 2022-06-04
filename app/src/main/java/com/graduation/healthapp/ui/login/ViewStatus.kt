package com.graduation.healthapp.ui.login

/**
 * Authentication result : success (user details) or error message.
 */
data class ViewStatus(
    val success: String? = null,
    val error: String? = null
)