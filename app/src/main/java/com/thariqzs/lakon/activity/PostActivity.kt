package com.thariqzs.lakon.activity

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View.INVISIBLE
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.instagramclone.createCustomTempFile
import com.example.instagramclone.reduceFileImage
import com.example.instagramclone.rotateFile
import com.example.instagramclone.uriToFile
import com.google.android.material.snackbar.Snackbar
import com.thariqzs.lakon.R
import com.thariqzs.lakon.ViewModelFactory
import com.thariqzs.lakon.databinding.ActivityPostBinding
import com.thariqzs.lakon.helper.Event
import com.thariqzs.lakon.preference.UserPreferences
import com.thariqzs.lakon.viewmodel.PostViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userPreference")

class PostActivity : AppCompatActivity() {
    private lateinit var currentPhotoPath: String
    private lateinit var postViewModel: PostViewModel
    private lateinit var pref: UserPreferences

    private var getFile: File? = null
    private var _binding: ActivityPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        _binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = UserPreferences.getInstance(dataStore)

        postViewModel = obtainViewModel(this)

        postViewModel.userResponseDetail.observe(this) {
            val token = it.token
            if (token != null) {
                if (token.isNotEmpty()) {
                    binding.btnUpload.setOnClickListener {
                        postStory(token)
                    }
                }
            }
        }
        postViewModel.errorMsg.observe(this) {
            setErrorMessage(it)
        }

        binding.btnCamera.setOnClickListener {
            startTakePhoto()
        }
        binding.btnCameraX.setOnClickListener {
            startCameraX()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        binding.icBack.setOnClickListener {
            finish()
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_a_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@PostActivity,
                "com.thariqzs.lakon",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == MainActivity.CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra(getString(R.string.picture), File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra(getString(R.string.picture))
            } as? File
            val isBackCamera = it.data?.getBooleanExtra(getString(R.string.isbackcamera), true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.ivPreviewImage.setImageBitmap(BitmapFactory.decodeFile(file.path))
                binding.vPreviewImage.visibility = INVISIBLE
                binding.ivImagePlaceholder.visibility = INVISIBLE
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@PostActivity)
                getFile = myFile
                binding.ivPreviewImage.setImageURI(uri)
                binding.vPreviewImage.visibility = INVISIBLE
                binding.ivImagePlaceholder.visibility = INVISIBLE
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                rotateFile(file)
                getFile = file
                binding.ivPreviewImage.setImageBitmap(BitmapFactory.decodeFile(file.path))
                binding.vPreviewImage.visibility = INVISIBLE
                binding.ivImagePlaceholder.visibility = INVISIBLE
            }
        }
    }

    private fun postStory(token : String) {
        if (getFile != null && binding.etDescription.text.toString().isNotEmpty()) {
            val file = reduceFileImage(getFile as File)
            val description = binding.etDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            postViewModel.postStory(token, description, imageMultipart)

            finish()
        } else {
            val warnMessage = if (getFile == null && binding.etDescription.text.toString().isNullOrEmpty()) {
                getString(R.string.please_provide_an_image_and_the_description)
            } else if (binding.etDescription.text.toString().isNullOrEmpty()) {
                getString(R.string.please_provide_a_description)
            } else {
                getString(R.string.please_provide_an_image)
            }
            Toast.makeText(this@PostActivity, warnMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtainViewModel(activity: PostActivity): PostViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, pref)
        return ViewModelProvider(activity, factory)[PostViewModel::class.java]
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
}