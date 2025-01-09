package com.example.tripapp.ui.feature.spending

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TotalSumVM:ViewModel(){

    private val _totalSum = MutableStateFlow(gettotalSumList())
    val totalSum = _totalSum.asStateFlow()

    init {
       _totalSum.value = gettotalSumList()
    }





////結算
//private var _totalSumVM =
//    MutableStateFlow<List<TotalSumVM>>(listOf())
//val totalSum = _totalSumVM.asStateFlow()








    private fun gettotalSumList(): List<TotalSum> {
        return listOf(
            TotalSum(
                userName = "小明",
                totalSum = "+1000",
            ),
            TotalSum(
                userName = "小美",
                totalSum = "-500",
            ),
            TotalSum(
                userName = "小美",
                totalSum = "-300",
            ),
            TotalSum(
                userName = "小美",
                totalSum = "4000",
            ),
            TotalSum(
                userName = "胖虎",
                totalSum = "3000",
            )
        )

    }
}