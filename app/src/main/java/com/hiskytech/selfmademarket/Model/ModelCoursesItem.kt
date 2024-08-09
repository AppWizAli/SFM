package com.hiskytech.selfmademarket.Model
import android.os.Parcel
import android.os.Parcelable

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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.createTypedArrayList(Video.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(course_duration)
        parcel.writeString(course_id)
        parcel.writeString(course_image)
        parcel.writeString(course_instructor)
        parcel.writeString(course_level)
        parcel.writeString(course_name)
        parcel.writeString(created_at)
        parcel.writeString(total_videos)
        parcel.writeTypedList(videos)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelCoursesItem> {
        override fun createFromParcel(parcel: Parcel): ModelCoursesItem {
            return ModelCoursesItem(parcel)
        }

        override fun newArray(size: Int): Array<ModelCoursesItem?> {
            return arrayOfNulls(size)
        }
    }
}