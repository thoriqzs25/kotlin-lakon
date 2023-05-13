package com.thariqzs.lakon.components

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.thariqzs.lakon.R
import java.util.regex.Pattern

class CustomEditText : AppCompatEditText, View.OnTouchListener {
    private val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    private var validationType = ValidationType.TEXT

    enum class ValidationType {
        EMAIL,
        PASSWORD,
        NAME,
        TEXT
    }

    constructor(context: Context): super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init ()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    private fun init() {
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable) {
                val input = s.toString().trim()
                if (validate(input)?.isNotEmpty() == true) {
                    setBackgroundResource(R.drawable.bg_custom_et_error)
                } else {
                    setBackgroundResource(R.drawable.bg_custom_et)
                }
            }
        })
    }

    fun setValidationType(type: ValidationType) {
        this.validationType = type
    }

    fun validate(input: String): String? {
        return when (validationType) {
            ValidationType.EMAIL -> {
                isEmailValid(input)
            }

            ValidationType.PASSWORD -> {
                isPasswordValid(input)
            }

            ValidationType.NAME -> {
                isNameValid(input)
            }
            else -> {
                null
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }

    private fun isEmailValid(email: String): String? {
        if (emailPattern.matcher(email).matches()) return null
        return context.getString(R.string.invalid_email_format)
    }

    private fun isPasswordValid(pass: String): String? {
        if (pass.length >= 8) return null
        return context.getString(R.string.password_must_be_at_least_8_characters)
    }

    private fun isNameValid(name: String): String? {
        if (name.isNotEmpty()) return null
        return context.getString(R.string.input_valid_naming)
    }
}