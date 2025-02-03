package com.example.tripapp.ui.feature.trip.plan.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripapp.ui.feature.trip.dataObjects.CrewMmeber
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.feature.trip.plan.crew.PlanCrewViewModel
import com.example.tripapp.ui.restful.RequestVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PlanCreateViewModel : ViewModel() {
    val requestVM = RequestVM()
    val planCrewViewModel = PlanCrewViewModel()

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

    fun createPlanWithCrewByApi(plan: Plan, callback: (Int) -> Unit) {
        viewModelScope.launch {
            val planRresponse = requestVM.CreatePlan(plan)
            var responsedId = 0
            var newCrew = CrewMmeber()
            planRresponse?.let { plan ->
                newCrew.apply {
                    crewNo = 0
                    schNo = plan.schNo
                    memNo = plan.memNo
                    crewPeri = 2
                    crewIde = 2
                    crewName = plan.schName
                    crewInvited = 3
                }
            }
            val crewResponse = requestVM.CreateCrew(newCrew)
            crewResponse?.let { crewResponse ->
                responsedId = crewResponse.schNo
                Log.d("createPlanWithCrewByApi", "${crewResponse}")
                Log.d("createPlanByApi", crewResponse.toString())
            }
        callback(responsedId)
        }
    }
}