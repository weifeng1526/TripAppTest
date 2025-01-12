package com.example.tripapp.ui.feature.spending

import com.google.gson.annotations.SerializedName

data class SpendingRecord(
    @SerializedName("costNo") val costNo: Int = 0,  // 消費紀錄編號
    @SerializedName("schNo") val schNo: Int = 0, // 行程編號
    @SerializedName("schName") val scName: String = "", // 行程名稱
    @SerializedName("costType") val costType: Int = 0, // 消費類別
    @SerializedName("costItem") val costItem: String = "", // 消費項目
    @SerializedName("costPrice") val costPrice: Double = 0.0, // 消費金額
    @SerializedName("paidBy") val paidBy: Int = 0, // 付款人：會員編號
    @SerializedName("paidByName") val paidByName: String = "", // 會員名稱
    @SerializedName("crCostTime") val crCostTime: String = "", // 消費時間
    @SerializedName("costPex") val costPex: Boolean = false, // 公費支出
    @SerializedName("crCurRecord") val crCurRecord: String = "", // 紀錄幣別
    @SerializedName("schCur") val schCur: String = "", // 出遊幣別
    @SerializedName("crCur") val crCur: String = "", // 結算幣別

//    val payByUserName: String = "", // 會員名稱
//    val className: String = "", // 類別 (例如: 交通、餐飲)
//    val itemName: String = "", // 項目名稱 (例如: 高鐵車票)
//    val dateTime: String = "", // 日期
//    val totalAmount: Double = 0.00, // 總花費金額
//    val perPersonAmount: Double = 0.00, // 每人均分金額
//    val numberOfPeople: Int = 0, // 分攤人數
//    val currency: String = "JPY" // 貨幣單位，預設為日圓

)



data class PostSpendingRecord(
    //可以考慮預設值的問題，使用者如果沒有輸入會自動帶入這件事情是對的嗎？
//    @SerializedName("costNo") val costNo: Int = 0,  // 消費紀錄編號
    @SerializedName("schNo") val schNo: Int = 0, // 行程編號
//    @SerializedName("schName") val scName: String = "", // 行程名稱
    @SerializedName("costType") val costType: Int = 0, // 消費類別
    @SerializedName("costItem") val costItem: String = "", // 消費項目
    @SerializedName("costPrice") val costPrice: Double = 0.0, // 消費金額
    @SerializedName("paidByNo") val paidByNo: Int = 0, // 付款人：會員編號
    @SerializedName("paidByName") val paidByName: String = "", // 會員名稱
    @SerializedName("crCostTime") val crCostTime: String = "", // 消費時間
//    @SerializedName("costPex") val costPex: Boolean = false, // 公費支出
    @SerializedName("crCur") val crCur: String = "", // 紀錄幣別
    @SerializedName("crCurRecord") val crCurRecord: String = "", // 紀錄幣別

//    @SerializedName("crCur") val crCur: String = "", // 結算幣別
)




