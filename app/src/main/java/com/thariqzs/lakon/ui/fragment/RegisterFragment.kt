package com.thariqzs.lakon.ui.fragment

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
import com.thariqzs.lakon.ViewModelFactory
import com.thariqzs.lakon.activity.AuthActivity
import com.thariqzs.lakon.databinding.FragmentRegisterBinding
import com.thariqzs.lakon.helper.Event
import com.thariqzs.lakon.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {
    private lateinit var authViewModel: AuthViewModel

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setListeners()

        authViewModel = obtainViewModel(this)

        authViewModel.errorMsg.observe(viewLifecycleOwner) {msg ->
            setErrorMessage(msg)
        }
        authViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
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
                binding.btnRegister.isEnabled = isAllFilled
            }
        }
        binding.etName.addTextChangedListener(textWatcher)
        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            authViewModel.registerUser(name, email, password)
            Log.d(
                TAG,
                "onCreate:  ${binding.etName.text} ${binding.etEmail.text} ${binding.etPassword.text}"
            )
        }
    }

    private fun obtainViewModel(fragment: Fragment): AuthViewModel {
        val factory = ViewModelFactory.getInstance(fragment.requireActivity().application)
        return ViewModelProvider(fragment, factory)[AuthViewModel::class.java]
    }

    private fun checkAllFieldFilled(): Boolean {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) return true
        return false
    }

    private fun setErrorMessage(msg: Event<String>) {
        msg.getContentIfNotHandled()?.let {
            val snackbar = Snackbar.make(
                requireActivity().window.decorView.rootView,
                it,
                Snackbar.LENGTH_SHORT
            )
            snackbar.anchorView = binding.botView
            snackbar.show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pbLoading.visibility = View.VISIBLE
        } else {
            binding.pbLoading.visibility = View.GONE
        }
    }
    
    companion object {
        val TAG = "rfthoriq"
    }
}