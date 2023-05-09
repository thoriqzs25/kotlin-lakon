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
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.thariqzs.lakon.R
import com.thariqzs.lakon.ViewModelFactory
import com.thariqzs.lakon.activity.AuthActivity
import com.thariqzs.lakon.activity.MainActivity
import com.thariqzs.lakon.components.CustomEditText
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

        authViewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            setErrorMessage(msg)
        }
        authViewModel.userDetail.observe(viewLifecycleOwner) {
            if (it.name!!.isNotEmpty() && it.userId!!.isNotEmpty() && it.token!!.isNotEmpty()) {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
        }
        authViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        binding.cetEmail.setValidationType(CustomEditText.ValidationType.EMAIL)
        binding.cetPass.setValidationType(CustomEditText.ValidationType.PASSWORD)

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
                binding.btnLogin.isEnabled = checkAllFieldFilled()
            }
        }

        binding.cetEmail.addTextChangedListener(textWatcher)
        binding.cetPass.addTextChangedListener(textWatcher)
        binding.btnLogin.setOnClickListener {
            val email = binding.cetEmail.text.toString()
            val password = binding.cetPass.text.toString()
            val emailValidation = binding.cetEmail.validate(email)
            val passwordValidation = binding.cetPass.validate(password)

            if (emailValidation.isNullOrEmpty() && passwordValidation.isNullOrEmpty()) {
                authViewModel.loginUser(email, password)
            } else {
                var errMsg: String = ""
                if (!emailValidation.isNullOrEmpty()) {
                    errMsg = emailValidation
                } else if (!passwordValidation.isNullOrEmpty()) {
                    errMsg = passwordValidation
                }
                setErrorMessage(Event(errMsg))
            }
        }
    }

    private fun obtainViewModel(fragment: Fragment): AuthViewModel {
        val factory = ViewModelFactory.getInstance(fragment.requireActivity().application)
        return ViewModelProvider(fragment, factory)[AuthViewModel::class.java]
    }

    private fun checkAllFieldFilled(): Boolean {
        val email = binding.cetEmail.text.toString()
        val password = binding.cetPass.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) return true
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
        val TAG = "lfthoriq"
    }
}