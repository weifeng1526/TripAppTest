package com.example.tripapp.ui.feature.member

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun GetUid(memberRepository: MemberRepository): Int {
    val uid by memberRepository.uid.collectAsState()
    return uid
}

@Composable
fun GetName(): String {
    val name by MemberRepository.name.collectAsState()
    return name
}

@Composable
fun IsLogin(): Boolean {
    val uid by MemberRepository.uid.collectAsState()
    return uid != 0
}

//@Composable
//fun GetName(memberRepository: MemberRepository) : String {
//    val name by memberRepository.name.collectAsState()
//    return name
//}

