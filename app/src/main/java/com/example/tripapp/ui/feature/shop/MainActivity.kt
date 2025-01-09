package com.example.tripapp.ui.feature.shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.ui.theme.TripAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripAppTheme {
//                Navigation()
                ProductMainScreen(
                    navController = rememberNavController(),
                    productVM = ProductVM(),
                    tabVM = TabVM())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TripAppTheme {
        Main()
    }
}