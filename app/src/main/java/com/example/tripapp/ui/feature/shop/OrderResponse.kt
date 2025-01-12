package com.example.tripapp.ui.feature.shop

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("order") val order: Order // Order物件
)
