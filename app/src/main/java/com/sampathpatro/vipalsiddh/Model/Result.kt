package com.sampathpatro.vipalsiddh.Model

class Result {
    var business_status: String? = null
    var formatted_address: String? = null
    var geometry: Geometry? = null
    var icon: String? = null
    var name: String? = null
    var opening_hours: OpeningHours? = null
    var place_id: String? = null
    var plus_code: PlusCode? = null
    var rating: Double = 0.toDouble()
    var reference: String? = null
    var types: List<String>? = null
    var user_ratings_total: Int = 0
    var photos: List<Photo>? = null
    var url: String? = null
    var address_components: Array<AddressComponent>? = null
    var adr_address: String? = null
    var formatted_phone_number: String? = null
    var international_phone_number: String? = null
    var price_level: Int = 0
    var reviews: Array<Review>? = null
    var utc_offset: Int = 0
    var vicinity: String? = null
    var website: String? = null

}