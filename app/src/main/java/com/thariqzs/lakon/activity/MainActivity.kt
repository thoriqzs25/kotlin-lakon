package com.thariqzs.lakon.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramclone.rotateFile
import com.google.android.material.snackbar.Snackbar
import com.thariqzs.lakon.R
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

    private val rotateOpenAnimation: Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.rotate_open_animation)}
    private val rotateCloseAnimation: Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.rotate_close_animation)}
    private val fromBottomAnimation: Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.from_bottom_animation)}
    private val toBottomAnimation: Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.to_bottom_animation)}

    private var addButtonClicked = false
    private var triggerOnRestart = true
    private var token = ""

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
            else {
                mainViewModel.getStories(it.token)
                token = it.token
            }
        }
        mainViewModel.stories.observe(this) {
            setStoriesDataToAdapter(it)
        }
        mainViewModel.errorMsg.observe(this) {
            setErrorMessage(it)
        }
        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.fabExpandOptions.setOnClickListener {
            onExpandFab()
        }
        binding.fabCreatePost.setOnClickListener {
            onExpandFab()
            val intent = Intent(this@MainActivity, PostActivity::class.java)
            startActivity(intent)
        }
        binding.fabLogout.setOnClickListener {
            onExpandFab()
            mainViewModel.logoutUser()
        }
        binding.srlMain.setOnRefreshListener {
            mainViewModel.getStories(token)
            Handler().postDelayed(Runnable {
                binding.srlMain.isRefreshing = false
            }, 750)
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (triggerOnRestart) {
            showLoading(true)
            Handler().postDelayed(Runnable {
            mainViewModel.getStories(token)
            }, 1500)
        }

        triggerOnRestart = true
    }

    override fun onBackPressed() {
    }
    
    private fun onExpandFab() {
        setVisibility(addButtonClicked)
        setAnimation(addButtonClicked)
        buttonSetClickable()

        addButtonClicked = !addButtonClicked
    }
    private fun setVisibility(buttonClicked: Boolean) {
        if (!buttonClicked){
            binding.fabCreatePost.visibility = VISIBLE
            binding.fabLogout.visibility = VISIBLE
        }else{
            binding.fabCreatePost.visibility = INVISIBLE
            binding.fabLogout.visibility = INVISIBLE
        }
    }

    private fun setAnimation(buttonClicked: Boolean) {
        if (!buttonClicked){
            binding.fabCreatePost.startAnimation(fromBottomAnimation)
            binding.fabLogout.startAnimation(fromBottomAnimation)
            binding.fabExpandOptions.startAnimation(rotateOpenAnimation)
        }else{
            binding.fabCreatePost.startAnimation(toBottomAnimation)
            binding.fabLogout.startAnimation(toBottomAnimation)
            binding.fabExpandOptions.startAnimation(rotateCloseAnimation)
        }
    }

    private fun buttonSetClickable() {
        if (!addButtonClicked){
            binding.fabCreatePost.isClickable = true
            binding.fabLogout.isClickable = true
        }else{
            binding.fabCreatePost.isClickable = false
            binding.fabLogout.isClickable = false
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
        val adapter = StoryRvAdapter(stories, onPressCard = { storyItem, holderView ->
            val intent = Intent(this@MainActivity, DetailActivity::class.java)
            intent.putExtra("story_details", storyItem)
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@MainActivity,
                    Pair(holderView,"image")
                )
            triggerOnRestart = false
            if (addButtonClicked) onExpandFab()
            startActivity(intent, optionsCompat.toBundle())
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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pbLoading.visibility = View.VISIBLE
        } else {
            binding.pbLoading.visibility = View.GONE
        }
    }

    companion object {
        val TAG = "mathoriq"

        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}