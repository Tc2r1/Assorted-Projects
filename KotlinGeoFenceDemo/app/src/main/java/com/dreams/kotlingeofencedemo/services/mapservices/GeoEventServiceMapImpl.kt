package com.dreams.kotlingeofencedemo.services.mapservices

import com.dreams.kotlingeofencedemo.domain.GeoEvent
import com.dreams.kotlingeofencedemo.services.GeoEventService

/**
 * Created by Tc2r on 1/23/2020.
 */
class GeoEventServiceMapImpl : AbstractMapService(), GeoEventService {

    override fun saveOrUpdate(domainObject: GeoEvent): GeoEvent {
        return super.saveorUpdate(domainObject) as GeoEvent
    }
}
