package com.thariqzs.lakon.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.thariqzs.lakon.R
import com.thariqzs.lakon.api.ListStoryItem
import com.thariqzs.lakon.databinding.ActivityDetailBinding
import com.thariqzs.lakon.databinding.ActivityMainBinding

class DetailActivity : AppCompatActivity() {
    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val details = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(PARCELIZED_DETAILS, ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(PARCELIZED_DETAILS)
        }

        Log.d(TAG, "onCreate: ${details?.name}")
    }

    companion object {
        const val PARCELIZED_DETAILS = "story_details"
        const val TAG = "dathoriq"
    }
}