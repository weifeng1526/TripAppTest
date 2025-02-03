package com.example.tripapp.ui.feature.trip.plan.home

import android.util.Log
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import kotlinx.coroutines.launch
import com.example.tripapp.ui.restful.RequestVM
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PlanHomeViewModel : ViewModel() {
    val requestVM = RequestVM()

    private var _planState = MutableStateFlow(Plan())
    val planState = _planState.asStateFlow()

    private val _plansState = MutableStateFlow(emptyList<Plan>())
    val plansState: StateFlow<List<Plan>> = _plansState.asStateFlow()

    private val _contriesState = MutableStateFlow(emptyList<String>())
    val contriesState: StateFlow<List<String>> = _contriesState.asStateFlow()

    private val _plansOfMemberState = MutableStateFlow(emptyList<Plan>())
    val plansOfMemberState: StateFlow<List<Plan>> = _plansOfMemberState.asStateFlow()

    private val _plansByContryState = MutableStateFlow(emptyList<Plan>())
    val plansByContryState: StateFlow<List<Plan>> = _plansByContryState.asStateFlow()


    private val _isDialogShow = MutableStateFlow(false)
    val isDialogShow: StateFlow<Boolean> = _isDialogShow.asStateFlow()

    private val _searchWord = MutableStateFlow("")
    val searchWord: StateFlow<String> = _searchWord.asStateFlow()

    private var _memberNumber = MutableStateFlow(Int)
    val memberNumberState = _memberNumber.asStateFlow()

    fun setPlanByApi(id: Int) {
        viewModelScope.launch {
            val planResponse = requestVM.GetPlan(id)
            planResponse?.let {
                _planState.update {
                    planResponse
                }
                Log.d("planState", "${planState.value}")
            }
        }
    }

    fun createPlan(plan: Plan) {
        viewModelScope.launch {
            val planResponse = requestVM.CreatePlan(plan)
            planResponse?.let {
                _planState.update {
                    planResponse
                }
            }
        }
    }

    fun setPlans(plans: List<Plan>) {
        _plansState.update {
            plans
        }
    }

    fun setPlansByMemberByApi(memId: Int) {
        viewModelScope.launch {
            val planResponse = requestVM.GetPlanByMemId(memId)
            Log.d("setPlansByMemberByApi", "${planResponse}")
            setPlansOfMember(planResponse)
            Log.d("setPlansByMemberByApi", "${_plansOfMemberState.value}")
        }
    }

    fun updatePlanByApi(plan: Plan) {
        viewModelScope.launch {
            val planResponse = requestVM.UpdatePlan(plan)
            planResponse?.let {
                _planState.update {
                    planResponse
                }
            }
        }
    }

    fun setPlansOfMember(plans: List<Plan>) {
        _plansOfMemberState.update {
            plans
        }
    }

    fun setContryNamesFromPlans(plans: List<Plan>) {
        _contriesState.update {
            plans.groupBy {
                it.schCon
            }.keys.toList()
        }
    }

    fun setPlansByContry(contry: String) {
        viewModelScope.launch {
            val planResponse = requestVM.GetPlansByContry(contry)
            planResponse.let {
                _plansByContryState.update {
                    planResponse
                }
                if (plansByContryState.value.isNotEmpty())
                    _isDialogShow.update { true }
            }
            Log.d("setPlansByContry", "${planResponse}")
            Log.d("_plansByContryState", "${_plansByContryState.value}")
        }
    }

    fun addPlan(plan: Plan) {
        _plansState.update {
            val plans = it.toMutableList()
            plans.add(plan)
            plans
        }
    }

    fun setPlan(plan: Plan) {
        _planState.update {
            it.let { plan }
        }
    }


    fun removePlan(id: Int) {
        _plansState.update {
            val plans = it.toMutableList()
            plans.removeIf { it.schNo == id }
            plans
        }
    }

    fun onDismissDialog() {
        _isDialogShow.update { false }
    }

    fun setSearchWord(word: String) {
        _searchWord.update { word }
        Log.d("searchWord", "${word}")
    }

    fun updatePlanImage(schId: Int, image: MultipartBody.Part?) {
        viewModelScope.launch {
            val putIdPart = schId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
            Log.d("putIdPart", "${putIdPart}")
            requestVM.UpdatePlanImage(putIdPart, image)
        }
    }

    /* 已經加入的行程表**/
    fun setPlansOfMemberInCrewByApi(id: Int) {
        viewModelScope.launch {
            val planResponse = requestVM.GetPlansOfMemberInCrew(id)
            Log.d("GetPlansOfMemberInCrew", "${planResponse}")
            setPlans(planResponse)
            Log.d("GetPlansOfMemberInCrew", "${_plansOfMemberState.value}")
        }
    }
}

