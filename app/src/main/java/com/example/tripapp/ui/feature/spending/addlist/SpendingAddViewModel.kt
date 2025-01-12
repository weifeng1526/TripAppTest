package com.example.tripapp.ui.feature.spending.addlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.spending.PostSpendingRecord
import com.ron.restdemo.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class CurrencyEx(
    val option: String, //選單顯示--中文
    val text: String    //輸入欄位顯示--英文
)

class SpendingAddViewModel() : ViewModel() {
    val TAG = "TAG---SpendingAddViewModel---"


    // 輸入金額
    private var _moneyInput = MutableStateFlow("")
    val moneyInput = _moneyInput.asStateFlow()

    //輸入幣別
    private var _inputCurrent = MutableStateFlow("JPY")
    val inputCurrent = _inputCurrent.asStateFlow()

    //幣別選擇（選取狀態）
    private var _ccySelected = MutableStateFlow("日幣")
    val ccySelected = _ccySelected.asStateFlow()

    //幣別選擇（選項）
    private var _ccyOptions = MutableStateFlow<Map<String, String>>(mapOf())
    val ccyOptions = _ccyOptions.asStateFlow()
//    private val _ccyOptions = MutableStateFlow<Map<String, String>>(emptyMap())
//    val ccyOptions = _ccyOptions.asStateFlow()
//    private val _ccyOptions = MutableLiveData<Map<String, String>>()
//    val ccyOptions: LiveData<Map<String, String>> = _ccyOptions


    private val currencyMap = mapOf(
        "日幣" to "JPY",
        "台幣" to "TWD",
        // ... 未來可以新增其他貨幣
    )


    //付款人（選取狀態）
    private var _payBySelect = MutableStateFlow("")
    val payBySelect = _payBySelect.asStateFlow()

    //付款人--旅伴（選項）
    private val _payByOptions = MutableStateFlow<Map<String, Int>>(emptyMap())
    val payByOptions = _payByOptions.asStateFlow()

    //人數
    private var _countCrew = MutableStateFlow(0)
    val countCrew = _countCrew.asStateFlow()

    //消費名稱
    private var _itemName = MutableStateFlow("")
    val itemName = _itemName.asStateFlow()

    //消費時間
    private var _costTime = MutableStateFlow("")
    val costTime = _costTime.asStateFlow()


    //類別名稱
    private var _selectedClassname = MutableStateFlow("")
    val selectedClassname = _selectedClassname.asStateFlow()

//    //類別圖片
//    private var _selectedClassimg = MutableStateFlow("")
//    val selectedClassimg = _selectedClassimg.asStateFlow()

    //選取人數
    private var _chmember = MutableStateFlow<Map<String, Boolean>>(mapOf())
    val chmember = _chmember.asStateFlow()

    //Switch
    private var _swSplit = MutableStateFlow(true)
    val swSplit = _swSplit.asStateFlow()

    init {

    }


    fun updateＭoneyInput(newText: String) {
        _moneyInput.update { newText }
    }

    fun updateInputCurrent(newText: String) {
        _inputCurrent.update { newText }
    }

    fun updateccySelected(newText: String) {
        _ccySelected.value = newText
    }

    fun updatePayBySelect(newText: String) {
        _payBySelect.value = newText
        //select: List<Pair<String,Int>>
    }

    fun updateItemName(newText: String) {
        _itemName.update { newText }
    }

    fun updateCostTime(newCostTime: String) {
        _costTime.value = newCostTime
    }

    fun updateSelectedClassname(newText: String) {
        _selectedClassname.update { newText }
    }

    fun updateonAllCheckedChanged(isChecked: Boolean) {
        val newList = _chmember.value.map { it.key to isChecked }.toMap()
        _chmember.update { newList }
    }

    fun updateOnMemberChecked(name: String, isChecked: Boolean) {
        val newList = _chmember.value.toMutableMap()
        newList[name] = isChecked
        _chmember.update { newList }
    }

