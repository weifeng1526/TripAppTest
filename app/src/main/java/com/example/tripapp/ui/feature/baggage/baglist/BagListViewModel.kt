package com.example.tripapp.ui.feature.baggage.baglist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.baggage.BagItems
import com.ron.restdemo.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 行程資料類，包含行程編號 schNo
data class Trip(
    val schName: String,
    val schStart: String,
    val schEnd: String,
    val schNo: Int,
    val memNo: Int
)

class TripViewModel : ViewModel() {
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips

    private val _userTrips = MutableStateFlow<List<Trip>>(emptyList())
    val userTrips: StateFlow<List<Trip>> = _userTrips

    private val _selectedTrip = MutableStateFlow<Trip?>(null)
    val selectedTrip: StateFlow<Trip?> get() = _selectedTrip

    // 當行程被選擇時，透過回調通知變更
    fun selectTrip(schNo: Int) {
        _selectedTrip.value = _trips.value.find { it.schNo == schNo }
    }

    // 初始化行程資料
    fun updateTrips(newTrips: List<Trip>) {
        _trips.value = newTrips
        selectFirstTrip() // 預設選擇第一個行程
    }

    private fun selectFirstTrip() {
        _selectedTrip.value = _trips.value.firstOrNull()
    }
}


class ItemViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<BagItems>>(emptyList())
    val items: StateFlow<List<BagItems>> = _items

    // 保存每個物品的勾選狀態
    private val _checkedState = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val checkedState: StateFlow<Map<Int, Boolean>> = _checkedState

    // 更新物品清單
    fun updateItemsForSelectedTrip(memNo: Int, schNo: Int) {
        viewModelScope.launch {
            val bagItems = RetrofitInstance.api.GetBagItemsBySchNo(schNo)
            _items.value = bagItems.map { bagItem ->
                BagItems(memNo, schNo, bagItem.itemNo, bagItem.itemName, bagItem.ready)
            }
            initializeCheckedState()
        }
    }

    // 初始化勾選狀態
    private fun initializeCheckedState() {
        _checkedState.value = _items.value.associate { it.itemNo to it.ready }
    }

    // 切換勾選狀態
    fun updateCheckedState(itemNo: Int, isChecked: Boolean) {
        _checkedState.value = _checkedState.value.toMutableMap().apply {
            this[itemNo] = isChecked
        }
    }

    // 刪除項目
    fun removeItem(itemNo: Int) {
        _items.value = _items.value.filterNot { it.itemNo == itemNo }
        _checkedState.value = _checkedState.value.toMutableMap().apply {
            remove(itemNo) // 同時移除其選中狀態
        }
    }
}


class BagViewModel : ViewModel() {
    private val tripViewModel = TripViewModel()
    private val itemViewModel = ItemViewModel()
    private val _selectedTrip = MutableStateFlow<Trip?>(null)

    val trips: StateFlow<List<Trip>> = tripViewModel.trips
    val selectedTrip: StateFlow<Trip?> get() = _selectedTrip
    val items: StateFlow<List<BagItems>> = itemViewModel.items
    val checkedState: StateFlow<Map<Int, Boolean>> = itemViewModel.checkedState

    val _isNeedDefaultSelected: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isNeedDefaultSelected = _isNeedDefaultSelected.asStateFlow()

    init {
        fetchTrips()
    }

    fun onDefaultSelected(memNo: Int, schNo: Int) {
        _isNeedDefaultSelected.update { false }
        onTripSelected(memNo, schNo)
    }

    // 當用戶點擊行程時，更新行程和物品清單
    fun onTripSelected(memNo: Int, schNo: Int) {
        viewModelScope.launch {
            val trip = trips.value.find { it.schNo == schNo }
            if (trip != null) {
                _selectedTrip.value = trip
                Log.d("BagViewModel", "Trip selected: $trip")
                // 傳遞 schNo 更新物品
                itemViewModel.updateItemsForSelectedTrip(memNo, schNo)
            } else {
                Log.d("BagViewModel", "Trip with schNo $schNo not found")
            }
        }
    }

    // Fetch trips from API
    private fun fetchTrips() {
        Log.d("bagListViewModel", "fetchTrips")
        viewModelScope.launch {
            try {
                val plans = RetrofitInstance.api.GetPlans()
                val tripList = plans.map {
                    Trip(it.schName, it.schStart, it.schEnd, it.schNo, it.memNo)
                }
                tripViewModel.updateTrips(tripList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    // 切換勾選狀態
    fun updateCheckedState(itemNo: Int, isChecked: Boolean) {
        itemViewModel.updateCheckedState(itemNo, isChecked)
    }

    fun removeItem(itemNo: Int) {
        itemViewModel.removeItem(itemNo)
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