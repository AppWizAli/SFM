package com.hiskytech.selfmademarket.Model

data class Data(
    val city: String,
    val country: String,
    val created_at: String,
    val district: String,
    val email: String,
    val id: Int,
    val id_card_back_pic: String,
    val id_card_front_pic: String,
    val is_verified: Int,
    val name: String,
    val payment_method: String,
    val phone: String,
    val plan_select: String,
    val postal_code: String,
    val transaction_id: String,
    val transcript_screenshot: String,
    val user_image: String
)