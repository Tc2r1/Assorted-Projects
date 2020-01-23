package com.dreams.kotlingeofencedemo.services.mapservices

import com.dreams.kotlingeofencedemo.domain.DomainObject
import java.util.*

/**
 * Created by Tc2r on 1/23/2020.
 *
 *
 */
abstract class AbstractMapService {

    protected var domainMap: MutableMap<Int, DomainObject>

    private val nextKey: Int
        get() = if (domainMap.isEmpty()) {
            1
        } else {
            Collections.max(domainMap.keys) + 1
        }

    init {
        domainMap = HashMap()
    }

    fun saveorUpdate(domainObject: DomainObject?): DomainObject {
        if (domainObject != null) {

            if (domainObject.id == null) {
                domainObject.id = nextKey


            }

            domainMap[domainObject.id!!] = domainObject

            return domainObject

        } else {
            throw RuntimeException("Object Can't Be Null")
        }
    }


}
