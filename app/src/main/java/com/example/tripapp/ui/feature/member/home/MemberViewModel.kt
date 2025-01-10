package com.example.tripapp.ui.feature.member.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.tripapp.ui.feature.member.MemberRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MemberViewModel(context: Context) : ViewModel() {
    private val tag = "tag_MemberVM"

    private val _uid = MutableStateFlow(0)
    val uid = _uid.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun signOut() {
        MemberRepository.clearUid() // 直接呼叫 clearUid()
    }
}