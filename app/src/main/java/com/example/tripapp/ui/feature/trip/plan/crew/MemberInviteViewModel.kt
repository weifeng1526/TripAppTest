package com.example.tripapp.ui.feature.trip.plan.crew

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.member.Member
import com.example.tripapp.ui.feature.trip.dataObjects.CrewMmeber
import com.example.tripapp.ui.restful.RequestVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MemberInviteViewModel : ViewModel() {
    val requestVM = RequestVM()

    private var _member =  MutableStateFlow(Member(
        memNo = 0,
        memEmail = "",
        memName = "",
        memPw = "",
        memSta = 0,
        memIcon = ""
    ))
    val member = _member.asStateFlow()

    private var _members =  MutableStateFlow(emptyList<Member>())
    val members = _members.asStateFlow()

    fun getMembersRequest(callback: (List<Member>) -> Unit) {
        viewModelScope.launch {
            val response = requestVM.GetMembers()
            response.let {
                callback(it)
            }
        }
    }
    fun setMember(member: Member) {
        _member.update { member }
    }

    fun getMember(): Member {
        return member.value
    }

    fun setMembers(members: List<Member>) {
        _members.update { members }
    }

    fun getMembers(): List<Member> {
        return members.value
    }

    fun filterMemberInThisCrew(crewMembers: List<CrewMmeber>, members: Member) {

    }
}