package kr.dksin.paginggallerypicker.base

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel

abstract class BaseViewModel() : ViewModel() {

    val isLoading: ObservableBoolean = ObservableBoolean(false)
    fun showProgress() = isLoading.set(true)
    fun hideProgress() = isLoading.set(false)

}