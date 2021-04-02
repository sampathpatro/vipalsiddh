package com.sampathpatro.vipalsiddh.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationRequest
import com.sampathpatro.vipalsiddh.MainActivity

object LocationUtils {
    fun checkLocationPermission(context: Activity): Boolean {
        return if (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission
                    .ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context, android.Manifest
                        .permission.ACCESS_FINE_LOCATION
                )
            )
                ActivityCompat.requestPermissions(
                    context, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), MainActivity.PERMISSION_REQUEST_CODE
                )
            else
                ActivityCompat.requestPermissions(
                    context, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), MainActivity.PERMISSION_REQUEST_CODE
                )
            false
        } else {
            true
        }
    }

    fun buildLocationCallback(): LocationRequest {
        return LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 3000
            smallestDisplacement = 10f
        }
    }

}