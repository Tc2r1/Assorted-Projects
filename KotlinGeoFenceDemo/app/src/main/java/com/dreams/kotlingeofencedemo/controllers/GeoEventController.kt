package com.dreams.kotlingeofencedemo.controllers

import com.dreams.kotlingeofencedemo.domain.GeoEvent
import com.dreams.kotlingeofencedemo.services.mapservices.GeoEventServiceMapImpl


/**
 * Created by Tc2r on 1/23/2020.
 *
 */
class GeoEventController {

    private var geoEventService: GeoEventServiceMapImpl? = null

    fun setGeoEventService(geoEventService: GeoEventServiceMapImpl) {
        this.geoEventService = geoEventService
    }

    fun saveOrUpdate(geoEvent: GeoEvent): String {

        val newGeoEvent = geoEventService!!.saveOrUpdate(geoEvent)
        return "redirect:geoevent/show/" + newGeoEvent.id!!
    }
}
