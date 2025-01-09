package com.example.tripapp.ui.feature.trip.plan.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.restful.RequestVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PlanCreateViewModel : ViewModel() {
    val requestVM = RequestVM()

    private var _isSampleState = MutableStateFlow(false)
    val isSampleState = _isSampleState.asStateFlow()

    private var _planForCreateteState = MutableStateFlow(Plan())
    val planForCreateteState = _planForCreateteState.asStateFlow()

    fun setPlanForCreate(plan: Plan) {
        _planForCreateteState.update {
            plan
        }
        Log.d("setPlanForCreate", plan.toString())
    }

    fun setIsSample(isSample: Boolean) {
        _isSampleState.update {
            isSample
        }
    }

    fun createPlanByApi(plan: Plan) {
        viewModelScope.launch {
            val response = requestVM.CreatePlan(plan)
            Log.d("createPlanByApi", response.toString())
        }
    }
}