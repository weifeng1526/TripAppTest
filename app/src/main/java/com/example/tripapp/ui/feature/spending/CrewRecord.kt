package com.example.tripapp.ui.feature.spending

import com.google.gson.annotations.SerializedName

data class CrewRecord(

//    @SerializedName("crewNo") val crewNo: Int = 0, // 群組會員明細編號
    @SerializedName("schNo") val schNo: Int = 0, // 行程編號
    @SerializedName("schName") val schName: String = "", // 行程名稱
    @SerializedName("schCur") val schCur: String = "", // 出遊幣別
    @SerializedName("crCur") val crCur: String = "", // 結算幣別
//    @SerializedName("crewName") val crewName: String = "", // 群組名稱
    @SerializedName("memNo") val memNo: Int = 0, // 會員編號
    @SerializedName("memName") val memName: String = "", // 會員暱稱
    @SerializedName("memIcon") val memIcon: String = "" // 會員頭像




//    // 群組會員明細編號
//    private Integer crewNo;
//    // 行程編號
//    private Integer schNo;
//    // 群組名稱
//    private String crewName;
//    // 會員編號
//    private Integer memNo;
//    // 會員暱稱
//    private String memName;
//    // 會員頭像
//    private String memIcon;

)