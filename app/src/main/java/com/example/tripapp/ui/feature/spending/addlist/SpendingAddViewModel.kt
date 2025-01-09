package com.example.tripapp.ui.feature.spending.addlist

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SpendingAddViewModel() : ViewModel() {

    private val _chep: MutableStateFlow<Map<String, Boolean>> = MutableStateFlow(mapOf())
    val chep = _chep.asStateFlow()


    //VM 拿到後端傳來的資料，前端跟後端交易的區域，接表格最新的值。
//
//    private val _costRecdEditState = MutableStateFlow(CostRecd())
//    //負責傳出去
//    val costRecdEditState = _costRecdEditState.asStateFlow()
//    //
//    fun setCostRecdEditState(costRecd: CostRecd) {
//        _costRecdEditState.value = costRecd
//    }
//
//    private val _costRecdsState = MutableStateFlow(emptyList<CostRecd>())
//    val costRecdsState: StateFlow<List<CostRecd>> = _costRecdsState.asStateFlow()
//
//    //每一次後端來資料的時候就要做這件事情。
//    init {
//        _costRecdsState.update { CostRecds() }
////        _costRecdsState.value  = CostRecds()
//    }

//    private fun CostRecds(): List<CostRecd> {
//        return listOf(
//// 大阪城
//            CostRecd(1, 123, 456, 1, "大阪城門票", 1600.0, 1, "2023-11-22 14:30", "2023-11-22 15:00", "大阪城", 0.0, "JPY"),
//            CostRecd(2, 123, 456, 2, "大阪城天守閣紀念品", 800.0, 2, "2023-11-22 15:00", "2023-11-22 15:30", "大阪城", 0.0, "JPY"),
//
//            // 道頓堀
//            CostRecd(3, 123, 458, 1, "道頓堀章魚燒", 500.0, 1, "2023-11-22 18:00", "2023-11-22 18:30", "道頓堀", 0.0, "JPY"),
//            CostRecd(4, 123, 458, 2, "金龍拉麵", 850.0, 2, "2023-11-22 19:00", "2023-11-22 19:30", "道頓堀", 0.0, "JPY"),
//
//            // 環球影城
//            CostRecd(5, 123, 460, 3, "環球影城門票", 8200.0, 1, "2023-11-23 10:00", "2023-11-23 10:30", "環球影城", 0.0, "JPY"),
//            CostRecd(6, 123, 460, 5, "哈利波特魔法世界紀念品", 2500.0, 2, "2023-11-23 16:00", "2023-11-23 16:30", "環球影城", 0.0, "JPY"),
//
//            // 心齋橋
//            CostRecd(7, 123, 462, 4, "心齋橋藥妝", 3000.0, 1, "2023-11-24 15:00", "2023-11-24 15:30", "心齋橋", 0.0, "JPY"),
//            CostRecd(8, 123, 462, 4, "服飾", 5000.0, 2, "2023-11-24 16:00", "2023-11-24 16:30", "心齋橋", 0.0, "JPY"),
//
//            // 黑門市場
//            CostRecd(9, 123, 464, 5, "黑門市場海鮮丼", 2500.0, 1, "2023-11-25 12:00", "2023-11-25 12:30", "黑門市場", 0.0, "JPY"),
//            CostRecd(10, 123, 464, 5, "水果", 1000.0, 2, "2023-11-25 13:00", "2023-11-25 13:30", "黑門市場", 0.0, "JPY")
//
//
//        )
}