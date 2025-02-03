package com.example.tripapp.ui.feature.shop

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.shop.ShopApiService.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductVM : ViewModel() {
    private val tag = "tag_ProductVM"
    private val _productDetailState = MutableStateFlow(Product())
    val productDetailState: StateFlow<Product> = _productDetailState.asStateFlow()

    fun setDetailProduct(product: Product) {
        _productDetailState.value = product
    }

    // MutableStateFlow用來監控指定資料狀態，當資料一改變即可通知對應畫面更新
    // MutableStateFlow常與ViewModel搭配，可以讓UI元件在生命週期期間作出適當更新
    private val _productsState = MutableStateFlow(emptyList<Product>())
    val productsState: StateFlow<List<Product>> = _productsState.asStateFlow()

    init {
        viewModelScope.launch {
            _productsState.value = fetchProducts()
        }
    }

    /**
     * 載入測試需要資料
     * @return 產品資訊
     */
    private suspend fun fetchProducts(): List<Product> {
        try {
            val products = RetrofitInstance.api.fetchProducts()
            Log.d(tag, "products: $products")
            return products
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return emptyList()
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.addProduct(product)
                if (response.isSuccessful) {
                    // 取得後端回傳的包含 prodNo 的新商品
                    val newProduct = response.body()
                    Log.d("AddProduct", "新增成功: $newProduct")

                    newProduct?.let {
                        // 更新 productsState
                        _productsState.update { currentList ->
                            currentList + it
                        }
                    }
                } else {
                    Log.e("AddProduct", "新增失敗：${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("AddProduct", "異常錯誤：${e.message}")
            }
        }
    }

    // 更新商品資料並且更新商品列表
    suspend fun updateProductDetails(product: Product) {
        try {
            val response = RetrofitInstance.api.updateProduct(product)
            if (response.isSuccessful) {
                val updatedProduct = response.body()
                if (updatedProduct != null) {
                    _productDetailState.value = updatedProduct
                    Log.d("ProductVM", "資料更新成功: $updatedProduct")
                    // 更新商品列表
                    _productsState.update { currentList ->
                        currentList.map {
                            if (it.prodNo == updatedProduct.prodNo) updatedProduct else it
                        }
                    }
                    // 這裡添加日誌來檢查更新後的商品列表
                    Log.d("ProductVM", "更新後的商品列表：${_productsState.value}")
                } else {
                    Log.e("ProductVM", "更新失敗，沒有返回商品資料")
                }
            } else {
                Log.e(
                    "ProductVM",
                    "更新失敗，錯誤碼: ${response.code()}, 錯誤訊息: ${response.message()}"
                )
            }
        } catch (e: Exception) {
            Log.e("ProductVM", "更新時發生錯誤: ${e.message}")
        }
    }

    fun deleteProduct(prodNo: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteProduct(prodNo)
                if (response.isSuccessful) {
                    Log.d("ProductVM", "刪除成功")
                } else {
                    Log.e("ProductVM", "刪除失敗：${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ProductVM", "刪除商品時發生錯誤: ${e.message}")
            }
        }
    }

    // 更新商品列表
    suspend fun updateProductList() {
        try {
            // 向後端請求商品資料
            val products = ShopApiService.RetrofitInstance.api.fetchProducts()
            _productsState.value = products
        } catch (e: Exception) {
            Log.e("ProductVM", "更新商品列表失敗: ${e.message}")
        }
    }
}
