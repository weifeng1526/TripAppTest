package com.example.tripapp.ui.feature.spending

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.ron.restdemo.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpendingRecordVM : ViewModel() {
    //
    private val tag = SpendingRecordVM::class.java.simpleName

    //初始化 listof() 空的list:
    private val _plan = MutableStateFlow<List<Plan>>(listOf())
    val plan = _plan.asStateFlow()



    // Pair<schNo, List<SpendingRecord>>
    // 從網路上抓下來的全部分類明細，用 SchNo 分類後的結果
    private var _spendingListInfo =
        MutableStateFlow<List<Pair<Int, List<SpendingRecord>>>>(listOf())
    val spendingListInfo = _spendingListInfo.asStateFlow()

    // 被選中的 Tab Index
    private var _tabsTripListSelectedIndex = MutableStateFlow(0)
    val tabsTripListSelectedIndex = _tabsTripListSelectedIndex.asStateFlow()

    // 顯示特定行程的消費明細
    private var _tabsTripListSelectedList = MutableStateFlow<Pair<Int, List<SpendingRecord>>?>(null)
    val tabTripListSelectedList = _tabsTripListSelectedList.asStateFlow()

//    變數VM寫法
//    private val _title = MutableStateFlow<String?>(null)
//    val title = _title.asStateFlow()

    fun onAddSpendingClick() {
        val tabIndex = tabsTripListSelectedIndex.value
        val schNo = _spendingListInfo.value.getOrNull(tabIndex)?.first

    }

    init {
        viewModelScope.launch {
            val spending = getSpendingList()
            // 用 schNo 分類，變成是 Pair<SchNo,消費明細>，才能區別每個 Tab 代表的 schNo
            val topicSpending = spending.groupBy { it.schNo }.toList()
            Log.d("topicSpending", "size" + topicSpending.size)
            _spendingListInfo.value = topicSpending
            // 分類完之後，將第一個列表當作預設顯示的資料
            _tabsTripListSelectedList.update { topicSpending.firstOrNull() }
        }
    }

    //初始化方法，裡面就會去getPlans()
    fun initPlan() {
        //
        viewModelScope.launch {
//            val plan = getPlans()
//            _plan.update { plan }
            _plan.update { getPlans() }
        }
    }

    //  getPlans()：打 API
    suspend fun getPlans(): List<Plan> {
        try {
            val response = RetrofitInstance.api.GetPlans()
            Log.d(tag, "getPlans data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "getPlans error: ${e.message}")
            return listOf()
        }
    }


    suspend fun getSpendingList(): List<SpendingRecord> {

        try {
            val response = RetrofitInstance.api.getSpendingList()
            Log.d(tag, "getSpendingList data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "getSpendingList error: ${e.message}")
            return listOf()
        }
    }

    // 點 tab 的反應，才能知道是哪個 Tab 亮起，跟要換哪個行程跟消費明細
    fun onTabChanged(changeIndex: Int) {
        _tabsTripListSelectedIndex.update { changeIndex }
        val selectedSchNo = _spendingListInfo.value.getOrNull(changeIndex)
        _tabsTripListSelectedList.update { selectedSchNo }
    }









//可以參考彬華老師的檔案來寫。
//帳務清單全部拿回來，然後分組在前端處理用filter處理。

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








}