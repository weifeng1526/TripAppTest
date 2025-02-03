import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.spending.CrewRecord
import com.example.tripapp.ui.feature.spending.SpendingRecord
import com.ron.restdemo.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpendingListViewModel : ViewModel() {

    private val TAG = SpendingListViewModel::class.java.simpleName

    // 顯示結算列表
    private val _settleExpanded = MutableStateFlow(false)
    val settleExpanded = _settleExpanded.asStateFlow()


    // 2 定義屬性
    // 找到一筆資料
    private var _spendingOneListInfo =
        MutableStateFlow<SpendingRecord?>(SpendingRecord())
    val spendingOneListInfo = _spendingOneListInfo.asStateFlow()

    // 找到行程編號跟名字
    private val _tripName = MutableStateFlow<List<CrewRecord>?>(listOf())
    val tripName = _tripName.asStateFlow()

    //付款人--旅伴（選項）
    private val _payByOptions = MutableStateFlow<Map<String, Int>>(emptyMap())
    val payByOptions = _payByOptions.asStateFlow()

//    init {
//        viewModelScope.launch {
//            _tripName.update { findTripName() }
//        }
//    }


    //3 根據清單編號取得單筆資料
    fun GetData(costNo: Int) {
        viewModelScope.launch {
            _spendingOneListInfo.update { getOne(costNo) }
        }
    }

    //3 根據會員編號找到旅行名稱
    fun getTripName(memNo: Int) {
        viewModelScope.launch {
            _tripName.update { findTripName(memNo) }
        }
    }

    fun setSettleExpanded(expanded: Boolean) {
        _settleExpanded.value = expanded
    }


//    fun getsettleExpanded(){
//   _settleExpanded.value = !_settleExpanded.value
//    }


    /** 取得一筆Plan */
//suspend fun getOne(costNo: Int): List<SpendingRecord>? {
//    val tag = "tag_OneSpendingListViewModel"
//    try {
//        val response = RetrofitInstance.api.getOneSpendingList(costNo)
//        Log.d(tag, "data: ${response}")
//        return response
//    } catch (e: Exception) {
//        Log.e(tag, "error: ${e.message}")
//        return null
//    }
//}


//取得單筆消費明細
    suspend fun getOne(costNo: Int): SpendingRecord? {
        try {
            val response = RetrofitInstance.api.getOneSpendingList(costNo)
            Log.d("tagaaa", "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e("tagaaa", "error: ${e.message}")
            return null
        }
    }


    suspend fun GetPlan(memNo: Int): SpendingRecord? {
        try {
            val response = RetrofitInstance.api.getOneSpendingList(memNo)
            Log.d("tagaaa", "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e("tagaaa", "error: ${e.message}")
            return null
        }
    }

    suspend fun findTripName(memNo: Int): List<CrewRecord>? {
        try {
            Log.d("TAGCCC", "有沒有進來")
            val response = RetrofitInstance.api.findTripName(memNo)
            Log.d("TAGCCC", "findTripName:$response ")
            return response
        } catch (e: Exception) {
            Log.d("TAGCCC", "Exception:$e ")
            return null
        }
    }

}


//package com.example.tripapp.ui.feature.spending.list
//
//import androidx.lifecycle.ViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
//class SpendingListViewModel : ViewModel() {
//    private val _spendingListInfo = MutableStateFlow(spendingListInfo())
//    val spendingListInfo = _spendingListInfo.asStateFlow()
//
//    init {
//        _spendingListInfo.value = spendingListInfo()
//    }
//
//
//    private fun spendingListInfo(): List<SpendingRecord> {
//        return listOf(
//            SpendingRecord(
//                payByUserName = "小明",
//                className = "餐飲",
//                itemName = "拉麵",
//                dateTime = "2023-11-22 19:30",
//                totalAmount = 800.0,
//                perPersonAmount = 800.0,
//                numberOfPeople = 1
//            ),
//            SpendingRecord(
//                payByUserName = "小美",
//                className = "交通",
//                itemName = "地鐵一日券",
//                dateTime = "2023-11-23 09:00",
//                totalAmount = 800.0,
//                perPersonAmount = 800.0,
//                numberOfPeople = 1
//            ),
//            SpendingRecord(
//                payByUserName = "小美",
//                className = "票券",
//                itemName = "演唱會門票",
//                dateTime = "2023-11-24 18:00",
//                totalAmount = 5000.0,
//                perPersonAmount = 2500.0,
//                numberOfPeople = 2
//            ),
//            SpendingRecord(
//                payByUserName = "小美",
//                className = "住宿",
//                itemName = "飯店住宿費",
//                dateTime = "2023-11-25 14:00",
//                totalAmount = 3000.0,
//                perPersonAmount = 1500.0,
//                numberOfPeople = 2
//            ),
//            SpendingRecord(
//                payByUserName = "胖虎",
//                className = "購物",
//                itemName = "運動用品",
//                dateTime = "2023-11-26 11:00",
//                totalAmount = 2000.0,
//                perPersonAmount = 2000.0,
//                numberOfPeople = 1
//            ),
//            SpendingRecord(
//                payByUserName = "小明",
//                className = "娛樂",
//                itemName = "遊樂園門票",
//                dateTime = "2023-11-27 13:00",
//                totalAmount = 4000.0,
//                perPersonAmount = 2000.0,
//                numberOfPeople = 2
//            ),
//            SpendingRecord(
//                payByUserName = "小明",
//                className = "餐飲",
//                itemName = "咖啡",
//                dateTime = "2023-11-28 16:00",
//                totalAmount = 300.0,
//                perPersonAmount = 300.0,
//                numberOfPeople = 1
//            ),
//            SpendingRecord(
//                payByUserName = "大雄",
//                className = "交通",
//                itemName = "計程車費",
//                dateTime = "2023-11-29 20:00",
//                totalAmount = 500.0,
//                perPersonAmount = 500.0,
//                numberOfPeople = 1
//            ),
//            SpendingRecord(
//                payByUserName = "大雄",
//                className = "其他",
//                itemName = "雜費",
//                dateTime = "2023-11-30 12:00",
//                totalAmount = 200.0,
//                perPersonAmount = 200.0,
//                numberOfPeople = 1
//            ),
//            SpendingRecord(
//                payByUserName = "靜香",
//                className = "購物",
//                itemName = "書籍",
//                dateTime = "2023-12-01 15:00",
//                totalAmount = 1000.0,
//                perPersonAmount = 1000.0,
//                numberOfPeople = 1
//            )
//        )
//
//    }
//
//
//}