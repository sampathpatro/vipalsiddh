package com.sampathpatro.vipalsiddh.Model

import com.google.gson.annotations.SerializedName

class MyPlaces {
    @SerializedName("error_message")
    var error_message: String? = null
    @SerializedName("html_attributions")
    var html_attributions: Array<String>? = null
    @SerializedName("next_page_token")
    var next_page_token: String? = null
    @SerializedName("results")
    var results: Array<Result>? = null
    @SerializedName("status")
    var status: String? = null
}