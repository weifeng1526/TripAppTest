package com.example.tripapp.ui.feature.member.home.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.ui.feature.member.home.MemberScreen
import com.example.tripapp.ui.theme.white100

@Composable
fun NotifyRoute() {
    NotifyScreen()
}

@Preview
@Composable
fun PreviewNotifyRoute() {
    NotifyScreen()
}

@Composable
fun NotifyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white100),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,


        ) {
        Text(
            text = "我的行李 ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}