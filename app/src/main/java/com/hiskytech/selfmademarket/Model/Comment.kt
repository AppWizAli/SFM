package com.hiskytech.selfmademarket.Model

data class Comment(
    val approved_by_admin: String,
    val created_at: String,
    val description: String,
    val id: String,
    val image: String,
    val user_name: String
)