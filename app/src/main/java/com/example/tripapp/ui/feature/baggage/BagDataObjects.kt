package com.example.tripapp.ui.feature.baggage

data class BagList(
    var memNo: Int = 0,
    var schNo: Int = 0,
    var itemNo: Int = 0,
    var ready:Boolean = false,
)

data class Item(
    var itemNo: Int = 0,
    var itemName: String = "",
    var itemType: Int = 0
)
