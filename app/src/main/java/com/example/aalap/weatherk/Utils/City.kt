package com.example.aalap.weatherk.Utils

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class City(@PrimaryKey
                var id: String = "",
                var latitude: Double = 0.0,
                var longitude: Double = 0.0,
                var name: String = "") : RealmObject() {


    override fun toString(): String {
        return "City(id='$id', latitude=$latitude, longitude=$longitude, name='$name')"
    }
}