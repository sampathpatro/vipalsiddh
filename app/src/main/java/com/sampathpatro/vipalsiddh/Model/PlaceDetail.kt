package com.sampathpatro.vipalsiddh.Model

import com.google.gson.annotations.SerializedName

class PlaceDetail {

    @SerializedName("html_attributions")
    var htmlAttributions: Array<String>? = null
    var result: Result? = null
    var status: String? = null
    
}