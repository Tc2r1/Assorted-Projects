package com.dreams.kotlingeofencedemo.services

import android.app.IntentService
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.example.testgeofence.MapsActivity
import com.example.testgeofence.R
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import java.util.*

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

        val geoFenceTransition = geofencingEvent.geofenceTransition

        // Check if the transition type is of interest.
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Get the geofence that was triggered
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            val geofenceTransitionDetails = getGeofenceTransitionDetails(geoFenceTransition,
                triggeringGeofences
            )

            // Send notification details as a String.
            TODO( "Send Notification")
        }
    }

    // Gets the geofenceTransitionDetails from list of triggering events.
    private fun getGeofenceTransitionDetails(geoFenceTransition: Int,
                                             triggeringGeofences: List<Geofence>): String
    {
        // get the ID of each geofence Triggered
        val triggeringGeofencesList = ArrayList<String>()
        for (geofence in triggeringGeofences) {
            triggeringGeofencesList.add(geofence.requestId)
        }

        var status: String? = null
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            status = "Entering"
        } else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            status = "Exiting"
        }

        return status!! + TextUtils.join(", ", triggeringGeofencesList)
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

