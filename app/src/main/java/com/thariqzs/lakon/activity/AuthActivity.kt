package com.thariqzs.lakon.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.thariqzs.lakon.R
import com.thariqzs.lakon.ViewModelFactory
import com.thariqzs.lakon.databinding.ActivityAuthBinding
import com.thariqzs.lakon.helper.Event
import com.thariqzs.lakon.preference.UserPreferences
import com.thariqzs.lakon.ui.fragment.LoginFragment
import com.thariqzs.lakon.ui.fragment.RegisterFragment
import com.thariqzs.lakon.viewmodel.AuthViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userPreference")

class AuthActivity : AppCompatActivity() {
    private lateinit var pref: UserPreferences
    private lateinit var authViewModel: AuthViewModel

    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = UserPreferences.getInstance(dataStore)

        authViewModel = obtainViewModel(this)
        
        setupBindSwitchFragment()
    }

    private fun setupBindSwitchFragment() {
        binding.tvLoginRegisterSwitchFragment.setOnClickListener {
            val currentText = binding.tvLoginRegisterSwitchFragment.text
            if (currentText == getString(R.string.sign_up_here)) {
                binding.tvLoginRegisterNote.text = getString(R.string.already_have_an_account)
                binding.tvLoginRegisterSwitchFragment.text = getString(R.string.login)
            } else if (currentText == getString(R.string.login)) {
                binding.tvLoginRegisterNote.text = getString(R.string.don_t_have_an_account_yet)
                binding.tvLoginRegisterSwitchFragment.text = getString(R.string.sign_up_here)
            }

            val newFragment = when (currentText) {
                getString(R.string.sign_up_here) -> RegisterFragment()
                getString(R.string.login) -> LoginFragment()
                else -> LoginFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fcv_login_or_register, newFragment)
                .commit()
        }
    }

    private fun obtainViewModel(activity: AuthActivity): AuthViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, pref)
        return ViewModelProvider(activity, factory)[AuthViewModel::class.java]
    }

    companion object {
        val TAG = "aathoriq"
    }
}