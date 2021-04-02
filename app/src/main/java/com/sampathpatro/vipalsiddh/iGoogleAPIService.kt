package com.sampathpatro.vipalsiddh

import com.sampathpatro.vipalsiddh.Model.MyPlaces
import com.sampathpatro.vipalsiddh.Model.PlaceDetail
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface iGoogleAPIService {

    @GET("maps/api/place/textsearch/json")
    fun getNearbyPlaces(@Query("query") query: String, @Query("location") latLng: String, @Query("radius") radius: Int,
                        @Query("key") key: String): Call<MyPlaces>

    @GET
    fun getDetailPlace(@Url url: String): Call<PlaceDetail>

}