package com.example.tripapp.ui.feature.shop

import com.example.tripapp.R

data class Product(
    var prodNo: Int = 0,
    var prodName: String = "",
    var prodDesc: String = "",
    var prodPrice: Int = 0,
    var prodSta: Boolean = false,
    var prodPic: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as Product
        return prodNo == other.prodNo &&
                prodName == other.prodName &&
                prodDesc == other.prodDesc &&
                prodPrice == other.prodPrice &&
                prodSta == other.prodSta &&
                prodPic == other.prodPic
    }

    override fun hashCode(): Int {
        return 31 * prodNo.hashCode() +
                prodName.hashCode() +
                prodDesc.hashCode() +
                prodPrice.hashCode() +
                prodSta.hashCode() +
                prodPic.hashCode()
    }
}

//    override fun equals(other: Any?): Boolean {
//        return this.prodName == (other as Product).prodName
//    }
//
//    override fun hashCode(): Int {
//        return prodName.hashCode()
//    }
//}
