package com.example.tripapp.ui.feature.member.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tripapp.R
import com.example.tripapp.ui.feature.member.MemberRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MemberViewModel(context: Context) : ViewModel() {
    private val tag = "tag_MemberVM"

    private val _uid = MutableStateFlow(0)
    val uid = _uid.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _memIconEditState = MutableStateFlow(MemberIcon())
    val memIconEditState = _memIconEditState.asStateFlow()

    private val _memIconState = MutableStateFlow(emptyList<MemberIcon>())
    val memIconState: StateFlow<List<MemberIcon>> = _memIconState.asStateFlow()

    init {
        val uid = MemberRepository.getUid()
        _uid.value = uid
        Log.d(tag, "innitUid: ${uid}")
        val icon =_memIconEditState.value
        Log.d(tag, "initIcon: ${icon}")
//        val newIcon = if (icon.memNo == uid) { _memIconState.update { _memIconState.value }} else {}
//        Log.d(tag, "newIcon: $newIcon")
    }

//    // [未完成]期望能取得對應uid的icon
//    fun getMemIcon() {
//        val memIcon = memIcon().find { it.memNo == _uid.value }
//        if (memIcon != null) {
//            val icon = _memIconEditState.update { it.copy(memNo = _uid.value, img = memIcon.img) }
//            Log.d(tag, "icon: ${icon}")
//        }
//    }
}

fun memIcon(): List<MemberIcon> {
    return listOf(
        MemberIcon(R.drawable.ic_member),
        MemberIcon(R.drawable.ic_member_16),
        MemberIcon(R.drawable.ic_member_02),
        MemberIcon(R.drawable.ic_member_03),
        MemberIcon(R.drawable.ic_member_04),
        MemberIcon(R.drawable.ic_member_05),
        MemberIcon(R.drawable.ic_member_06),
        MemberIcon(R.drawable.ic_member_07),
        MemberIcon(R.drawable.ic_member_08),
        MemberIcon(R.drawable.ic_member_09),
        MemberIcon(R.drawable.ic_member_10),
    )
}
