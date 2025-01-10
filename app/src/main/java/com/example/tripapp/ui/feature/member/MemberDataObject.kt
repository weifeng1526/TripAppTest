package com.example.tripapp.ui.feature.member

data class Member (
    val memNo: Int,
    val memEmail: String,
    val memName: String,
    val memPw: String,
    val memSta: Int,
    val memIcon: String
)

data class LoginRequest(
    val memNo: Int,
    val memEmail: String,
    val memPw:String
)

data class SignUpRequest(
    val memNo: Int,
    val memEmail: String,
    val memName: String,
    val memPw: String,
    val memIcon: String
)