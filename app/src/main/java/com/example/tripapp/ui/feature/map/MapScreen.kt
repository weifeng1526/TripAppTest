package com.example.tripapp.ui.feature.map

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState


import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.tripapp.ui.feature.trip.plan.edit.PLAN_EDIT_ROUTE

import com.example.tripapp.ui.theme.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch


@Composable
fun MapRoute(navHostController: NavHostController, planNumber: Int = 0,planDate: String="") {
    MapScreen(
        viewModel = viewModel(),
        navHostController = navHostController,
        planNumber = planNumber,
        planDate = planDate,

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    navHostController: NavHostController = NavHostController(LocalContext.current),
    planNumber: Int = 0,
    planDate: String = "",

) {
    val context = LocalContext.current
    //place
    val tripPlace by viewModel.tripPlaceList.collectAsState()
    val selectedPlace by viewModel.selectedTripPlace.collectAsState()
    val search by viewModel.search.collectAsState()
    val image by viewModel.selectedTripPlaceImage.collectAsState()
    val checkReturn by viewModel.checkSearch.collectAsState()

    var type = selectedPlace?.type.toString()
    var name = selectedPlace?.displayName.toString()
    var address = selectedPlace?.formattedAddress.toString()
    var latLng = selectedPlace?.location
    //提示框
    val snackbarHostState = remember { SnackbarHostState() }
    // 回傳CoroutineScope物件以適用於此compose環境
    val scope = rememberCoroutineScope()

    //景點資訊
    var poiInfo by remember { mutableStateOf(false) }
    var poiState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var checkSearch by remember { mutableStateOf(false) }
//    地圖
    val myfavor = LatLng(25.02878879999997, 121.50661679999999)
    // CameraPositionState用於儲存地圖鏡頭狀態
    val cameraPositionState = rememberCameraPositionState {
        // 移動地圖到指定位置
        this.position = CameraPosition.fromLatLngZoom(myfavor, 15f)
    }
    var markerState by remember { mutableStateOf<MarkerState?>(null) }
//    var positions by remember { mutableStateOf(listOf<LatLng>()) }
    // 暫存最新標記的位置，方便之後移動地圖至該標記
//    var newPosition by remember { mutableStateOf<LatLng?>(null) }


    val toastRequest by viewModel.toastRequest.collectAsState()

    LaunchedEffect(toastRequest) {
        if (toastRequest!=null){
            Toast.makeText(context, toastRequest, Toast.LENGTH_SHORT).show()
            viewModel.consumeToastRequest()
        }
    }
    LaunchedEffect(checkReturn) {
        if (checkReturn!=null){
            Toast.makeText(context, checkReturn, Toast.LENGTH_SHORT).show()
            viewModel.consumeCheckSearch()
        }

    }

    LaunchedEffect(Unit) {
        viewModel.initClient(context)
        viewModel.getPlaces(
            search = "朴子當歸鴨",
        )
    }
    LaunchedEffect(latLng) {
        if (latLng != null) {
            markerState = MarkerState(position = latLng)
        }
    }

//    LaunchedEffect(positions) {
//        // search 改變
//        viewModel.getPlaces(
//            search = newPosition.toString(),
//        )
//    }
//    LaunchedEffect(search) {
//        // search 改變
//        viewModel.getPlaces(
//            search = search,
//        )
//    }

    if (checkSearch == true) {
        viewModel.getPlaces(
            search = search,
        )
        checkSearch = false
    }


    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
//            onMapLongClick = { latLng ->
//                // 長按地圖就將該點位置加入到儲存標記的list
//                positions = positions + latLng
//                // 更新最新標記位置
//                newPosition = latLng
//            },
            properties = MapProperties(// 是否呈現交通圖
                isTrafficEnabled = true,
                // 設定可捲動的範圍
                latLngBoundsForCameraTarget = LatLngBounds(
                    LatLng(22.045858, 119.426224),
                    LatLng(25.161124, 122.343094)
                ),
                // 設定地圖種類：NORMAL(一般圖，預設)、HYBRID(混合圖)、SATELLITE(衛星圖)、TERRAIN(地形圖)
                mapType = MapType.NORMAL,
                // 設定放大上限
                maxZoomPreference = 20f,
                // 設定縮小下限
                minZoomPreference = 5f
            ),
            // UI相關設定
            uiSettings = MapUiSettings(
                // 顯示指北針
                compassEnabled = true,
                // 允許旋轉手勢
                rotationGesturesEnabled = true,
                // 允許滑動手勢
                scrollGesturesEnabled = true,
                // 允許旋轉或縮放時可同時使用滑動手勢
                scrollGesturesEnabledDuringRotateOrZoom = true,
                // 開啟地圖傾斜手勢
                tiltGesturesEnabled = true,
                // 顯示縮放按鈕
                zoomControlsEnabled = true,
                // 允許縮放手勢
                zoomGesturesEnabled = true
            ),
            // 地圖載入完成後執行
            onMapLoaded = {
                Toast.makeText(context, "Map Loaded", Toast.LENGTH_SHORT).show()
            }
        ) {
            Marker(
//                Creating a state object during composition without using remember */
                state = rememberMarkerState(position = myfavor),
                title = "最愛的餐廳:朴子當歸鴨",
                icon = BitmapDescriptorFactory.defaultMarker(200F)


            )
            //search產生的

            if (latLng != null) {
                markerState?.let {
                    Marker(
                        state = MarkerState(position = latLng),
                        title = name,
                        snippet = address,
                        onInfoWindowClick = {
                            poiInfo = true
                        },
                        icon = BitmapDescriptorFactory.defaultMarker(220F)


                    )
                }

            }

//            //長按
//            positions.forEach { position ->
//                Marker(
//                    state = MarkerState(position = position),
//                    title = name,
//                    snippet = address,
//                    onInfoWindowClick = {
//                        poiInfo = true
//                    },
//                    // 長按訊息視窗就移除該標記
//                    onInfoWindowLongClick = {
//                        positions = positions - position
//                    }
//                )
//            }
        }


//輸入框
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { newSearch -> viewModel.onSearchChange(newSearch) },
                    label = { Text(text = "Search") },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Blue,
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)

                )
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        checkSearch = true

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = purple200,
                        contentColor = purple300
                    )
                ) {
                    Text(text = "搜尋", color = white100)
                }
            }

