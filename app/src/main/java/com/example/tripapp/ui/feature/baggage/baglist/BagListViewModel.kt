package com.example.tripapp.ui.feature.baggage.baglist

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

// 行程 ViewModel
class TripViewModel : ViewModel() {
    val trips = mutableStateListOf("Trip 1", "Trip 2", "Trip 3")
    val selectedTrip = mutableStateOf<String?>(null)

    // 選擇行程
    fun selectTrip(trip: String) {
        selectedTrip.value = trip
    }
}

// 物品 ViewModel
class ItemViewModel : ViewModel() {
    val items = mutableStateListOf<String>()

    // 更新物品清單
    fun updateItemsForTrip(trip: String) {
        items.clear()
        when (trip) {
            "Trip 1" -> items.addAll(listOf("Item A", "Item B", "Item C"))
            "Trip 2" -> items.addAll(listOf("Item D", "Item E", "Item F"))
            "Trip 3" -> items.addAll(listOf("Item G", "Item H", "Item I"))
            else -> items.addAll(listOf("Default Item 1", "Default Item 2"))
        }
    }
}

// 主 ViewModel
class BagViewModel : ViewModel() {
    val tripViewModel = TripViewModel()
    val itemViewModel = ItemViewModel()

    init {
        // 監聽 TripViewModel 的選擇變化，並通知 ItemViewModel 更新數據
        tripViewModel.selectedTrip.value?.let {
            onTripSelected(it)
        }
    }

    fun onTripSelected(trip: String) {
        // 當選擇一個行程時，更新物品清單
        tripViewModel.selectTrip(trip)
        itemViewModel.updateItemsForTrip(trip)
    }
}



////假資料
//// 假設這是從資料庫取得的資料
//class TripViewModel : ViewModel() {
//
//    // 模擬的行程數據
//    val trips = mutableStateListOf("trip 1", "trip 2", "trip 3", "trip 4", "trip 5", "trip 6", "trip 7", "trip 8")
//
//    // 可擴展為從後端或數據庫讀取數據
//}
//
//
//class ItemViewModel : ViewModel() {
//    // 模擬的物品清單數據
//    val items = mutableStateListOf<String>().apply {
//        addAll((1..30).map { "Item $it" })
//    }
//
//    // 可擴展為從後端或數據庫讀取數據
//}