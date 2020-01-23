package com.dreams.kotlingeofencedemo.domain

import androidx.annotation.IdRes

/**
 * Created by Tc2r on 1/23/2020.
 *
 *
 */
class GeoEvent : DomainObject {

    @IdRes
    override var id: Int? = null

    var userName: String? = null
    var eventTriggered: String? = null
    var eventId: String? = null
    var locality: String? = null
    var timeStamp: String? = null
}
