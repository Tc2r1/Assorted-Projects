package com.dreams.kotlingeofencedemo.controllers

import com.dreams.kotlingeofencedemo.domain.GeoEvent
import com.dreams.kotlingeofencedemo.services.mapservices.GeoEventServiceMapImpl
import org.junit.Assert
import org.junit.Test

/**
 * Created by Tc2r on 1/23/2020.
 *
 *
 * Description:
 */
class GeoEventControllerTest {

    internal lateinit var geoEventServiceMap: GeoEventServiceMapImpl
    internal lateinit var geoEventController: GeoEventController

    @Test
    fun testSaveOrUpdate() {

        geoEventServiceMap = GeoEventServiceMapImpl()
        geoEventController = GeoEventController()
        geoEventController.setGeoEventService(geoEventServiceMap)
        val tempGeoEvent = GeoEvent()

        val id = 1
        val eventTriggered = "Entered"
        val locality = "Oak Brook"
        val eventId = "U5h0u1dh17eM9"
        val timeStamp = "8:00pm"
        val userName = "Dre"

        tempGeoEvent.id = id
        tempGeoEvent.eventTriggered =  eventTriggered
        tempGeoEvent.eventId = eventId
        tempGeoEvent.locality = locality
        tempGeoEvent.timeStamp = timeStamp
        tempGeoEvent.userName = userName

        val actual = geoEventController.saveOrUpdate(tempGeoEvent)

        // expected value is redirect:geoevent/show/1
        val expected = "redirect:geoevent/show/1"
        Assert.assertEquals("Attempt to post/edit event failed", expected, actual)
    }
}