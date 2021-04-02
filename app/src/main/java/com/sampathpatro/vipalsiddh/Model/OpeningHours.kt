package com.sampathpatro.vipalsiddh.Model

import com.google.gson.annotations.SerializedName

class OpeningHours {
    var open_now: Boolean = false
    var periods: List<Period>? = null
    @SerializedName("weekday_text")
    var weekdayText: List<String>? = null
}
