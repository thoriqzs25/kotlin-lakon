package com.thariqzs.lakon

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thariqzs.lakon.preference.UserPreferences
import com.thariqzs.lakon.viewmodel.AuthViewModel

class ViewModelFactory private constructor(private val mApplication: Application, private val pref: UserPreferences?) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        val TAG = "vmfthoriq"

        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(application: Application, pref: UserPreferences? = null): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(application, pref)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            if (pref != null) {
                return AuthViewModel(mApplication, pref) as T
            } else {
                throw IllegalArgumentException("UserPreferences cannot be null for AuthViewModel")
            }
        }
//        else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
//            return DetailViewModel(mApplication) as T
//        } else if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
//            return FavoriteViewModel(mApplication) as T
//        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}