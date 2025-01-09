package com.example.tripapp.ui.feature.trip.dataObjects

import java.math.BigDecimal

//一筆景點的格式
data class Poi(
    var poiNo: Int = 0,               // 景點編號
    var poiAdd: String = "",           // 景點地址
    var poiName: String = "",          // 景點名稱
    var poiLng: BigDecimal = BigDecimal("0.0"),  // 經度
    var poiLat: BigDecimal = BigDecimal("0.0"),  // 緯度
    var poiLab: String = "",           // 景點標籤
    var poiPic: String = "",           // 景點圖片路徑
    var poiRat: Double = 0.0,          // 評分
    var poiHno: String = "",           // 景點電話
    var poiPhon: String = "",          // 景點手機
    var poiBs: String = "",    // 開店時間
    var poiNbs: String = "",   // 關店時間
    var poiBd: String = "",            // 景點描述
    var poiLike: Int = 0               // 收藏數量
)
