package com.thariqzs.lakon.ui.detail

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.thariqzs.lakon.data.model.Story
import com.thariqzs.lakon.databinding.ActivityDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private val PARCELIZED_DETAILS = "story_details"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val details = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(PARCELIZED_DETAILS, Story::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(PARCELIZED_DETAILS)
        }

        if (details != null) {
            binding.apply {
                Glide.with(this@DetailActivity)
                    .load(details.photoUrl)
                    .centerCrop()
                    .into(ivDetail)
                tvDetailDate.text = formatDate(details.createdAt)
                tvDetailDesc.text = details.description
                tvDetailName.text = details.name
            }
        }
    }

    private fun formatDate(date: String): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

        val inputDate = inputDateFormat.parse(date)
        val outputDate = outputDateFormat.format(inputDate)
        return outputDate.uppercase()
    }
}