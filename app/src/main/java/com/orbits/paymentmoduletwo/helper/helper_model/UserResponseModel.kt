package com.orbits.paymentmoduletwo.helper.helper_model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseModel(
    val `data`: UserDataModel? = null,
    var message: String? = null,
    var code: String? = null,
    val status: Int? = null,
    val success: Boolean? = null
)

@Serializable
data class UserDataModel(
    val appVersion: String? = null,
    val createdAt: String? = null,
    val deviceModel: String? = null,
    val deviceToken: String? = null,
    val deviceType: String? = null,
    val dob: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val id: Int? = null,
    val isCodeVerified: Boolean? = false,
    val osVersion: String? = null,
    var connection_code : String = ""
)
