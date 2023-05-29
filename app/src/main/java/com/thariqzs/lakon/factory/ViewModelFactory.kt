package com.thariqzs.lakon.factory

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thariqzs.lakon.di.Injection
import com.thariqzs.lakon.preference.UserPreferences
import com.thariqzs.lakon.ui.auth.AuthViewModel
import com.thariqzs.lakon.ui.main.MainViewModel
import com.thariqzs.lakon.ui.map.MapsViewModel
import com.thariqzs.lakon.ui.post.PostViewModel

class ViewModelFactory private constructor(private val mApplication: Application, private val pref: UserPreferences?, private val context: Context) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        val TAG = "vmfthoriq"

        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(application: Application, pref: UserPreferences? = null, context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(application, pref, context)
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
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            if (pref != null) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(pref, Injection.provideRepository(context)) as T
            } else {
                throw IllegalArgumentException("UserPreferences cannot be null for MainViewModel")
            }
        } else if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            if (pref != null) {
                return PostViewModel(mApplication, pref) as T
            } else {
                throw IllegalArgumentException("UserPreferences cannot be null for PostViewModel")
            }
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            if (pref != null) {
                return MapsViewModel(mApplication, pref) as T
            } else {
                throw IllegalArgumentException("UserPreferences cannot be null for PostViewModel")
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}