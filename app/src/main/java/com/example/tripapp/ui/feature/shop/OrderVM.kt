package com.example.tripapp.ui.feature.shop

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class OrderVM : ViewModel() {
    private val tag = "tag_OrderVM"
    private val _ordersState = MutableStateFlow(emptyList<Order>())
    val ordersState: StateFlow<List<Order>> = _ordersState.asStateFlow()

    private var orderCounter = 1

    fun addOrder(
        memNo: Int,
        prodNo: Int,
        prodName: String,
        prodPrice: Double,
        cardNo: String,
        expDate: String,
        cvv: String,
        isSubmitted: Boolean = false
    ) {
        val order = Order(
            ordNo = orderCounter,  // 使用當前的訂單編號
            memNo = memNo,
            prodNo = prodNo,
            prodName = prodName,
            prodPrice = prodPrice,
            ordDt = LocalDateTime.now(),  // 使用當前時間作為訂單時間
            cardNo = cardNo,
            expDate = expDate,
            cvv = cvv,
            isSubmitted = isSubmitted
        )
        _ordersState.update { currentOrders ->
            currentOrders + order
        }
        orderCounter++
    }

    fun removeOrder(ordNo: Int) {
        _ordersState.update { currentOrders ->
            currentOrders.filter { it.ordNo != ordNo }
        }
    }

    fun markAllOrdersSubmitted() {
        viewModelScope.launch {
            saveOrdersToDatabase(_ordersState.value)
            _ordersState.update { orders ->
                orders.map { it.copy(isSubmitted = true) }
            }
        }
    }


    fun submitOrderToDatabase(order: OrderRequest) {
        viewModelScope.launch {
            try {
                val response = ShopApiService.RetrofitInstance.api.addOrder(order)
                if (response.isSuccessful) {
                    Log.d("OrderSubmit", "訂單提交成功")
                } else {
                    Log.e("OrderSubmit", "訂單提交失敗: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("OrderSubmit", "提交時發生錯誤: ${e.message}")
            }
        }
    }

    // 模擬儲存訂單到資料庫的函式
    private suspend fun saveOrdersToDatabase(orders: List<Order>) =
        withContext(Dispatchers.IO) {
            orders.forEach { order ->
                println("儲存訂單 ${order.ordNo} 到資料庫") // 模擬耗時操作
                Thread.sleep(500) // 模擬延遲（實際情況中應替換為資料庫操作）
            }
        }
}