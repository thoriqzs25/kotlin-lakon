package com.thariqzs.lakon.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramclone.rotateFile
import com.google.android.material.snackbar.Snackbar
import com.thariqzs.lakon.ViewModelFactory
import com.thariqzs.lakon.api.ListStoryItem
import com.thariqzs.lakon.databinding.ActivityMainBinding
import com.thariqzs.lakon.helper.Event
import com.thariqzs.lakon.preference.UserPreferences
import com.thariqzs.lakon.rv.StoryRvAdapter
import com.thariqzs.lakon.viewmodel.MainViewModel
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userPreference")

class MainActivity : AppCompatActivity() {
    private lateinit var pref: UserPreferences
    private lateinit var mainViewModel: MainViewModel

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        pref = UserPreferences.getInstance(dataStore)

        mainViewModel = obtainViewModel(this)

        mainViewModel.userDetail.observe(this) {
            if (it.name!!.isEmpty() || it.userId!!.isEmpty() || it.token!!.isEmpty()) {
                val intent = Intent(this@MainActivity, AuthActivity::class.java)
                startActivity(intent)
            }
            else
                mainViewModel.getStories(it.token)
        }
        mainViewModel.stories.observe(this) {
            setStoriesDataToAdapter(it)
        }
        mainViewModel.errorMsg.observe(this) {
            setErrorMessage(it)
        }

        binding.fabCreatePost.setOnClickListener {
            val intent = Intent(this@MainActivity, PostActivity::class.java)
            startActivity(intent)
        }
    }

    private fun obtainViewModel(activity: MainActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, pref)
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setStoriesDataToAdapter(stories: List<ListStoryItem>) {
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        val adapter = StoryRvAdapter(stories, onPressCard = {
            val intent = Intent(this@MainActivity, DetailActivity::class.java)
            intent.putExtra("story_details", it)
            startActivity(intent)
        },)
        binding.rvStories.adapter = adapter
    }

    private fun setErrorMessage(msg: Event<String>) {
        msg.getContentIfNotHandled()?.let {
            val snackbar = Snackbar.make(
                window.decorView.rootView,
                it,
                Snackbar.LENGTH_SHORT
            )
            snackbar.anchorView = binding.botView
            snackbar.show()
        }
    }

    companion object {
        val TAG = "mathoriq"

        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}