    suspend fun saveOneTripsSpending(
        // 使用者輸入的資料，跟UI對接
        schNo: Int, // 行程編號
        costType: Int, // 消費類別
        costItem: String, // 消費項目
        costPrice: Double, // 消費金額
        paidByNo:Int,
        paidByName:String,
        crCostTime: String, // 消費時間
        crCur:String,
        crCurRecord: String, // 紀錄幣別


    ) {
        val response = RetrofitInstance.api.saveOneTripsSpending(
            // 我要傳給後端的資料
            PostSpendingRecord(
                schNo = schNo,
                costType = costType,
                costItem = costItem,
                costPrice = costPrice,
                paidByNo = paidByNo,
                paidByName = paidByName,
                crCostTime = crCostTime,
                crCur = crCur,
                crCurRecord = crCurRecord,


            )
        )
        return response
    }

    //打API
    //fetchInitData function 就是一個很好的例子，它包含了兩個協程 (coroutine)。
    //第一個協程： 呼叫 findTripCrew API，取得行程相關的資訊。
    //第二個協程： 更新貨幣選項，這個動作可能涉及查詢資料庫或其他 API，但在此範例中，直接在程式碼中硬編碼了貨幣選項。
    fun tripCrew(schNo: Int) {
        // 取得旅伴資料
        viewModelScope.launch {
            /*try{
                val response = RetrofitInstance.api.findTripCrew(schNo)
                Log.d(TAG, "fetchInitData: ${response}")
            } catch(e: Exception){
                Log.d(TAG, "fetchInitData Exception: ${e}")
            }*/
            val response = RetrofitInstance.api.findTripCrew(schNo) ?: emptyList()
            Log.d(TAG, "test旅伴名字: ${response}")
            _payByOptions.value = response.associate {
                it.memName to it.memNo
            }

            _chmember.value = response.associate {
                it.memName to true
            }

//            val countCrew = response.size
            _countCrew.update { response.size }

//            Log.d(TAG, "fetchInitData: ${response}")

//            Log.d(TAG, "aaa:$memberInfo ")
            // 把資料塞回到對應的畫面資料欄位
            // 打新增時，要記得帶行程的參數上去

//            response.forEach {
//                it.crewName
//            }
        }

        // 取得幣別資料
        viewModelScope.launch {
            val response = RetrofitInstance.api.findTripCur(schNo)
            Log.d(TAG, "test幣別: ${response}")

            // 資料庫因為同樣的產生資料過多，直接用DISTINCT過濾掉，所以資料結構多一層，變成陣列包物件，所以才需要使用response[0]
            val schCur = response[0].schCur // 取得出遊幣別
            val crCur = response[0].crCur  // 取得結算幣別
            Log.d(TAG, "schCur: $schCur")
            Log.d(TAG, "crCur: $crCur")

            _ccySelected.update { schCur }

            // 根據取到的資料轉換為中文
            val schNoEx = currencyMap[schCur] ?: schCur
            val crCurEx = currencyMap[crCur] ?: crCur
            Log.d(TAG, "schCur: $schNoEx")
            Log.d(TAG, "crCur: $crCurEx")

//            _ccyOptions.update{ response.mapOf(it.memName to it.memNo) }


            _ccyOptions.value = mapOf(
                schCur to schNoEx,
                crCur to crCurEx
            )

        }


//        viewModelScope.launch {
//
//            // Money
//            _ccyOptions.update {
//                mapOf(
//                    "日幣" to "JPY",
//                    "台幣" to "TWD"
//                )
//            }
//        }

    }

    fun fetchFindOneTripsSpending(costNo: Int) {
        // 要把 Response 塞回到畫面的 StateFlow
    }


//fun updateSwitch(){
//
//        swSplit = it
//        Log.d(TAG, "swSplit:${swSplit} ")
//        if (swSplit == true) {
//            txSplitMethod = "（全選）"
//            chmember.value.forEach { name, isChecked ->
//                chmember.value = chmember.value.toMutableMap().apply {
//                    this[name] = true
//                    Log.d(TAG, "chmember--if:${name}/${this[name]} ")
//
//                }
//            }
//
//        } else {
//            txSplitMethod = "（取消）"
//
//            chmember.value.forEach { name, isChecked ->
//                chmember.value = chmember.value.toMutableMap().apply {
//                    this[name] = false
//                    Log.d(TAG, "chmember--else:${name}/${this[name]} ")
//                }
//            }
//        }
//
//}


    //假資料
//    private val _chep: MutableStateFlow<Map<String, Boolean>> = MutableStateFlow(mapOf())
//    val chep = _chep.asStateFlow()
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