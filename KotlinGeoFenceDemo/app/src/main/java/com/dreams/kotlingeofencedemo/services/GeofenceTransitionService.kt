package com.dreams.kotlingeofencedemo.services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.example.testgeofence.MapsActivity
import com.example.testgeofence.R
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

/**
 * Created by Tc2r on 1/19/2020.
 *
 *
 * Description:
 */
class GeofenceTransitionService : IntentService(TAG) {
    override fun onHandleIntent(intent: Intent?) {

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        // Handle Errors
        if (geofencingEvent.hasError()) {
            val errorMessage = getErrorString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }
    }


    companion object {

        private val TAG = GeofenceTransitionService::class.java.simpleName

        // Code Written By N. White

        fun getErrorString(errorCode: Int): String {

            when (errorCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> return "GeoFence not available"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> return "Too Many Geofences"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> return "Too Many Pending Intents"
                else -> return "Unknown Error"
            }
        }
    }
}

