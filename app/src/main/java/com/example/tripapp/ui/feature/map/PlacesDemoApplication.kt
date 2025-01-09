package com.example.tripapp.ui.feature.map

import android.app.Application
import android.widget.Toast
import com.example.tripapp.BuildConfig
import com.example.tripapp.R
import com.google.android.libraries.places.api.Places


class PlacesDemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val apiKey = BuildConfig.PLACES_API_KEY
        if (apiKey.isEmpty()) {
            Toast.makeText(this, getString(R.string.app_name), Toast.LENGTH_LONG).show()
            return
        }

        Places.initialize(applicationContext, BuildConfig.PLACES_API_KEY)
    }
}