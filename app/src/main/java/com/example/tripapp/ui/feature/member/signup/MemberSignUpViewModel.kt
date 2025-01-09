package com.example.tripapp.ui.feature.member.signup

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.member.Member
import com.example.tripapp.ui.feature.member.SignUpRequest
import com.ron.restdemo.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MemberSignUpViewModel(context: Context) : ViewModel() {
    private val tag = "tag_SignUpVM"

    private val _uid = MutableStateFlow(0)
    val uid = _uid.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isSignUpSuccess = MutableStateFlow(false)
    val isSignUpSuccess = _isSignUpSuccess.asStateFlow()

    private val _icon = MutableStateFlow("")
    val icon = _icon.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val isButtonEnabled = MutableStateFlow(true)


    fun onNameChanged(name: String) {
        _name.update { name }
    }

    fun onEmailChanged(email: String) {
        _email.update { email }
    }

    fun onPasswordChange(password: String) {
        _password.update { password }
    }

    fun onIconChanged(icon: String) {
        _icon.update { icon }
    }

    suspend fun signup(
        memNo: Int,
        memEmail: String,
        memName: String,
        memPw: String,
        memIcon: String
    ): Member? {
        try {
            val response = RetrofitInstance.api.signup(SignUpRequest(memNo, memEmail, memName, memPw, memIcon))
            Log.d(tag, "uid: ${memNo}, email: ${memEmail}, name: ${memName}, password: ${memPw}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return null
        }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            val user = signup(
                _uid.value,
                _email.value,
                _name.value,
                _password.value,
                _icon.value
            )
            if (user != null) {
//                _icon.update { R.drawable() -> "" }
                _isSignUpSuccess.update { true }
//                isButtonEnabled.value = true
            }
        }
    }

    fun showErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

//    fun onSignUpClick(
//        name: String, email: String, password: String, confirmPassword: String,
//        onSuccess: () -> Unit
//    ) {
//        isButtonEnabled.value = false
//        // 驗證輸入內容並執行註冊邏輯
//        if (
//            !name.isBlank() &&
//            Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
//            password.length in 6..8 &&
//            password.all { it.isLetterOrDigit() } &&
//            password == confirmPassword
//        ) {
//            onSuccess()
//        } else {
//            showErrorMessage("錯誤訊息")
//        }
//        isButtonEnabled.value = true
//    }
}