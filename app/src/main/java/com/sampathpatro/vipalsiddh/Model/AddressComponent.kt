package com.sampathpatro.vipalsiddh.Model

import com.google.gson.annotations.SerializedName

class AddressComponent {

    @SerializedName("long_name")
    var longName: String? = null

    @SerializedName("short_name")
    var shortName: String? = null

    var types: List<String>? = null

}
