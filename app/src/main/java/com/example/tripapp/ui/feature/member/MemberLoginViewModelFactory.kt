package com.example.tripapp.ui.feature.member

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tripapp.ui.feature.member.login.MemberLoginViewModel

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