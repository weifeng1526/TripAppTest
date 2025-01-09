package com.example.tripapp.ui.feature.member.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.member.LoginRequest
import com.example.tripapp.ui.feature.member.Member
import com.example.tripapp.ui.feature.member.MemberRepository
import com.ron.restdemo.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MemberLoginViewModel(context: Context) : ViewModel() {
    private val tag = "tag_LoginVM"

    private val memberRepository = MemberRepository
    val uid: StateFlow<Int> = memberRepository.uid

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isLoginSuccess = MutableStateFlow(false)
    val isLoginSuccess = _isLoginSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val isButtonEnabled = MutableStateFlow(true)


    fun onEmailChanged(email: String) {
        _email.update { email }
    }

    fun onPasswordChange(password: String) {
        _password.update { password }
    }

    fun onLoginClick(newUid: Int) {
        viewModelScope.launch {
            val member = login(_email.value, _password.value)
            if (member != null) {
                _isLoginSuccess.update { true }
                memberRepository.saveUid(newUid) //儲存登入成功後的 Uid
//                isButtonEnabled.value = true
//                saveMemberInfoPref(member)
//                MemberRepository()
            }
            // 儲存 User 資料 跳頁
        }
    }

    fun logout() {
        viewModelScope.launch {
            memberRepository.clearUid() //清除 Uid
        }
    }
    fun showErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    suspend fun login(memEmail: String, memPw: String): Member? {
        try {
            val response = RetrofitInstance.api.login(LoginRequest(memEmail, memPw))
            Log.d(tag, "response: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return null
        }
    }
}

class MemberLoginViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberLoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MemberLoginViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}