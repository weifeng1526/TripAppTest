package com.example.tripapp.ui.feature.member.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MemberViewModel: ViewModel() {
    private val tag = "tag_MemberVM"

    private val _uid = MutableStateFlow(0)
    val uid = _uid.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
}