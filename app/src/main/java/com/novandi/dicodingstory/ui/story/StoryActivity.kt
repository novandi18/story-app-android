package com.novandi.dicodingstory.ui.story

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.novandi.dicodingstory.R
import com.novandi.dicodingstory.databinding.ActivityStoryBinding
import com.novandi.dicodingstory.storage.LoginModel
import com.novandi.dicodingstory.storage.LoginPreference
import com.novandi.dicodingstory.ui.home.HomeActivity
import com.novandi.dicodingstory.utils.createCustomTempFile
import com.novandi.dicodingstory.utils.reduceFileImage
import com.novandi.dicodingstory.utils.rotateFile
import com.novandi.dicodingstory.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var mLoginPreference: LoginPreference
    private lateinit var loginModel: LoginModel
    private var getFile: File? = null
    private val storyViewModel by viewModels<StoryViewModel>()

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                rotateFile(file)
                getFile = file
                binding.storyImage.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage = result.data?.data as Uri
            selectedImage.let { uri ->
                val myFile = uriToFile(uri, this@StoryActivity)
                getFile = myFile
                binding.storyImage.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        mLoginPreference = LoginPreference(this)
        loginModel = mLoginPreference.getUserLogin()

        inputValidation()

        with(binding) {
            btnCamera.setOnClickListener { startCamera() }
            btnGallery.setOnClickListener { startGallery() }
            buttonAdd.setOnClickListener { uploadStory() }
            appBar.setNavigationOnClickListener { finish() }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.error_camera_permission), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun inputValidation() {
        binding.edAddDescription.doOnTextChanged { text, _, _, _ ->
            binding.edAddDescriptionLayout.error = if (text.toString().isEmpty()) getString(R.string.error_description_empty) else null
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(this@StoryActivity, "com.novandi.dicodingstory", it)
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.gallery_title))
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadStory() {
        val description = binding.edAddDescription.text.toString()

        if (getFile != null && description.isNotEmpty()) {
            val file = reduceFileImage(getFile as File)
            val requestDescription = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)

            storyViewModel.uploadStory(imageMultipart, requestDescription, loginModel.token.toString())
            storyViewModel.isLoading.observe(this) { showLoading(it) }
            storyViewModel.snackbarText.observe(this) {
                it.getContentIfNotHandled()?.let { text ->
                    if (text == "") {
                        val intent = Intent(this@StoryActivity, HomeActivity::class.java)
                        intent.putExtra(EXTRA_MESSAGE, getString(R.string.success_story_create))
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    } else {
                        Snackbar.make(window.decorView.rootView, text, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            if (description.isEmpty()) binding.edAddDescriptionLayout.error = getString(R.string.error_description_empty)
            if (getFile == null)
                Toast.makeText(this@StoryActivity, getString(R.string.error_image_null), Toast.LENGTH_SHORT).show()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
            btnCamera.isEnabled = !isLoading
            btnGallery.isEnabled = !isLoading
            buttonAdd.isEnabled = !isLoading
            edAddDescriptionLayout.isEnabled = !isLoading
        }
    }

    companion object {
        const val EXTRA_MESSAGE = "extra_message"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}