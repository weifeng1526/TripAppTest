package com.example.tripapp.ui.feature.map

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.util.Locale

class MapViewModel : ViewModel() {
    private val tag = "tag_MapVM"

    var placesClient: PlacesClient? = null

    private val _isLoading = MutableStateFlow(false)
    var isLoading = _isLoading.asStateFlow()

    private val _search = MutableStateFlow("")
    var search = _search.asStateFlow()

    private val _checkSearch = MutableStateFlow<String?>(null)
    var checkSearch = _checkSearch.asStateFlow()

    private val _placeList = MutableStateFlow<List<Place>>(emptyList())
    var placeList = _placeList.asStateFlow()

    private val _tripPlaceList = MutableStateFlow<List<PlaceSearch>>(emptyList())
    var tripPlaceList = _tripPlaceList.asStateFlow()

    private val _selectedTripPlace = MutableStateFlow<SelectPlace?>(null)
    var selectedTripPlace = _selectedTripPlace.asStateFlow()

    //提示吐司
    private val _toastRequest = MutableStateFlow<String?>(null)
    var toastRequest = _toastRequest.asStateFlow()

    //照片相關
    private val _selectedTripPlaceImage = MutableStateFlow<Uri?>(null)
    val selectedTripPlaceImage = _selectedTripPlaceImage.asStateFlow()
    private val _selectedTripPlaceByte = MutableStateFlow<ByteArray?>(null)
    val selectedTripPlaceByte = _selectedTripPlaceByte.asStateFlow()
//    private val _selectedTripPlaceBit = MutableStateFlow<Bitmap?>(null)
//    val selectedTripPlaceBit = _selectedTripPlaceBit.asStateFlow()

    fun onSearchChange(search: String) {
        _search.update { search }

    }


    fun initClient(context: Context) {
        placesClient = Places.createClient(context)
    }


    fun getPlaces(search: String = "朴子當歸鴨") {

        val placeFields = listOf(
            Place.Field.ID,
        )
        // Use the builder to create a SearchByTextRequest object.
        val searchByTextRequest = SearchByTextRequest.builder(search, placeFields)
            .setMaxResultCount(1)
            .setLocationRestriction(
                RectangularBounds.newInstance(
                    LatLng(21.9, 119.5),
                    LatLng(45.4, 145.7)
                )
            ).build()
        placesClient?.searchByText(searchByTextRequest)
            ?.addOnSuccessListener { response ->
                _placeList.update { response.places }
                val place = response.places.map { PlaceSearch(it.id.toString()) }
                _tripPlaceList.update { place }
                if (_tripPlaceList.value.isNotEmpty()) {
                    val placeId = placeList.value.getOrNull(0)?.id ?: return@addOnSuccessListener
                    fetchPlaceDetail(placeId = placeId)
                }
            }
    }

