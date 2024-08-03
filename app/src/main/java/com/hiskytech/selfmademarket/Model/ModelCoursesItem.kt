package com.hiskytech.selfmademarket.Model

data class ModelCoursesItem(
    val course_duration: String,
    val course_id: String,
    val course_image: String,
    val course_instructor: String,
    val course_level: String,
    val course_name: String,
    val created_at: String,
    val total_videos: String,
    val videos: List<Video>
)