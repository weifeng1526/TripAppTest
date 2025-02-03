package com.example.tripapp.ui.feature.trip.plan.crew

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.member.Member
import com.example.tripapp.ui.feature.trip.dataObjects.CrewMmeber
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.restful.RequestVM
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlanCrewViewModel: ViewModel() {
    val requestVM = RequestVM()

    private var _oneOfCewMemberSatate =  MutableStateFlow(CrewMmeber())
    val oneOfCewMemberSatate = _oneOfCewMemberSatate.asStateFlow()

    private var _crewOfMembersSatate =  MutableStateFlow(emptyList<CrewMmeber>())
    val crewOfMembersSatate = _crewOfMembersSatate.asStateFlow()


    fun createCrewRequest(crewMmeber: CrewMmeber) {
        viewModelScope.launch {
            val response = requestVM.CreateCrew(crewMmeber)
            response?.let {
                Log.d("tag_PlanCrewViewModel", "${it}}")
            }
        }
    }

    fun getCrewMembersRequest(id: Int, callback: (List<CrewMmeber>) -> Unit) {
        viewModelScope.launch {
            val response = requestVM.GetCrewMmebersBySchId(id)
            response.let {
                Log.d("tag_PlanCrewViewModel", "${it}}")
                callback(response)
            }
        }
    }

    fun setCrewMember(crewMember: CrewMmeber) {
        _oneOfCewMemberSatate.update { crewMember }
    }

    fun getCrewMember(): CrewMmeber {
        return oneOfCewMemberSatate.value
    }

    fun setCrewMembers(crewMembers: List<CrewMmeber>) {
        _crewOfMembersSatate.update { crewMembers }
    }

    fun removeCrewMember(crewMember: CrewMmeber) {
        _crewOfMembersSatate.update { currentList ->
            currentList.toMutableList().apply { remove(crewMember) }
        }
    }

    fun addToCrews(crewMmeber: CrewMmeber) {
        val crewMmebers = _crewOfMembersSatate.value.toMutableList()
        _crewOfMembersSatate.update {
            crewMmebers.add(crewMmeber)
            crewMmebers
        }
    }
    fun updateMemberCrewByApi(crewMmeber: CrewMmeber) {
        viewModelScope.launch {
            val response = requestVM.UpdateCrewMmeber(crewMmeber)
            response?.let {
                setCrewMember(crewMmeber)
                Log.d("crewMmeberUpdate", "${it}}")
            }
        }
    }

    fun addToCrewsByApi(crewMmeber: CrewMmeber) {
        viewModelScope.launch {
            val response = requestVM.CreateCrew(crewMmeber)
            response?.let {
                addToCrews(it)
                Log.d("tag_PlanCrewViewModel", "${it}}")
            }
        }
    }
}