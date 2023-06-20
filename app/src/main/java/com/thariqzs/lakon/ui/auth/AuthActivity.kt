package com.thariqzs.lakon.ui.auth

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.thariqzs.lakon.R
import com.thariqzs.lakon.databinding.ActivityAuthBinding
import com.thariqzs.lakon.factory.ViewModelFactory
import com.thariqzs.lakon.preference.UserPreferences
import com.thariqzs.lakon.ui.login.LoginFragment
import com.thariqzs.lakon.ui.register.RegisterFragment

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
                binding.tvLoginRegisterSwitchFragment.text = getString(R.string.login)
            } else if (currentText == getString(R.string.login)) {
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
        val factory = ViewModelFactory.getInstance(activity.application, pref, this@AuthActivity)
        return ViewModelProvider(activity, factory)[AuthViewModel::class.java]
    }

}