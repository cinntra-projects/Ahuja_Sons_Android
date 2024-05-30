package com.ahuja.sons.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.service.repository.MainRepos

import kotlinx.coroutines.CoroutineDispatcher

class MainViewModelProvider(
    val app: Application,
    private val repos: MainRepos,
    private val dispatchers: CoroutineDispatcher,
    private val fanxApi: Apis

): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(app,repos,dispatchers,fanxApi) as T
    }
}