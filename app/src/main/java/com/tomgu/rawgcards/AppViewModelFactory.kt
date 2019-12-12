package com.tomgu.rawgcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.di.AppComponent

class AppViewModelFactory (private val application: AppApplication) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val t = super.create(modelClass)
        if (t is AppComponent.Injectable) {
            (t as AppComponent.Injectable).inject(application.appComponent())
        }
        return t
    }
}