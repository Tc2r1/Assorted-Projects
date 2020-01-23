package com.dreams.kotlingeofencedemo.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dreams.kotlingeofencedemo.MapsActivity
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
            sendNotification(geofenceTransitionDetails)
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

    private fun sendNotification(message: String) {
        Log.wtf(TAG, "sendNotification: $message")

        // Intent to start the maps Activity.
        val notificationIntent = MapsActivity
            .makeNotificationIntent(applicationContext, message)

        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(MapsActivity::class.java)
        stackBuilder.addNextIntent(notificationIntent)
        val notoficationPendingIntent = stackBuilder.getPendingIntent(0,
            PendingIntent.FLAG_UPDATE_CURRENT)

        // Creating and sending Notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val CHANNEL_ID = "GeoFence_01"
        val name = "Hire Me Channel"
        val Description = "Brandy, you're a fine girl!"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = Description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mChannel.setShowBadge(false)
            notificationManager.createNotificationChannel(mChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(this,
            CHANNEL_ID)

        TODO("notify Manager. Create a notification")
        //notificationManager.notify(GEOFENCE_NOTIFICATION_ID,)

    }


    companion object {
        // Code Written By N. White

        private val TAG = GeofenceTransitionService::class.java.simpleName

        val GEOFENCE_NOTIFICATION_ID = 10

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

