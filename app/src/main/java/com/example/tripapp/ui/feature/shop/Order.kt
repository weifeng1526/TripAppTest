package com.example.tripapp.ui.feature.shop

import java.time.LocalDateTime

data class Order(
    val ordNo: Int = 0,                // 訂單編號，預設為 0
    val memNo: Int = 0,                  // 會員編號，預設為 0
    val prodNo: Int = 0,                 // 產品編號，預設為 0
    val prodName: String = "",           // 產品名稱，預設為空字串
    val prodPrice: Double = 0.0,         // 訂單價格，預設為 0.0
    val ordDt: LocalDateTime = LocalDateTime.now(), // 訂購日期，預設為當前時間
    val cardNo: String = "",             // 信用卡號，預設為空字串
    val expDate: String = "",            // 信用卡到期日，預設為空字串
    val cvv: String = "",                // 信用卡認證碼，預設為空字串
    val isSubmitted: Boolean = false     // 訂單是否提交，預設為 false
)

data class OrderRequest(
    val memNo: Int,
    val prodNo: Int,
    val prodName: String,
    val prodPrice: Double,
    val cardNo: String,
    val expDate: String,
    val cvv: String
)