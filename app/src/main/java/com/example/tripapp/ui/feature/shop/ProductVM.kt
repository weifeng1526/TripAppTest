package com.example.tripapp.ui.feature.shop

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.shop.ShopApiService.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductVM : ViewModel() {
    private val tag = "tag_ProductVM"
    private val _productDetailState = MutableStateFlow(Product())
    val productDetailState: StateFlow<Product> = _productDetailState.asStateFlow()
    fun setDetailProduct(product: Product) {
        _productDetailState.value = product
//        _productDetailState.updateAndGet {
//            product
//        }
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

//    init {
//        _productsState.update { fetchProducts() }
//    }
//}

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
}

//    suspend fun productbox(
//        prodName: String,
//        prodPrice: Int,
//        byteArray: ByteArray?
//    ): Product {
//        // 要上傳圖文，包裝成form data格式
//        val prodNamePart = prodName.toRequestBody("text/plain".toMediaTypeOrNull())
//        val prodPricePart = prodPrice.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//        val picRequestBody =
//            byteArray?.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)
//        val picPart =
//            picRequestBody?.let { MultipartBody.Part.createFormData("image", "upload.jpg", it) }
//        try {
//            val response = RetrofitInstance.api.productbox(prodNamePart, prodPricePart, picPart)
//            return response.isSuccessful
//        } catch (e: Exception) {
//            Log.e(android.R.attr.tag, "error: ${e.message}")
//            return false
//        }
//    }
//}



//        return listOf(
//            Product(
//                "0001",
//                "冬戀北海道",
//                3888.0,
//                R.drawable.aaa,
//                longDescription = "\n" + "*東京河津櫻花祭+熱海梅園梅花祭\n" +
//                        "*伊東溫泉區(兩晚)+富士五湖溫泉區(乙晚)\n" +
//                        "*十國峠軌道纜車+伊豆之國全景纜車(碧露台)"
//                 ),
//            Product("0002", "東京河津櫻花", 6888.0, R.drawable.bbb),
//        )
//    }
