package com.example.tripapp.ui.feature.member

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tripapp.ui.feature.member.home.MemberViewModel

class MemberViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MemberViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}