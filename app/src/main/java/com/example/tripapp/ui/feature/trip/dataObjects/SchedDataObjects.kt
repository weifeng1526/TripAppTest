package com.example.tripapp.ui.feature.trip.dataObjects

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



//一筆行程表的格式
data class Plan(
    var schNo: Int = 0,
    var memNo: Int = 0,
    var schState: Int = 0,
    var schName: String = "",
    var schCon: String = "",
    var schStart: String = "",
    var schEnd: String = "",
    var schCur: String = "",
    var schPic: ByteArray = ByteArray(0),
    var schLastEdit: String = ""
)

fun getCurrentTimeAsString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val currentTime = Date()
    return sdf.format(currentTime)
}

//刪除成功的回傳
data class DeletePlanResponse(var isDelete: Boolean)

//一筆形成明細的格式
data class Destination(
    var dstNo: Int = 0,        // 景點編號
    var schNo: Int = 0,        // 行程編號
    var poiNo: Int = 0,        // 地點編號
    var dstName: String = "",  // 景點名稱
    var dstAddr: String = "",  // 景點地址
    var dstPic: ByteArray? = null,  // 景點圖片
    var dstDep: String = "",   // 描述
    var dstDate: String = "",  // 日期
    var dstStart: String = "", // 開始時間
    var dstEnd: String = "",   // 結束時間
    var dstInr: String = ""    // 間隔時間
)

