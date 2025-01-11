package com.example.tripapp.ui.feature.member

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private val tag = "tag_MemberRepository"

@SuppressLint("StaticFieldLeak")
object MemberRepository {
    private const val PREF_NAME = "uid_preferences"
    private const val KEY_UID = "memNo"
    private const val TAG = "MemberRepository"

    private lateinit var context: Context // 儲存 Context
    private val _memNo = MutableStateFlow(0)
    val uid = _memNo.asStateFlow()

    private val _memName = MutableStateFlow("")
    val name = _memName.asStateFlow()

    private lateinit var sharedPreferences: SharedPreferences // 儲存 SharedPreferences

    fun initialize(context: Context) { // 初始化函式
        this.context = context
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // 初始化時載入Uid
        val saveUid = sharedPreferences.getInt(KEY_UID, 0)
        _memNo.update { saveUid }
        Log.d(tag, "初始化 UID： $saveUid")
    }

    //儲存Uid
    fun saveUid(newUid: Int){
        _memNo.update { newUid }
        sharedPreferences.edit().putInt(KEY_UID,newUid).apply()
        Log.d(tag, "儲存Uid: $newUid")
    }


    //清除Uid
    fun clearUid() {
        _memNo.update { 0 }
        sharedPreferences.edit().remove(KEY_UID).apply()
        Log.d(tag, "清除Uid")
    }

    // 讀取當前的 UID（方便同步操作）
    fun getUid(): Int = _memNo.value

    fun getName(name: String) {
        _memName.update { name }
        sharedPreferences.edit().putString(KEY_UID,name).apply()
        Log.d(tag, "取得會員名稱： $name")
    }

}

//class MemberRepository (context: Context) {
//    companion object {
//        private const val PREF_NAME = "uid_preferences"
//        private const val KEY_UID = "memNo"
//        private const val TAG = "MemberRepository"
//
//        @Volatile
//        private var instance: MemberRepository? = null
//
//        fun getInstance(context: Context): MemberRepository {
//            return instance ?: synchronized(this) {
//                instance ?: MemberRepository(context).also { instance = it }
//            }
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