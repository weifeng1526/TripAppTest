package com.example.tripapp.ui.feature.spending.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.tripapp.ui.theme.white100

@Composable
fun tripC(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white100),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "tripC",
            fontSize = 24.sp)
    }

}

@Preview
@Composable
fun tripCPre(){
    tripC()
}