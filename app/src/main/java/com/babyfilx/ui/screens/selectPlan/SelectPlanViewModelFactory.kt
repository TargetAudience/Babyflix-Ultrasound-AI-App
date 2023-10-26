package com.babyfilx.ui.screens.selectPlan

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.babyfilx.data.repositories.SelectPlanRepository

class SelectPlanViewModelFactory(private val context: Context, val repository: SelectPlanRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SelectPlanVIewModel::class.java)) {
            return SelectPlanVIewModel(context , repository ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}