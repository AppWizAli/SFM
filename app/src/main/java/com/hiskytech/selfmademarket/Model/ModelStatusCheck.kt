package com.hiskytech.selfmademarket.Model

data class ModelStatusCheck(
    val is_subscription_valid: Boolean,
    val is_verified: Int,
    val status: String,
    var message:String=""
)