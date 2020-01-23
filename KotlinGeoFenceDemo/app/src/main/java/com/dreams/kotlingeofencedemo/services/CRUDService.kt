package com.dreams.kotlingeofencedemo.services

/**
 * Created by Tc2r on 1/23/2020.
 */
interface CRUDService<T> {

    fun saveOrUpdate(domainObject: T): T
}
