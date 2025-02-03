package com.example.tripapp.ui.feature.spending

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.member.MemberRepository
import com.example.tripapp.ui.feature.trip.dataObjects.Destination
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.ron.restdemo.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpendingRecordVM : ViewModel() {
    val TAG = "TAG---SpendingRecordVM---"
    private val tag = SpendingRecordVM::class.java.simpleName

    //初始化 listof() 空的list:
    private val _plan = MutableStateFlow<List<Plan>>(listOf())
    val plan = _plan.asStateFlow()

    // 金額加總
    private val _totalSumStatus = MutableStateFlow<List<TotalSum>?>(listOf())
    val totalSumStatus = _totalSumStatus.asStateFlow()


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


    //用行程算總金額
    // 顯示特定行程的消費明細
    private var _totalCost = MutableStateFlow(0)
    val totalCost = _totalCost.asStateFlow()

    //平均金額
    private val _averageCost = MutableStateFlow(0)
    val averageCost = _averageCost.asStateFlow()

    val memberNum = MemberRepository.getUid()



//    變數VM寫法
//    private val _title = MutableStateFlow<String?>(null)
//    val title = _title.asStateFlow()

    fun onAddSpendingClick() {
        val tabIndex = tabsTripListSelectedIndex.value
        val schNo = _spendingListInfo.value.getOrNull(tabIndex)?.first

    }

    init {
        viewModelScope.launch {
            //要改會員編號
            val spending = getSpendingList(memberNum)
            Log.d(TAG, "spendingAAAAA" + spending)
            // 用 schNo 分類，變成是 Pair<SchNo,消費明細>，才能區別每個 Tab 代表的 schNo
            val topicSpending = spending.groupBy { it.schNo }.toList()
            Log.d(TAG, "topicSpendingSize" + topicSpending.size)
            _spendingListInfo.value = topicSpending
            // 分類完之後，將第一個列表當作預設顯示的資料
            _tabsTripListSelectedList.update { topicSpending.firstOrNull() }


            //加總算錢
            val spendingData = topicSpending.flatMap { (schNo, price) ->
                price.map { spending ->
                    Pair(schNo, spending.costPrice)
                }
            }
//            val schNotest = spendingData.first()
//            val spendingBySchNo = spendingData.groupBy { it.first }
            val totalCost = spendingData
                .filter { it.first == 1 }
                .sumOf { it.second }
            Log.d(TAG, "spendingData: $totalCost")

            _totalCost.update { totalCost.toInt() }


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

    /** 取得所有資料 */
    suspend fun getSpendingList(memNo: Int): List<SpendingRecord> {
        try {
            val response = RetrofitInstance.api.getSpendingList(memNo)
            Log.d(tag, "getSpendingList data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "getSpendingList error: ${e.message}")
            return listOf()
        }
    }

//    /** 取得某筆資料 */
//    suspend fun getOneSpendingList(costNo: Int):List<SpendingRecord>{
//        try {
//            val response = RetrofitInstance.api.getOneSpendingList(costNo)
//            Log.d(tag, "data: ${response}")
//            return response
//        }catch (e: Exception){
//            Log.e(tag, "error: ${e.message}")
//            return  listOf()
//        }
//    }


    /** 新增一筆資料 */
//    suspend fun addSpendingList(costNo: Int):List<SpendingRecord>{
//        try {
//            val response = RetrofitInstance.api.getOneSpendingList(costNo)
//            Log.d(tag, "data: ${response}")
//            return response
//        }catch (e: Exception){
//            Log.e(tag, "error: ${e.message}")
//            return  listOf()
//        }
//    }


    // 點 tab 的反應，才能知道是哪個 Tab 亮起，跟要換哪個行程跟消費明細
    fun onTabChanged(changeIndex: Int) {
        _tabsTripListSelectedIndex.update { changeIndex }
        val selectedSchNo = spendingListInfo.value.getOrNull(changeIndex)
        Log.d(TAG, "spendingListInfo: $spendingListInfo")
        _tabsTripListSelectedList.update { selectedSchNo }
    }


    fun tripCrew(schNo: Int) {
        viewModelScope.launch {
            // 取得此行程的所有參與者
            val response = RetrofitInstance.api.findTripCrew(schNo) ?: emptyList()
            Log.d(TAG, "test旅伴名字: ${response}")

            // 此行程的參與者人數
            val peopleCount = response.size

            // 此行程的所有花費
            // 例如： 100 + 100 + 200 = 500
            val totalCost: Int =
                (_tabsTripListSelectedList.value?.second?.sumOf { it.costPrice })?.toInt() ?: 0
            _totalCost.update { totalCost }

            // 此行程的平均花費
            // 例如： 500 / 2 = 250
            val average = totalCost / peopleCount
            _averageCost.update { average }


            // 此行程依照人名，將所有消費分組
            // 例如： A: {100,100,100}, B: {200}
            val data = _tabsTripListSelectedList.value?.second?.groupBy { it.paidByName }


            // 依照人名，將所有消費加總
            // 例如： A: 300, B: 200
            val result = data?.map { it.key to it.value.sumOf { it.costPrice } }


            // 依照人名，將各自消費減去平均花費，等於此人應付或應收
            // 例如： A: 300 - 250 = 50, B: 200 - 250 = -50
            val totalSum =
                response.map { crewRecord ->
                    crewRecord.memName to ((result?.find { it.first == crewRecord.memName }?.second
                        ?: 0).toInt() - average)
                }

            // 只是將上面的結果轉成，要呈現在UI的格式
            val totalSumUiState =
                totalSum.map { TotalSum(userName = it.first, totalSum = it.second.toString()) }
            _totalSumStatus.update { totalSumUiState }


        }

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