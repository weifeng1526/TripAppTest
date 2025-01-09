package com.example.tripapp.ui.feature.map

import android.content.Context
import android.content.res.Configuration
import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class MapViewModel : ViewModel() {
    private val _search = MutableStateFlow("")
    var search =_search.asStateFlow()

    private  val  _positions = MutableStateFlow<List<LatLng>>(emptyList())
    var positions = _positions.asStateFlow()


    private val _newPosition = MutableStateFlow<LatLng?>(null)
    var newPosition = _newPosition.asStateFlow()

    var placesClient: PlacesClient? = null

    private val _placeList = MutableStateFlow<List<Place>>(emptyList())
    var placeList = _placeList.asStateFlow()

    private val _tripPlaceList = MutableStateFlow<List<PlaceSearch>>(emptyList())
    var tripPlaceList = _tripPlaceList.asStateFlow()

    private val _selectedTripPlace = MutableStateFlow<SelectPlace?>(null)
    var selectedTripPlace = _selectedTripPlace.asStateFlow()

    fun onSearchChange(search: String) {
        _search.update { search }

    }

    fun onPositionChange(positions: List<LatLng>) {
        _positions.update { positions }

    }
    fun onNewPositionChange(newPosition: LatLng?) {
        _newPosition.update { newPosition }

    }

    fun initClient(context: Context) {
        placesClient = Places.createClient(context)
    }


    fun getPlaces(search: String) {

        val placeFields = listOf(
            Place.Field.ID,
        )
        // Use the builder to create a SearchByTextRequest object.
        val searchByTextRequest = SearchByTextRequest.builder(search, placeFields)
            .setMaxResultCount(1)
            .setLocationRestriction(RectangularBounds.newInstance(LatLng(22.045858, 119.426224),
                LatLng(25.161124, 122.343094))).build()
        placesClient?.searchByText(searchByTextRequest)
            ?.addOnSuccessListener { response ->
                _placeList.update { response.places }
                val place = response.places.map { PlaceSearch(it.id.toString(),) }
                _tripPlaceList.update { place }
                if (_tripPlaceList.value.isNotEmpty()) {
                    val placeId = placeList.value.getOrNull(0)?.id ?: return@addOnSuccessListener
                    fetchPlaceDetail(placeId = placeId)
                }
            }
    }

    fun fetchPlaceDetail(placeId: String) {
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
//            Place.Field.PHOTO_METADATAS
        )

// Construct a request object, passing the place ID and fields array.
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient?.fetchPlace(request)
            ?.addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                _selectedTripPlace.update {
                    SelectPlace(
                        place.displayName,
                        place.formattedAddress,
                        place.location,
                        place.types.toString(),
//                        place.photoMetadatas
                    )
                }
            }?.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    Log.e("TAG", "Place not found: ${exception.message}")
                    val statusCode = exception.statusCode
                }
            }

    }
    fun addPlace(PlaceDetial:SelectPlaceDetail) {

        viewModelScope.launch {
           MapRetrofit.api.selectPlace(PlaceDetial)

        }
    }
//    fun getPhoto(photoMetadatas: List<PhotoMetadata?>) {

// 取得地點物件（此範例使用 fetchPlace()，但您也可以使用 findCurrentPlace()）


//        val photoMetadata = photoMetadatas[0]
//
//        // 取得歸屬文字和作者歸屬。
//        val attributions = photoMetadata?.attributions
//        val authorAttributions = photoMetadata?.authorAttributions
//
//        // 建立 FetchResolvedPhotoUriRequest。
//        if (photoMetadata != null) {
//            val photoRequest = FetchResolvedPhotoUriRequest.newInstance(photoMetadata)
//                .setMaxWidth(500)
//                .setMaxHeight(300)
//                .build()
//
//            // 請求相片 URI
//            placesClient?.fetchResolvedPhotoUri(photoRequest)
//                ?.addOnSuccessListener { fetchResolvedPhotoUriResponse ->
//                    val uri = fetchResolvedPhotoUriResponse.uri
//                    val requestOptions = RequestOptions().override(Target.SIZE_ORIGINAL)
//                    Glide.with(this).load(uri).apply(requestOptions).into(imageView)
//                }
//                ?.addOnFailureListener { exception ->
//                    if (exception is ApiException) {
//                        val apiException = exception as ApiException
//                        Log.e("TAG", "找不到地點：" + exception.message)
//                        val statusCode = apiException.statusCode
//                    }
//                }
//        }
//    }

    }

