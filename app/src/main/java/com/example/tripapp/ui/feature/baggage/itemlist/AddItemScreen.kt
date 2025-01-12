package com.example.tripapp.ui.feature.baggage.itemlist

import AddItemViewModel
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.baggage.Item
import com.example.tripapp.ui.feature.member.GetUid
import com.example.tripapp.ui.feature.member.MemberRepository

@Composable
fun AddItemRoute(navController: NavHostController, schNo: Int) {
    AddItemScreen(navController, schNo)
}

@SuppressLint("RememberReturnType")
@Composable
fun AddItemScreen(
    navController: NavHostController,
    schNo: Int, // 添加 schNo 參數
    addItemViewModel: AddItemViewModel = viewModel()
) {
    val  memNo = GetUid(MemberRepository)
    // 確保在初次進入頁面時呼叫 fetchData()
    LaunchedEffect(Unit) {
        addItemViewModel.fetchData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center, // 垂直置中
        horizontalAlignment = Alignment.CenterHorizontally // 水平置中
    ) {
        // 替代 TopAppBar 的 Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.green_100))
                .padding(horizontal = 24.dp, vertical = 4.dp),

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // 左右分散排列
        ) {
//            IconButton(onClick = { navController.popBackStack() }) {
//                Icon(
//                    Icons.AutoMirrored.Filled.ArrowBack,
//                    contentDescription = "回到上一頁",
//                    tint = colorResource(id = R.color.white_100)
//                )
//            }
            Spacer(modifier = Modifier.width(44.dp)) // 這樣設定 Spacer 的高度為 24.dp

            Text(
                text = "勾選後新增到行李清單",
                style = MaterialTheme.typography.titleLarge,
                color = colorResource(id = R.color.white_100),
                modifier = Modifier
                    .weight(1f) // 將標題居中
                    .wrapContentWidth(Alignment.CenterHorizontally) // 填滿剩餘空間
            )
            // 儲存變更
            IconButton(onClick = {
                navController.popBackStack() // 返回行李
                addItemViewModel.saveSelectedItems( memNo , schNo )
                Log.d("AddItemScreen", "${memNo}, ${schNo}")
            }) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "確認儲存",
                    tint = colorResource(id = R.color.white_100),
                    modifier = Modifier.size(36.dp) // 設定圖示大小，這裡設定為 48.dp

                )
            }
        }

        val sections by addItemViewModel.sections.collectAsState()
        val expandedStates by addItemViewModel.expandedStates.collectAsState()
        val checkedState by addItemViewModel.checkedState.collectAsState()
        val editingItem by addItemViewModel.editingItem.collectAsState()
        val editedText by addItemViewModel.editedText.collectAsState()

        LaunchedEffect(sections) {
            Log.e("section: ", "${sections.size}")
        }

//         傳遞解包過的值到 ExpandableLists
        ExpandableLists(
            sections = sections,
            expandedStates = expandedStates,
            checkedState = checkedState,
            onToggleExpanded = { sectionIndex ->
                addItemViewModel.updateExpandedState(sectionIndex, !expandedStates[sectionIndex]!!)
            },
            onCheckedChange = { itemNo, isChecked ->
                addItemViewModel.updateCheckedState(itemNo, isChecked)
            },


            editingItem = editingItem,
            editedText = editedText,
            innerPadding = PaddingValues(12.dp),
            addItemViewModel = addItemViewModel  // 也确保传递 ViewModel
        )
    }
}

@Composable
fun ExpandableLists(
    sections: List<Pair<String, List<Item>>>,  // 使用 SnapshotStateList
    expandedStates: Map<Int, Boolean>,
    checkedState: Map<Int, Boolean>,
    onToggleExpanded: (Int) -> Unit,           // 切換展開狀態的回調
    onCheckedChange: (Int, Boolean) -> Unit,  // 更新勾選狀態的回調

    editingItem: Map<String, Boolean>,
    editedText: Map<String, String>,
    innerPadding: PaddingValues,
    addItemViewModel: AddItemViewModel  // 添加 addItemViewModel 参数

) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sections.forEachIndexed { index, section ->
                item(key = section.first) {
                    val title = section.first
                    val items = section.second
                    val isExpanded = expandedStates[index] ?: true // 默認值為 true

                    Column(
                        modifier = Modifier
                            .width(317.dp)
                            .border(1.dp, Color(0x8065558F), RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleExpanded(index) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isExpanded) R.drawable.ah_baseline_arrow_drop_down_24 else R.drawable.ah_baseline_arrow_right_24
                                ),
                                contentDescription = if (isExpanded) "收起" else "展開",
                                modifier = Modifier.padding(end = 8.dp) // 圖標和文字間距
                            )
                            Text(
                                text = title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f) // 讓文字佔據剩餘空間
                            )
                        }

                        if (isExpanded) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
