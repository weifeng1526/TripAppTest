package com.example.tripapp.ui.feature.map

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place

data class PlaceSearch(

    val id:String,



)

data class SelectPlace(
    val displayName:String?=null,
    val formattedAddress:String?=null,
    val location:LatLng?=null,
    val type:String?=null,
    val photoMataData:List<PhotoMetadata?> = emptyList(),
)

