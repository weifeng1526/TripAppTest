package com.example.tripapp.ui.feature.member

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Member (
    val memNo: Int,
    val memEmail: String,
    val memName: String,
    val memPw: String,
    val memSta: Byte,
    val memIcon: String
)

data class LoginRequest(
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

//private val tag = "tag_MemberRepository"
//
//class MemberRepository (context: Context) {
//    companion object {
//        private const val PREF_NAME = "uid_preferences"
//        private const val KEY_UID = "memNo"
//        private const val TAG = "MemberRepository"
//    }
//
//    private val _memNo = MutableStateFlow(0)
//    val uid = _memNo.asStateFlow()
//
//    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//
//    init {
//        //初始化時載入Uid
//        val saveUid = sharedPreferences.getInt(KEY_UID, 0)
//        _memNo.update { saveUid }
//        Log.d(tag, "初始化 UID： $saveUid")
//    }
//
//    //儲存Uid
//    fun saveUid(newUid: Int){
//        _memNo.update { newUid }
//        sharedPreferences.edit().putInt(KEY_UID,newUid).apply()
//        Log.d(tag, "儲存Uid: $newUid")
//    }
//
//    //清除Uid
//    fun clearUid() {
//        _memNo.update { 0 }
//        sharedPreferences.edit().remove(KEY_UID).apply()
//        Log.d(tag, "清除Uid")
//    }
//
//    // 讀取當前的 UID（方便同步操作）
//    fun getUid(): Int = _memNo.value
//}

//    private val preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
//    private val repository =MemberRepository

//    private fun saveMemberInfoPref(member:Member){
//        with(preferences.edit()){
//            putInt("uid",member.memNo)
//            putString("email",member.memEmail)
//            putString("name",member.memName)
//            putBoolean("sta",member.memSta)
//            putString("icon",member.memIcon)
//
//        }
//    }