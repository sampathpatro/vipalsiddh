package com.sampathpatro.vipalsiddh

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sampathpatro.vipalsiddh.Common.Common
import com.sampathpatro.vipalsiddh.Model.PlaceDetail
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_place.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViewPlace : AppCompatActivity() {

    internal lateinit var mService: iGoogleAPIService
    var mPlace: PlaceDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_place)

        val sharedPreferencesDark = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
        val darkMode = sharedPreferencesDark.getBoolean("NightMode", false)

        if(darkMode){
            imgGoogle.setImageResource(R.drawable.powered_by_google_on_black)
        }

        place_name.text = ""
        place_address.text = ""
        open_close.text = ""

        btn_show_map.setOnClickListener {
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mPlace!!.result!!.url))
            val chooser = Intent.createChooser(mapIntent, "Choose an Application")
            if (mapIntent.resolveActivity(packageManager) != null){
                startActivity(chooser)
            } else {
                Toast.makeText(this, "No compatible application found.", Toast.LENGTH_SHORT).show()
            }
        }

        if (Common.currentResult!!.photos != null && Common.currentResult!!.photos!!.isNotEmpty()) {
            Picasso.with(this)
                .load(getPhotoOfPlace(Common.currentResult!!.photos!![0].photo_reference!!, 1000))
                .into(place_image)
        }

        rating_bar.rating = Common.currentResult!!.rating.toFloat()

        if (Common.currentResult!!.opening_hours != null) {
            if (Common.currentResult!!.opening_hours!!.open_now){
                open_close.text = getString(R.string.open)
                open_close.setTextColor(ContextCompat.getColor(this, R.color.txtOpen))
            } else {
                open_close.text = getString(R.string.close)
                open_close.setTextColor(ContextCompat.getColor(this, R.color.txtClose))
            }
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mService = retrofit.create(iGoogleAPIService::class.java)
        val mCall = mService.getDetailPlace(getPlaceDetailUrl(Common.currentResult!!.place_id!!))
        mCall.enqueue(object : Callback<PlaceDetail> {
            override fun onResponse(call: Call<PlaceDetail>, response: Response<PlaceDetail>) {
                mPlace = response!!.body()

                place_address.text = mPlace!!.result!!.formatted_address
                place_name.text = mPlace!!.result!!.name
            }

            override fun onFailure(call: Call<PlaceDetail>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun getPlaceDetailUrl(placeId: String): String {
        val url = StringBuilder("https://maps.googleapis.com/maps/api/place/details/json")
        url.append("?place_id=$placeId")
        url.append("&key=AIzaSyAE5gpo_mXW1KhxpszXpR9beC22BUAwues")
        return url.toString()
    }

    private fun getPhotoOfPlace(photoReference: String, maxwidth: Int): String {
        val url = StringBuilder("https://maps.googleapis.com/maps/api/place/photo")
        url.append("?maxwidth=$maxwidth")
        url.append("&photoreference=$photoReference")
        url.append("&key=AIzaSyAE5gpo_mXW1KhxpszXpR9beC22BUAwues")
        return url.toString()
    }
}