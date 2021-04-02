package com.sampathpatro.vipalsiddh

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.sampathpatro.vipalsiddh.Common.Common
import com.sampathpatro.vipalsiddh.Model.MyPlaces
import com.sampathpatro.vipalsiddh.utils.LocationUtils
import kotlinx.android.synthetic.main.activity_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var latitude = 0.toDouble()
    private var longitude = 0.toDouble()

    private var currentPlaces: MyPlaces? = null

    private var disSer: Int = 0

    val handler = Handler()

    private lateinit var lastLocation: Location
    private var mMarker: Marker? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
        private val GOOGLE_MAP_API = "AIzaSyAE5gpo_mXW1KhxpszXpR9beC22BUAwues"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val intent = intent
        val LAT = intent.getDoubleExtra("LAT", 0.toDouble())
        val LNG = intent.getDoubleExtra("LNG", 0.toDouble())
        disSer = intent.getIntExtra("DISSER", 0)
        if (LAT != 0.toDouble() && LNG != 0.toDouble()) {
            progress_circular.visibility = View.GONE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (LocationUtils.checkLocationPermission(this)) {
                buildLocationRequest(LAT, LNG)
                locationRequest = LocationUtils.buildLocationCallback()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback,
                    Looper.myLooper()
                )
            }

        } else {
            buildLocationRequest(LAT, LNG)
            locationRequest = LocationUtils.buildLocationCallback()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback,
                Looper.myLooper()
            )
        }

        mapSearchBtn.setOnClickListener {
            var noLocation = 0
            if (latitude != 0.toDouble() && longitude != 0.toDouble()){
                nearbyPlaces()
            } else if (noLocation > 5) {
                Toast.makeText(this, "If the app is unable to find your location, try restarting the page, or the app.", Toast.LENGTH_LONG).show()
            }
            else {
                noLocation += 1
                Toast.makeText(this, "Please wait for your location to load.", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun nearbyPlaces() {
        mMap.clear()
        val latLng = "$latitude,$longitude"
        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mService = retrofit.create(iGoogleAPIService::class.java)

        val mCall = mService.getNearbyPlaces("hospital", latLng, 5000, GOOGLE_MAP_API)
        mCall.enqueue(object : Callback<MyPlaces> {
            override fun onResponse(call: Call<MyPlaces>, response: Response<MyPlaces>) {

                currentPlaces = response.body()

                if (response.isSuccessful) {
                    for (i in response.body()!!.results!!.indices)
                    {
                        val googlePlace = response.body()!!.results!![i]
                        val markerOptions = MarkerOptions()
                        val lat = googlePlace.geometry!!.location!!.lat
                        val lng = googlePlace.geometry!!.location!!.lng
                        val latLng = LatLng(lat, lng)
                        val name = googlePlace.name
                        markerOptions.title(name)
                            .icon(
                                bitmapDescriptor(baseContext, R.drawable.ic_nav_icon)
                            )
                            .position(latLng)
                            .snippet(i.toString())
                        mMap.addMarker(markerOptions)
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(13f))
                    }
                } else {
                    Toast.makeText(baseContext, response.code(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MyPlaces>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun buildLocationRequest(LAT: Double, LNG: Double) {
        if (LAT == 0.toDouble() && LNG == 0.toDouble()) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult?) {
                    lastLocation = p0!!.locations[p0.locations.size - 1]
                    if (mMarker != null) {
                        mMarker!!.remove()
                    }
                    latitude = lastLocation.latitude
                    longitude = lastLocation.longitude

                    val latLng = LatLng(latitude, longitude)
                    val markerOptions = MarkerOptions()
                        .position(latLng)
                        .title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    mMarker = mMap.addMarker(markerOptions)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
                }
            }
        } else {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult?) {
                    if (mMarker != null) {
                        mMarker!!.remove()
                    }
                    latitude = LAT
                    longitude = LNG

                    val latLng = LatLng(latitude, longitude)
                    val markerOptions = MarkerOptions()
                        .position(latLng)
                        .title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    mMarker = mMap.addMarker(markerOptions)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
                }
            }
        }
        progress_circular.visibility = View.GONE
    }

    fun bitmapDescriptor(
        context: Context,
        @DrawableRes vectorDrawableResourceID: Int
    ): BitmapDescriptor {
        var drawable = ContextCompat.getDrawable(context, vectorDrawableResourceID)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable!!)).mutate()
        }
        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888)
        val canvas = Canvas (bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission
                        .ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true
            }
        } else {
            mMap.isMyLocationEnabled = true
        }
        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.setOnMarkerClickListener { marker ->

            if (Integer.parseInt(marker.snippet) != 0){
                Common.currentResult = currentPlaces!!.results!![Integer.parseInt(marker.snippet)]
                startActivity(Intent(this@MapsActivity, ViewPlace::class.java))
            } else {
                Toast.makeText(baseContext, Integer.parseInt(marker.snippet).toString(), Toast.LENGTH_SHORT).show()
            }
            true
        }

    }

}