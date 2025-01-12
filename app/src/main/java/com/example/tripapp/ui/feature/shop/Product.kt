package com.example.tripapp.ui.feature.shop

import com.example.tripapp.R

data class Product(
    var prodNo: Int = 0,
    var prodName: String = "",
    var prodDesc: String = "",
    var prodPrice: Int = 0,
    var prodSta: Boolean = false,
//    var prodPic: ByteArray = byteArrayOf()
//    var prodPic: Int = R.drawable.ic_launcher_foreground,
    var prodPic: String = ""
) {

    override fun equals(other: Any?): Boolean {
        return this.prodName == (other as Product).prodName
    }

    override fun hashCode(): Int {
        return prodName.hashCode()
    }
}