//                                verticalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                items.forEach { item ->
                                    val isChecked = checkedState[item.itemNo] ?: false

                                    Row(
                                        modifier = Modifier
                                            .width(317.dp)
                                            .height(52.dp)
                                            .border(
                                                1.dp,
                                                colorResource(id = R.color.purple_400),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .background(
                                                if (isChecked) colorResource(id = R.color.purple_200)
                                                else colorResource(id = R.color.purple_100),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .padding(
                                                start = 20.dp,
//                                                top = 2.dp,
                                                end = 20.dp,
//                                                bottom = 2.dp
                                            )
                                            .clickable { onCheckedChange(item.itemNo, !isChecked) },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clickable {
                                                    onCheckedChange(
                                                        item.itemNo,
                                                        !isChecked
                                                    )
                                                }, // 點擊圖標也可以切換狀態
                                            painter = if (isChecked) painterResource(id = R.drawable.ashley_pickoption02) else painterResource(
                                                id = R.drawable.ashley_pickoption01
                                            ),
                                            contentDescription = if (isChecked) "已選擇" else "未選擇"
                                        )
                                        Spacer(modifier = Modifier.width(24.dp))
                                        Text(text = item.itemName, modifier = Modifier.weight(1f))

//                                        // 當物品處於編輯模式時顯示 TextField，否則顯示文字
//                                        if (editingItem[index.toString()] == true) {
//                                            OutlinedTextField(
//                                                value = editedText[index.toString()] ?: item,
//                                                onValueChange = { newText ->
//                                                    addItemViewModel.updateEditedText(index.toString(), newText)
//                                                },
//                                                singleLine = true,
//                                                modifier = Modifier
//                                                    .fillMaxSize()
//                                                    .weight(1f)
//                                                    .padding(end = 16.dp),
//                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
//                                            )
//                                        } else {
//                                            Text(
//                                                text = editedText[index.toString()] ?: item,
//                                                modifier = Modifier.weight(1f)
//                                            )
//                                        }
//
//                                        // 編輯按鈕
//                                        IconButton(onClick = {
//                                            val isEditing = editingItem[item.itemName] == true
//                                            addItemViewModel.updateEditingItem(item.itemName, !isEditing)
//                                            if (!isEditing) {
//                                                addItemViewModel.updateEditedText(item.itemName,"")
//                                            }
//                                        }) {
//                                            Icon(
//                                                modifier = Modifier.size(24.dp),
//                                                painter = painterResource(id = if (editingItem[index.toString()] == true) R.drawable.ashley_edit_done else R.drawable.ashley_edit_text),
//                                                contentDescription = if (editingItem[index.toString()] == true) "確定" else "編輯"
//                                            )
//                                        }


//                                        // 刪除按鈕
//                                        IconButton(onClick = {
//                                            addItemViewModel.removeItemFromSection(index, item.toString())
//                                        }) {
//                                            Icon(
//                                                imageVector = Icons.Filled.Delete,
//                                                contentDescription = "刪除"
//                                            )
                                    }
                                }
                            }
                        }
//
//                            // 新增物品輸入框
//                            var newItem by remember { mutableStateOf("") }
//
//                            Row(
//                                modifier = Modifier
//                                    .width(317.dp)
//                                    .height(52.dp)
//                                    .border(
//                                        1.dp,
//                                        colorResource(id = R.color.purple_400),
//                                        RoundedCornerShape(10.dp)
//                                    )
//                                    .background(
//                                        colorResource(id = R.color.green_100),
//                                        RoundedCornerShape(10.dp)
//                                    )
//                                    .padding(
//                                        start = 20.dp,
////                                            top = 1.dp,
//                                        end = 20.dp,
////                                            bottom = 1.dp
//                                    ),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Icon(
//                                    painter = painterResource(id = R.drawable.ashley_edit_plus),
//                                    contentDescription = "新增選擇框",
//                                    modifier = Modifier.size(24.dp)
//                                )
//                                Spacer(modifier = Modifier.width(24.dp))
//
//                                // 新增物品的輸入框
//                                OutlinedTextField(
//                                    value = newItem,
//                                    onValueChange = { newItem = it },
//                                    placeholder = { Text("新增物品") },
//                                    modifier = Modifier
//                                        .fillMaxSize()
//                                        .weight(1f)
//                                        .padding(end = 16.dp),
//                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
//                                )
//                                val isExpanded = expandedStates[index] == false
//
//                                // 新增按鈕
//                                IconButton(onClick = {
//                                    if (newItem.isNotBlank()) {
//                                        addItemViewModel.addItemToSection(item.itemName, newItem)
//                                        newItem = ""
//                                    }
//                                }) {
//                                    Icon(
//                                        imageVector = Icons.Filled.Done,
//                                        contentDescription = "新增物品"
//                                    )
                    }
                }
            }
        }
    }
}
//        }
//    }
//}


@Preview
@Composable
fun PreviewAddItemScreen() {
    AddItemScreen(navController = NavHostController(LocalContext.current), schNo = 1)
}