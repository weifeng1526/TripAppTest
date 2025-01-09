package com.example.tripapp.ui.feature.member

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun GetUid(memberRepository: MemberRepository): Int {
    val uid by memberRepository.uid.collectAsState()
    return uid
}