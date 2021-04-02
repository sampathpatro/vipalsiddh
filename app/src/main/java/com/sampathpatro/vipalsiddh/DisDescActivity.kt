package com.sampathpatro.vipalsiddh

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.sampathpatro.vipalsiddh.MapsActivity
import com.sampathpatro.vipalsiddh.utils.LocationUtils
import kotlinx.android.synthetic.main.activity_dis_desc.*

open class DisDescActivity : AppCompatActivity() {

    private var latitude = 0.toDouble()
    private var longitude = 0.toDouble()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private lateinit var locationManager: LocationManager
    private var gps_enabled = false

    private lateinit var lastLocation: Location

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dis_desc)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        val sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putBoolean("AppUsed", true)
        editor.apply()

        if (gps_enabled){
            mapBtn.setText(R.string.look_for_hospitals)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (LocationUtils.checkLocationPermission(this)) {
                    buildLocationRequest()
                    locationRequest = LocationUtils.buildLocationCallback()

                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest, locationCallback,
                        Looper.myLooper()
                    )
                }

            } else {
                buildLocationRequest()
                locationRequest = LocationUtils.buildLocationCallback()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback,
                    Looper.myLooper()
                )
            }
        }



        val listOfPotDiseases = intent.getStringArrayListExtra("DISSYM")
        val disName = intent.getStringExtra("DISNAME")
        val disSer = intent.getIntExtra("DISSER", 1)
        val disSpr = intent.getIntExtra("DISSPR", 1)

        val disDesc = intent.getStringExtra("DISDES")

        potDisSubTitle.text = "Here's what we know about $disName"

        val potDisSymList = findViewById<TextView>(R.id.potDisSymList)

        mapBtn.setOnClickListener{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!gps_enabled){
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.location_request)
                builder.setIcon(R.drawable.ic_logo)
                builder.setMessage("Location services are required to show hospitals nearby.")
                builder.setPositiveButton(R.string.enable_location){dialogInterface, which ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                builder.setNegativeButton(R.string.cancel){dialogInterface, which ->
                    Toast.makeText(baseContext, "Location permission not allowed.", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this, DiseaseActivity::class.java))
                }
                val alertDialog = builder.create()
                alertDialog.show()
            } else {
                val intent = Intent (this, MapsActivity::class.java)
                intent.putExtra("LAT", latitude)
                intent.putExtra("LNG", longitude)
                intent.putExtra("DISSER", disSer)
                startActivity(intent)
            }
        }

        potDisName.text = disName

        potDisDesc.text = disDesc

        if (listOfPotDiseases != null) {
            val list = StringBuilder()
            for (dis in listOfPotDiseases){
                when {
                    listOfPotDiseases.indexOf(dis) == 0 -> {
                        list.append(dis)
                    }
                    listOfPotDiseases.indexOf(dis) == listOfPotDiseases.size-1 -> {
                        list.append(", and $dis")
                    }
                    else -> {
                        list.append(", $dis")
                    }
                }
            }

            val symptoms = list.toString()
            potDisSymList.text = symptoms
        }

        val sympList = StringBuilder()
        for (symp in chosenSymptomsList){
            when {
                chosenSymptomsList.indexOf(symp) == 0 -> {
                    sympList.append(symp)
                }
                chosenSymptomsList.indexOf(symp) == chosenSymptomsList.size-1 -> {
                    sympList.append(", and $symp")
                }
                else -> {
                    sympList.append(", $symp")
                }
            }
        }

        if (sympList.isNotEmpty()){
            your_symptoms.text = "Your Symptoms: ${sympList.toString()}"
        } else {
            your_symptoms.visibility = View.GONE
        }

        when(disSer){

            1 -> pot_imgSeriousness.setImageResource(R.drawable.safe_indic_15)
            2 -> pot_imgSeriousness.setImageResource(R.drawable.safe_indic_25)
            3 -> pot_imgSeriousness.setImageResource(R.drawable.safe_indic_35)
            4 -> pot_imgSeriousness.setImageResource(R.drawable.safe_indic_45)
            5 -> pot_imgSeriousness.setImageResource(R.drawable.safe_indic)

        }

        when (disSpr){
            1 -> {
                pot_txtSpread.text = "Very Rare"
            }
            2 -> {
                pot_txtSpread.text = "Fairly Rare"
            }
            3 -> {
                pot_txtSpread.text = "Rare"
            }
            4 -> {
                pot_txtSpread.text = "Common"
            }
            5 -> {
                pot_txtSpread.text = "Very Common"
            }
        }

    }

    override fun onResume() {
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gps_enabled){
            mapBtn.setText(R.string.look_for_hospitals)
        }
        super.onResume()
    }

    private fun buildLocationRequest() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                lastLocation = p0!!.locations[p0.locations.size - 1]

                latitude = lastLocation.latitude
                longitude = lastLocation.longitude
            }
        }
    }

}