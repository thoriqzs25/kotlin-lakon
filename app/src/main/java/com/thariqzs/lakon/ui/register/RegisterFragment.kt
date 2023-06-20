package com.thariqzs.lakon.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.thariqzs.lakon.components.CustomEditText
import com.thariqzs.lakon.databinding.FragmentRegisterBinding
import com.thariqzs.lakon.factory.ViewModelFactory
import com.thariqzs.lakon.helper.Event
import com.thariqzs.lakon.ui.auth.AuthViewModel
import com.thariqzs.lakon.ui.main.MainActivity

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

        authViewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            setErrorMessage(msg)
        }
        authViewModel.userResponseDetail.observe(viewLifecycleOwner) {
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
        binding.cetName.setValidationType(CustomEditText.ValidationType.NAME)

        return binding.root
    }

    private fun setListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.btnRegister.isEnabled = checkAllFieldFilled()
            }
        }
        binding.cetName.addTextChangedListener(textWatcher)
        binding.cetEmail.addTextChangedListener(textWatcher)
        binding.cetPass.addTextChangedListener(textWatcher)
        binding.btnRegister.setOnClickListener {
            val name = binding.cetName.text.toString()
            val email = binding.cetEmail.text.toString()
            val password = binding.cetPass.text.toString()
            val nameValidation = binding.cetName.validate(name)
            val emailValidation = binding.cetEmail.validate(email)
            val passwordValidation = binding.cetPass.validate(password)

            if (emailValidation.isNullOrEmpty() && passwordValidation.isNullOrEmpty() && nameValidation.isNullOrEmpty()) {
                authViewModel.registerUser(name, email, password)
            } else {
                var errMsg: String = ""
                if (!nameValidation.isNullOrEmpty()) {
                    errMsg = nameValidation
                } else if (!emailValidation.isNullOrEmpty()) {
                    errMsg = emailValidation
                } else if (!passwordValidation.isNullOrEmpty()) {
                    errMsg = passwordValidation
                }
                setErrorMessage(Event(errMsg))
            }

        }
    }

    private fun obtainViewModel(fragment: Fragment): AuthViewModel {
        val factory = ViewModelFactory.getInstance(
            fragment.requireActivity().application,
            null,
            requireContext()
        )
        return ViewModelProvider(fragment, factory)[AuthViewModel::class.java]
    }

    private fun checkAllFieldFilled(): Boolean {
        val name = binding.cetName.text.toString()
        val email = binding.cetEmail.text.toString()
        val password = binding.cetPass.text.toString()
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
}