//回去
            Button(
                modifier = Modifier
                    .padding(0.dp)
                    .align(Alignment.CenterHorizontally),

                onClick = {
                    navHostController.popBackStack(
                        "${PLAN_EDIT_ROUTE}/$planNumber",
                        false
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple200,
                    contentColor = purple300
                )
            ) {
                Text(text = "回到行程表", color = white100)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {


            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp)) // 设置圆角
                            .fillMaxWidth()
                            .background(color = purple200)
                            .clickable { poiInfo = true },

                        ) {


                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(type, maxLines = 1, fontSize = 12.sp, color = white100)
                            Spacer(modifier = Modifier.padding(top = 8.dp))
                            Text(name, maxLines = 1, fontSize = 16.sp, color = white100)
                            Spacer(modifier = Modifier.padding(top = 8.dp))
                            Text(
                                address,
                                maxLines = 2,
                                fontSize = 12.sp,
                                overflow = TextOverflow.Ellipsis,
                                color = white100
                            )
                        }
                        Icon(imageVector = Icons.Default.Add,
                            contentDescription = "add",
                            tint = Color.White,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    if (latLng != null) {

                                        viewModel.addPlace(
                                            schNo = planNumber,
                                            poiName = name,
                                            poiAdd = address,
                                            poiLat = latLng.latitude.toBigDecimal(),
                                            poiLng = latLng.longitude.toBigDecimal(),
                                            poiPic = image.toString(),
                                            poiLab = type,
                                            dstDate = planDate,
                                            dstStart ="00:00:00" ,
                                            dstEnd ="00:00:00"  ,
                                            dstInr = "00:00:00" ,

                                            )
                                    }
                                    scope.launch {
                                        // 呼叫showSnackbar()會改變SnackbarHostState狀態並顯示Snackbar
                                        snackbarHostState.showSnackbar(
                                            "${name}已加入行程表",
                                            // 建議加上取消按鈕
                                            withDismissAction = true,
                                            // 不設定duration，預設為Short(停留短暫並自動消失)
                                            // duration = SnackbarDuration.Long
                                        )
                                    }

                                })


                    }

                }


            }
        }
        if (poiInfo) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(),
                sheetState = poiState,
                onDismissRequest = { poiInfo = false },
                containerColor = purple200
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Icon(imageVector = Icons.Default.Add,
                            contentDescription = "add",
                            tint = Color.White,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    if (latLng != null) {

                                        viewModel.addPlace(
                                            schNo = planNumber,
                                            poiName = name,
                                            poiAdd = address,
                                            poiLat = latLng.latitude.toBigDecimal(),
                                            poiLng = latLng.longitude.toBigDecimal(),
                                            poiPic = image.toString(),
                                            poiLab = type,
                                            dstDate = planDate,
                                            dstStart = "00:00:00" ,
                                            dstEnd ="00:00:00"  ,
                                            dstInr = "00:00:00" ,

                                            )
                                    }
                                    scope.launch {
                                        // 呼叫showSnackbar()會改變SnackbarHostState狀態並顯示Snackbar
                                        snackbarHostState.showSnackbar(
                                            "${name}已加入行程表",
                                            // 建議加上取消按鈕
                                            withDismissAction = true,
                                            // 不設定duration，預設為Short(停留短暫並自動消失)
                                            // duration = SnackbarDuration.Long
                                        )
                                    }


                                })
                    }

                    //外來照片顯示
                    AsyncImage(
                        model = image.toString(),
                        contentDescription = "image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Text(
                        text = name, fontSize = 20.sp, modifier = Modifier
                            .padding(12.dp)
                            .clickable {
                                poiInfo = true

                            },
                        color = white100
                    )

                    Text(
                        text = type, fontSize = 16.sp,
                        color = white100,
                        modifier = Modifier.padding(12.dp)
                    )

                    Text(
                        text = "地址${address}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(12.dp),
                        color = white100

                    )


                }
            }
        }

        // 建立SnackbarHost以設定Snackbar顯示位置；至於顯示與否依據SnackbarHostState狀態
        SnackbarHost(hostState = snackbarHostState)
    }
    // 移動地圖至最新標記所在位置(newMarker一旦改變就會執行)
    LaunchedEffect(latLng) {
        latLng?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, 15f)
            )
        }
    }

}

@Composable
@Preview
fun mapPreview() {
    MapScreen(viewModel = viewModel())
}


//taipei station
//台北車站 朴子當歸鴨
//桃園車站
//台北101 淡水捷運站
//中壢緯育
//新北市
