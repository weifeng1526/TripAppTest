package com.example.tripapp.ui.feature.map


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface MapApi{
    //新增行程
    @POST("map/place")
    suspend fun selectPlace(@Body request:SelectPlaceDetail):List<PlaceDetail>
}
object MapRetrofit {
    val api: MapApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/TripAppEnd/") // Base URL
            .addConverterFactory(GsonConverterFactory.create()) // GSON for JSON conversion
            .build()
            .create(MapApi::class.java)
    }
}