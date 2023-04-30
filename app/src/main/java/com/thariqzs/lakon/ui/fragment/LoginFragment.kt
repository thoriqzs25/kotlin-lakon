package com.thariqzs.lakon.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.thariqzs.lakon.R
import com.thariqzs.lakon.ViewModelFactory
import com.thariqzs.lakon.activity.AuthActivity
import com.thariqzs.lakon.activity.MainActivity
import com.thariqzs.lakon.databinding.FragmentLoginBinding
import com.thariqzs.lakon.helper.Event
import com.thariqzs.lakon.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var authViewModel: AuthViewModel

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        setListeners()

        authViewModel = obtainViewModel(this)

        authViewModel.errorMsg.observe(viewLifecycleOwner) {msg ->
            Log.d(TAG, "onCreate2: ${msg.peekContent()}")
        }
        authViewModel.userDetail.observe(viewLifecycleOwner) {
            if (it.name!!.isNotEmpty() && it.userId!!.isNotEmpty() && it.token!!.isNotEmpty()) {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
        }
        
        return binding.root
    }

    private fun setListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed.
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed.
                val isAllFilled = checkAllFieldFilled()
                binding.btnLogin.isEnabled = isAllFilled
            }
        }
        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            authViewModel.loginUser(email, password)
        }
    }

    private fun obtainViewModel(fragment: Fragment): AuthViewModel {
        val factory = ViewModelFactory.getInstance(fragment.requireActivity().application)
        return ViewModelProvider(fragment, factory)[AuthViewModel::class.java]
    }

    private fun checkAllFieldFilled(): Boolean {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) return true
        return false
    }

    companion object {
        val TAG = "lfthoriq"
    }
}