    fun fetchPlaceDetail(placeId: String) {
        //當重新搜尋會清除舊的圖
        _selectedTripPlaceImage.update { null }

        val locale = Locale("zh", "TW")
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.locale = locale

        // Specify the fields to return.
        val placeFields = listOf(
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION,
            Place.Field.TYPES,
            Place.Field.PHOTO_METADATAS
        )

// Construct a request object, passing the place ID and fields array.
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient?.fetchPlace(request)
            ?.addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                if (place.types != null && place.displayName != null && place.formattedAddress != null && place.location != null && place.types != null && place.photoMetadatas != emptyList<PhotoMetadata?>()) {
                    getPhoto(place.photoMetadatas)

                    _selectedTripPlace.update {
                        SelectPlace(
                            place.displayName,
                            place.formattedAddress,
                            place.location,
                            place.types.toString(),
                            place.photoMetadatas
                        )
                    }
                } else {
                    _toastRequest.update { "無此資料" }
                }

            }?.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    Log.e("TAG", "Place not found: ${exception.message}")
                    val statusCode = exception.statusCode
                }
            }

    }

    fun addPlace(
        schNo: Int = 0,               //跟行程拿編號
        poiAdd: String = "",           // 景點地址
        poiName: String = "",          // 景點名稱
        poiLng: BigDecimal = BigDecimal("0.0"),  // 經度
        poiLat: BigDecimal = BigDecimal("0.0"),  // 緯度
        poiLab: String = "",           // 景點標籤
        poiPic: String = "",           // 景點圖片路徑
        poiLike: Int = 1,
        dstDate: String = "",
        dstStart: String = "00:00:00",
        dstEnd: String = "00:00:00",
        dstInr: String = "00:00:00",
        dstPic: ByteArray,

        ) {


        viewModelScope.launch {
            try {
                _isLoading.update { true }
                var response = MapRetrofit.api.selectPlace(
                    SelectPlaceDetail(
                        schNo = schNo,
                        poiName = poiName,
                        poiAdd = poiAdd,
                        poiLat = poiLat,
                        poiLng = poiLng,
                        poiLab = poiLab,
                        poiPic = poiPic,
                        poiLike = poiLike,
                        dstDate = dstDate,
                        dstStart = dstStart,
                        dstEnd = dstEnd,
                        dstInr = dstInr,
                        dstPic = dstPic
                    )
                )
                _isLoading.update { false }
                Log.d(
                    tag,
                    "地點${poiName},地址${poiAdd},經緯度${poiLng},${poiLat},行程時間${dstDate},${dstStart},${dstEnd},${dstInr}"
                )
                if (response != null) {
                    _checkSearch.update { "已經加入了" }
                }
            } catch (e: Exception) {
                Log.e(tag, "error: ${e.message}")

            }

        }
    }

    fun getPhoto(photoMetadatas: List<PhotoMetadata?>) {

// 取得地點物件（此範例使用 fetchPlace()，但您也可以使用 findCurrentPlace()）


        val photoMetadata = photoMetadatas[0]

        // 取得歸屬文字和作者歸屬。


        // 建立 FetchResolvedPhotoUriRequest。
        if (photoMetadata != null) {
            val photoRequest = FetchResolvedPhotoUriRequest.newInstance(photoMetadata)
//                .setMaxWidth(500)
//                .setMaxHeight(300)
//                .build()

            // 請求相片 URI
            placesClient?.fetchResolvedPhotoUri(photoRequest)
                ?.addOnSuccessListener { fetchResolvedPhotoUriResponse ->
                    val uri = fetchResolvedPhotoUriResponse.uri
                    _selectedTripPlaceImage.update { uri }
                    if (uri != null) {
                        getImageByteArray(uri.toString())
                    }

                }
                ?.addOnFailureListener { exception ->
                    if (exception is ApiException) {
                        val apiException = exception as ApiException
                        Log.e("TAG", "找不到地點：" + exception.message)
                        val statusCode = apiException.statusCode
                    }
                }
        }


//         Get the photo metadata.


//         Create a FetchPhotoRequest.
//        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
//            .setMaxWidth(500) // Optional.
//            .setMaxHeight(300) // Optional.
//            .build()
//        placesClient?.fetchPhoto(photoRequest)
//            ?.addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
//                val bitmap = fetchPhotoResponse.bitmap
//                _selectedTripPlaceBit.update { bitmap}
//
//                Log.d("PlaceBit", bitmap.toString())
//
//            }?.addOnFailureListener { exception: Exception ->
//                if (exception is ApiException) {
//                    Log.e("TAG", "Place not found: " + exception.message)
//                    val statusCode = exception.statusCode
//                    TODO("Handle error with given status code.")
//                }
//            }
    }

    fun consumeToastRequest() {
        _toastRequest.update { null }
    }

    fun consumeCheckSearch() {
        _checkSearch.update { null }
    }

    suspend fun downloadImageAsync(imageUrl: String): ByteArray = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(imageUrl)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to download image: ${response.code}")
            }
            return@withContext response.body?.bytes() ?: throw IOException("Empty response body")
        }
    }

    fun getImageByteArray(url: String) {
        viewModelScope.launch {
            try {
                val bytes = downloadImageAsync(url)
                val compressBytes = compressByQuality(bytes, 2)

                Log.d("getImageByteArray", bytes.toString())
                _selectedTripPlaceByte.update { compressBytes }
                // 處理圖片字節數組
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 方法1: 使用 Quality 壓縮
     * @param imageBytes 原始圖片的 byte array
     * @param quality 壓縮品質 (0-100)
     * @return 壓縮後的 byte array
     */
    fun compressByQuality(imageBytes: ByteArray, quality: Int): ByteArray {
        // 將 byte array 轉換為 Bitmap
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        // 創建一個 ByteArrayOutputStream
        val outputStream = ByteArrayOutputStream()

        // 使用 compress 方法壓縮圖片
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        // 回傳壓縮後的 byte array
        return outputStream.toByteArray()
    }
}



