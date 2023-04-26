package com.thariqzs.lakon.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.thariqzs.lakon.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setListeners()
    }

    private fun setListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed.
                Log.d(TAG, "onTextChanged: $s")
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed.
            }
        }
//        binding.etEmail.addTextChangedListener(textWatcher)
//        binding.etPassword.addTextChangedListener(textWatcher)
//        binding.btnSignIn.setOnClickListener {
//            Log.d(TAG, "onCreate: ${binding.etEmail.text} ${binding.etPassword.text}")
//        }
    }
    companion object {
        val TAG = "authactivitythoriq"
    }
}