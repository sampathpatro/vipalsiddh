package com.sampathpatro.vipalsiddh.Model

import com.google.gson.annotations.SerializedName

class Review {

    @SerializedName("author_name")
    val authorName: String? = null
    @SerializedName("author_url")
    val authorURL: String? = null
    val language: String? = null
    @SerializedName("profile_photo_url")
    val profilePhotoURL: String? = null
    val rating: Long = 0.toLong()
    @SerializedName("relative_time_description")
    val relativeTimeDescription: String? = null
    val text: String? = null
    val time: Long = 0.toLong()

}
