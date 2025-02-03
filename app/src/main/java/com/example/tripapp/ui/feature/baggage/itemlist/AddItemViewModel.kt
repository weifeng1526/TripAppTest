import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.baggage.BagList
import com.example.tripapp.ui.feature.baggage.Item
import com.google.android.gms.common.util.UidVerifier
import com.ron.restdemo.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddItemViewModel : ViewModel() {

    private val tag = AddItemViewModel::class.java.simpleName


//原本的功能

    // 保存编辑状态
    private val _editingItem = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val editingItem = _editingItem.asStateFlow()

    // 保存编辑文字
    private val _editedText = MutableStateFlow<Map<String, String>>(emptyMap())
    val editedText = _editedText.asStateFlow()

    // 保存物品分組資料
    private val _sections = MutableStateFlow<List<Pair<String, List<Item>>>>(emptyList())
    val sections: StateFlow<List<Pair<String, List<Item>>>> = _sections

    // 保存每個物品的勾選狀態
    private val _checkedState = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val checkedState: StateFlow<Map<Int, Boolean>> = _checkedState

    // 保存每個分類的展開狀態
    private val _expandedStates = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val expandedStates: StateFlow<Map<Int, Boolean>> = _expandedStates


    private var memNo: Int = 0 // 定義 memNo 變數

    fun setMemNo(memNo: Int) {
        this.memNo = memNo
    }

    private var schNo: Int = 0 // 定義 schNo 變數
    fun setSchNo(schNo: Int) {
        this.schNo = schNo
    }

    init {
        fetchData(memNo, schNo) // 初始化時抓取資料
    }

    /**
     * 抓取物品資料，並且更新各項狀態
     */
    fun fetchData(memNo: Int, schNo: Int) {
        viewModelScope.launch {
            val items = GetItems()
            val itemsWithExist = GetItemsIfExist(memNo, schNo) // 獲取包含 EXIST 屬性的數據
            if (items.isEmpty()) {
                Log.e("fetchData", "No items available")
                return@launch
            }

            // 分組物品
            val groupedItems = items.groupBy { it.itemType.toString() }.toList()

            // 更新物品分組資料
            _sections.value = groupedItems.map { (title, items) ->
                val newTitle = when (title) {
                    "0" -> "自訂"
                    "1" -> "衣物"
                    "2" -> "隨身用品"
                    "3" -> "個人用品"
                    "4" -> "洗漱用品"
                    "5" -> "化妝保養品"
                    "6" -> "電子用品"
                    "7" -> "藥品"
                    "8" -> "文件支付類"
                    else -> title
                }
                newTitle to items
            }

            // 設定所有分類為未展開狀態
            _expandedStates.value = groupedItems.indices.associateWith { false }

            // 設定所有物品的勾選狀態
            _checkedState.value = itemsWithExist.associate { item ->
                item.itemNo to item.itemExist // 使用 itemExist 屬性設定勾選狀態
            }
        }
    }

    /**
     * 取得物品資料
     */
    suspend fun GetItems(): List<Item> {
        return try {
            val response = RetrofitInstance.api.GetItems()
            Log.d(tag, "_data: ${response}")
            response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            emptyList()
        }
    }

    suspend fun GetItemsIfExist(memNo: Int, schNo: Int): List<Item> {
        return try {
            val response = RetrofitInstance.api.GetItemsIfExist(memNo, schNo) // 呼叫後端 API 獲取物品是否存在
            Log.d(tag, "_data: ${response}")
            response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            emptyList()
        }
    }

    /**
     * 更新物品的展開狀態
     */
    fun updateExpandedState(itemtype: Int, isExpanded: Boolean) {
        _expandedStates.update { states ->
            states.toMutableMap().apply {
                this[itemtype] = isExpanded
            }
        }
    }

    /**
     * 更新物品的勾選狀態
     */
    fun updateCheckedState(itemNo: Int, isChecked: Boolean) {
        _checkedState.update { it.toMutableMap().apply { this[itemNo] = isChecked } }
    }

    /**
     * 更新物品的增加刪除
     */
    fun updateChangeState(itemNo: Int, isChecked: Boolean) {
        _checkedState.update { it.toMutableMap().apply { this[itemNo] = isChecked } }

        viewModelScope.launch {
            try {

                if (isChecked) {
                    // 當勾選時，新增物品
                    val bagListEntry = BagList(memNo, schNo, itemNo, false)
                    val response = RetrofitInstance.api.AddBagItem(bagListEntry)
                    Log.d("AddItemViewModel", "Item added successfully: $response")
                } else {
                    // 當取消勾選時，刪除物品
                    val response = RetrofitInstance.api.DeleteBagItem(memNo, schNo, itemNo)
                    Log.d("AddItemViewModel", "Item removed successfully: $response")
                }
            } catch (e: Exception) {
                // 異常處理，並記錄錯誤
                Log.e("AddItemViewModel", "Error saving items: ${e.message}")
                // 可以通知 UI 顯示錯誤訊息
                Log.e("AddItemViewModel","Error deleting item: ${e.message}")
            }
        }
    }

    /**
     * 提交選擇的物品到後端
     */
    //    第五點待會處理
//    按下儲存按鈕時，將選中的物品加入 bag_list
    fun saveSelectedItems(memNo: Int, schNo: Int) {
        viewModelScope.launch {
            try {
                // 獲取所有狀態和數據
                val selectedItems = _checkedState.value.filterValues { it }.keys
                val currentItems = RetrofitInstance.api.GetBagItems(memNo, schNo).map { it.itemNo }

                // 確保數據正確後再進行新增/刪除操作
                val itemsToAdd = selectedItems.filterNot { it in currentItems }
                val itemsToDelete = currentItems.filterNot { it in selectedItems }

                // 新增物品
                itemsToAdd.forEach { itemNo ->
                    val bagListEntry = BagList(memNo, schNo, itemNo, false)
                    try {
                        val response = RetrofitInstance.api.AddBagItem(bagListEntry)
                        Log.d("AddItemViewModel", "Item saved: $response")
                    } catch (e: Exception) {
                        Log.e("AddItemViewModel", "Error adding item $itemNo: ${e.message}")
                    }
                }

                // 刪除物品
                itemsToDelete.forEach { itemNo ->
                    try {
                        val response = RetrofitInstance.api.DeleteBagItem(memNo, schNo, itemNo)
                        Log.d("AddItemViewModel", "Item deleted: $response")
                    } catch (e: Exception) {
                        Log.e("AddItemViewModel", "Error deleting item $itemNo: ${e.message}")
                    }
                }

                Log.d("AddItemViewModel", "Items saved successfully.")
            } catch (e: Exception) {
                Log.e("AddItemViewModel", "Error saving items: ${e.message}")
            }
        }
    }


}
//
//    // 更新物品的编辑状态
//    fun updateEditingItem(item: String, isEditing: Boolean) {
//        _editingItem.update { it.toMutableMap().apply { this[item] = isEditing } }
//    }
//
//    // 更新物品的编辑文本
//    fun updateEditedText(item: String, newText: String) {
//        _editedText.update { it.toMutableMap().apply { this[item] = newText } }
//    }
//
//    // 从指定类别删除物品
//    fun removeItemFromSection(sectionIndex: Int, item: String) {
//        val section = _sections.value[sectionIndex]
//        val newItems = section.second.filterNot { ig == item }
//
//        // 使用新的 Pair 替换旧的 Pair
//        _sections.update { currentSections ->
//            currentSections.toMutableList().apply {
//                this[sectionIndex] =
//                    section.copy(second = SnapshotStateList<Item>().apply { addAll(newItems) })
//            }
//        }
//    }
//
//    // 向指定类别添加物品
//    fun addItemToSection(sectionIndex: Int, item: String) {
//        val section = _sections.value[sectionIndex]
//        val newItems = section.second + item
//
//        // 使用新的 Pair 替换旧的 Pair
//        _sections.update { currentSections ->
//            currentSections.toMutableList().apply {
//                this[sectionIndex] =
//                    section.copy(second = SnapshotStateList<Item>().apply { addAll(newItems) })
//            }
//        }
//    }

//}

