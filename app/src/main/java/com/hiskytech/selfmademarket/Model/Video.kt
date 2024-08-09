package com.hiskytech.selfmademarket.Model

import android.os.Parcel
import android.os.Parcelable

data class Video(
   val course_id: String,
   val created_at: String,
   val video: String,
   val video_description: String,
   val video_id: String
) : Parcelable {
   constructor(parcel: Parcel) : this(
      parcel.readString().toString(),
      parcel.readString().toString(),
      parcel.readString().toString(),
      parcel.readString().toString(),
      parcel.readString().toString()
   )

   override fun writeToParcel(parcel: Parcel, flags: Int) {
      parcel.writeString(course_id)
      parcel.writeString(created_at)
      parcel.writeString(video)
      parcel.writeString(video_description)
      parcel.writeString(video_id)
   }

   override fun describeContents(): Int {
      return 0
   }

   companion object CREATOR : Parcelable.Creator<Video> {
      override fun createFromParcel(parcel: Parcel): Video {
         return Video(parcel)
      }

      override fun newArray(size: Int): Array<Video?> {
         return arrayOfNulls(size)
      }
   }
}