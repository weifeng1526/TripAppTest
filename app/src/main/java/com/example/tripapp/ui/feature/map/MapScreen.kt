package com.example.tripapp.ui.feature.map

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState


import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontStyle

import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
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


import kotlinx.coroutines.launch
import java.sql.Blob


@Composable
fun MapRoute(navHostController: NavHostController, planNumber: Int = 0, planDate: String = "") {
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
    val selectedTripPlaceByte by viewModel.selectedTripPlaceByte.collectAsState()
    
    var type = selectedPlace?.type.toString()
    var name = selectedPlace?.displayName.toString()
    var address = selectedPlace?.formattedAddress.toString()
    var latLng = selectedPlace?.location

    val isLoading by viewModel.isLoading.collectAsState()
    //提示框
    val snackbarHostState = remember { SnackbarHostState() }
    // 回傳CoroutineScope物件以適用於此compose環境
    val scope = rememberCoroutineScope()
    var  showDialog by remember { mutableStateOf(false) }
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
        if (toastRequest != null) {
            Toast.makeText(context, toastRequest, Toast.LENGTH_SHORT).show()
            viewModel.consumeToastRequest()
        }
    }
    LaunchedEffect(checkReturn) {
        if (checkReturn != null) {
            Toast.makeText(context, checkReturn, Toast.LENGTH_SHORT).show()
            viewModel.consumeCheckSearch()
        }

    }

    LaunchedEffect(Unit) {
        viewModel.initClient(context)
        viewModel.getPlaces(
            search = "南機場朴子當歸鴨",
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

        if (isLoading && poiInfo.not()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center)
                    .zIndex(1f)
            )
        }

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
                    LatLng(21.9, 119.5),
                    LatLng(45.4, 145.7)
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
                        icon = BitmapDescriptorFactory.defaultMarker(210F)


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
                    modifier = Modifier.fillMaxWidth(1f),
                    shape = RoundedCornerShape(40.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = purple100,
                        unfocusedIndicatorColor = purple300
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    trailingIcon={
                        IconButton(
                            onClick = {checkSearch = true},
                            colors = IconButtonDefaults.iconButtonColors(
                               containerColor = Color.Transparent,
                                contentColor = purple200
                            )
                            ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "search"
                            )
                        }
                    }
                )

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
                    containerColor = purple100,
                    contentColor = purple200
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
            Button(
                modifier = Modifier
                    .padding(0.dp)
                    .align(Alignment.CenterHorizontally),

                onClick = {
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple100,
                    contentColor = purple200
                )
            ){
                Text(text = "使用教學", color = white100)
            }







            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp)) // 设置圆角
                            .fillMaxWidth()
                            .background(color = purple100)
                            .clickable { poiInfo = true }
                            .padding(8.dp),

                        ) {


                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(type, maxLines = 1, fontSize = 12.sp, color = white100,fontStyle= FontStyle.Italic)
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
                                            dstStart = "00:00:00",
                                            dstEnd = "00:00:00",
                                            dstInr = "00:00:00",
                                            dstPic = selectedTripPlaceByte!!

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
                                        navHostController.navigate(
                                            "${PLAN_EDIT_ROUTE}/$planNumber"
                                        )
                                    }

                                })


                    }

                }


            }
        }
        if (showDialog) {
            DialogWithImage(
                image =image,

                onConfirmation = {

                    showDialog = false
                },
                onDismissRequest = {

                    showDialog = false
                }
            )
        }
        if (poiInfo) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(),
                sheetState = poiState,
                onDismissRequest = { poiInfo = false },
                containerColor = purple100
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.White
                        )
                    }
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
                                            dstStart = "00:00:00",
                                            dstEnd = "00:00:00",
                                            dstInr = "00:00:00",
                                            dstPic = selectedTripPlaceByte!!
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
                                        navHostController.navigate(
                                            "${PLAN_EDIT_ROUTE}/$planNumber",

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
                            .padding(8.dp)
                            .clickable {
                                poiInfo = true

                            },
                        color = white100,

                    )

                    Text(
                        text = type, fontSize = 16.sp,
                        color = white100,
                        modifier = Modifier.padding(4.dp)
                    )

                    Text(
                        text = "地址${address}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(4.dp),
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
fun DialogWithImage(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    image: Uri?,

) {
    // 自訂Dialog內容
    Dialog(onDismissRequest = onDismissRequest) {
        // card適合用在dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp),
            // 形狀設定為圓角矩形
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = image,
                    contentDescription = "image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Text(
                    text = "去頂部輸入框輸入你想去的景點," ,
                    modifier = Modifier.padding(4.dp),
                )
                Text(
                    text = "按搜尋後可以直接點擊+,"
                            ,
                    modifier = Modifier.padding(4.dp),
                )
                Text(
                    text =
                            "或者點開資訊欄後再按+",
                    modifier = Modifier.padding(4.dp),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {

                    // 設定確定按鈕
                    TextButton(
                        onClick = { onDismissRequest()}
                    ) {
                        Text("知道了")
                    }
                }
            }
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
