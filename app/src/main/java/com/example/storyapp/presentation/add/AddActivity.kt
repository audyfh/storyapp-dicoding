package com.example.storyapp.presentation.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil3.load
import coil3.request.crossfade
import com.example.storyapp.MainActivity
import com.example.storyapp.databinding.ActivityAddBinding
import com.example.storyapp.util.Utility.reduceFileImage
import com.example.storyapp.util.Utility.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddActivity : AppCompatActivity() {

    private var imageFile: File? = null
    private lateinit var binding: ActivityAddBinding
    private val viewModel: AddViewModel by viewModels()

    private var currentLat: Double? = null
    private var currentLong: Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val file = uriToFile(uri, this)
                imageFile = reduceFileImage(file)
                binding.ivPreview.load(file) {
                    crossfade(true)
                }
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageFile = reduceFileImage(photoFile)
                binding.ivPreview.load(imageFile) {
                    crossfade(true)
                }
            }
        }

    private lateinit var photoUri: Uri
    private lateinit var photoFile: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setClickListener()
        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.addUiState.collect { uiState ->
                        binding.apply {
                            progressBar.isVisible = uiState.isLoading
                        }
                        if (uiState.isSuccess) {
                            Toast.makeText(this@AddActivity, "Story Uploaded!", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this@AddActivity, MainActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                        }
                        if (uiState.isError) {
                            Toast.makeText(
                                this@AddActivity,
                                uiState.errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun setClickListener() {
        binding.apply {
            btnGallery.setOnClickListener {
                galleryLauncher.launch("image/*")
            }

            btnCamera.setOnClickListener {
                val timeStamp: String =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                photoFile = File.createTempFile(
                    "IMG_$timeStamp",
                    ".jpg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                )
                photoUri = FileProvider.getUriForFile(
                    this@AddActivity,
                    "${packageName}.provider",
                    photoFile
                )
                cameraLauncher.launch(photoUri)
            }

            btnUpload.setOnClickListener {
                val desc = etDescription.text.toString()
                val file = imageFile
                if (file != null && desc.isNotEmpty()) {
                    viewModel.addStory(
                        file = file,
                        desc = desc,
                        lat = currentLat?.toFloat(),
                        lon = currentLong?.toFloat()
                    )
                } else {
                    Toast.makeText(this@AddActivity, "Masukkan semua data", Toast.LENGTH_SHORT)
                        .show()
                }

            }

            switchLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    progressBar.isVisible = true
                    btnUpload.isEnabled = false
                    getCurrentLocation()
                    progressBar.isVisible = false
                    btnUpload.isEnabled = true
                } else {
                    currentLat = null
                    currentLong = null
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getCurrentLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getCurrentLocation()
                }

                else -> {
                    Toast.makeText(
                        this,
                        "Location is needed",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.switchLocation.isChecked = false
                }
            }
        }


    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLat = location.latitude
                    currentLong = location.longitude
                } else {
                    Toast.makeText(
                        this@AddActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @VisibleForTesting
    fun setDummyImageFile(file: File) {
        this.imageFile = file
    }
}