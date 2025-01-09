package com.example.tripapp.ui.feature.trip.plan.edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.trip.dataObjects.Destination
import com.example.tripapp.ui.restful.RequestVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime


class PlanEditViewModel : ViewModel() {
    val requestVM = RequestVM()

    private var _dstState = MutableStateFlow(Destination())
    val dstState = _dstState.asStateFlow()

    private val _dstsState = MutableStateFlow(emptyList<Destination>())
    val dstsState: StateFlow<List<Destination>> = _dstsState.asStateFlow()

    private val _dstsForDateState = MutableStateFlow(emptyList<Destination>())
    val dstsForDateState: StateFlow<List<Destination>> = _dstsForDateState.asStateFlow()

    private val _dstsForSample = MutableStateFlow(emptyList<Destination>())
    val dstsForSample: StateFlow<List<Destination>> = _dstsForSample.asStateFlow()

    fun addToDses(dst: Destination) {
        _dstsState.update {
            val dsts = it.toMutableList()
            dsts.add(dst)
            dsts
        }
    }

    fun addToDsesByApi(dst: Destination) {
        viewModelScope.launch {
            val response = requestVM.AddDst(dst)
            response?.let {
                    addToDses(dst)
                     Log.d("addToDsesByApi response", "${response}")
                }
            }
        }

    fun setDst(dst: Destination) {
        _dstState.update {
            dst
        }
    }

    fun setDstByApi(dst: Destination) {
        viewModelScope.launch {
            val response = requestVM.UpdateDst(dst)
            response?.let { setDst(it) }
            Log.d("setDstByApi response", "${response}")
            Log.d("setDstByApi", "${dstState.value}")
        }
    }

    fun setDstsByApi(id: Int) {
        viewModelScope.launch {
            val response = requestVM.GetDstsBySchedId(id)
            setDsts(response)
            Log.d("setDstsByApi response", "${response}")
            Log.d("setDstsByApi", "${dstsState.value}")
        }
    }

    fun setDstsForSampleByApi(memId: Int, schNo: Int) {
        viewModelScope.launch {
            val response = requestVM.GetDestsSample(memId, schNo)
            setDstsForSample(response)
            Log.d("setDstsForSampleByApi response", "${response}")
            Log.d("_dstsForSample", "${_dstsForSample.value}")
        }
    }

    fun setDstsForSample(dsts: List<Destination>) {
        _dstsForSample.update {
            dsts
        }
    }

    fun setDsts(dsts: List<Destination>) {
        _dstsState.update {
            dsts
        }
        Log.d("setDstsByApi", "${dstsState.value}")
    }

    fun removeFromDsts(dst: Destination) {
        _dstsState.update {
            val dsts = it.toMutableList()
            dsts.remove(dst)
            dsts
        }
    }

    /* 以下是某天的行程明細的MutableStateFlow **/
    fun setDstsForDate(date: String) {
        val dstsFordate = _dstsState.value.filter { it.dstDate.equals(date) }
        _dstsForDateState.update {
            dstsFordate
        }
    }

    fun setDstForDateByDesc() {
        _dstsForDateState.update { dsts ->
            dsts.sortedBy { LocalTime.parse(it.dstStart).toSecondOfDay() }
        }
    }

    fun addToDstForDate(dst: Destination) {
        _dstsForDateState.update {
            val dstsFordate = it.toMutableList()
            dstsFordate.add(dst)
            dstsFordate
        }
    }

    fun onStartTimeChange() {
        setDstForDateByDesc()
    }
}