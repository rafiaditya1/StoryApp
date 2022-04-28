package com.bangkit.storyapp.ui.upload

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.model.UploadResponse
import com.bangkit.storyapp.data.model.UserLogin
import com.bangkit.storyapp.data.networking.ApiConfig
import com.bangkit.storyapp.data.preference.SettingPreference
import com.bangkit.storyapp.databinding.ActivityUploadStoryBinding
import com.bangkit.storyapp.ui.home.MainActivity
import com.bangkit.storyapp.ui.home.dataStore
import com.bangkit.storyapp.utils.ApiCallbackString
import com.bangkit.storyapp.utils.rotateBitmap
import com.bangkit.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class UploadStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadStoryBinding
    private var getFile: File? = null
    private lateinit var user: UserLogin
    private lateinit var viewModel: UploadViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locations: Location

//    private val viewModel by viewModels<UploadViewModel>()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                }
            }
        }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    locations = location
                } else {
                    Toast.makeText(
                        this@UploadStoryActivity,
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

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, UploadViewModelFactory(SettingPreference.getInstance(dataStore)))[UploadViewModel::class.java]

//        user = intent.getParcelableExtra(EXTRA_USER)!!

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()
        binding.btnCamera.setOnClickListener { startCameraX() }
        binding.btnGaleri.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }

    }

    private fun uploadImage() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()
        showLoading(true)
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description = binding.edtDescription.text.toString().toRequestBody("text/plain".toMediaType())
//            val description = binding.edtDescription.text.toString()
            val lat = locations.latitude.toFloat()
            val long = locations.longitude.toFloat()
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

//            viewModel.uploadImage(user, description, imageMultipart, lat, long, object : ApiCallbackString {
//                override fun onResponse(success: Boolean, message: String) {
//                    showAlertDialog(success, message)
//                }
//
//            })

//            viewModel.getUserToken.observe(
//                this
//            ) { token: String ->
//                val service = ApiConfig.getApiService().storyUpload("Bearer $token", imageMultipart, description, lat, long)
//                showLoading(true)
//                service.enqueue(object : Callback<UploadResponse> {
//                    override fun onResponse(
//                        call: Call<UploadResponse>,
//                        response: Response<UploadResponse>,
//                    ) {
//                        if (response.isSuccessful) {
//                            showLoading(false)
//                            val responseBody = response.body()
//                            if (responseBody != null && !responseBody.error) {
//                                Toast.makeText(this@UploadStoryActivity,
//                                    responseBody.message,
//                                    Toast.LENGTH_SHORT).show()
//                                val intent =
//                                    Intent(this@UploadStoryActivity, MainActivity::class.java)
//                                startActivity(intent)
//                                finish()
//                            } else {
//                                showLoading(false)
//                                Toast.makeText(
//                                    this@UploadStoryActivity,
//                                    response.message(),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    }
//
//                    override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
//                        showLoading(false)
//                        Toast.makeText(this@UploadStoryActivity, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
//                        Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
//                    }
//
//                })
//            }

            viewModel.getUser().observe(this) {
                if (it != null) {
                    val service = ApiConfig.getApiService().storyUpload("Bearer " + it.token, imageMultipart, description, lat, long)
                    service.enqueue(object : Callback<UploadResponse> {
                        override fun onResponse(
                            call: Call<UploadResponse>,
                            response: Response<UploadResponse>,
                        ) {
                            showLoading(false)
                            val responseBody = response.body()
                            if (response.isSuccessful && responseBody?.message == "Story created successfully") {
                                Toast.makeText(this@UploadStoryActivity,
                                    responseBody.message,
                                    Toast.LENGTH_SHORT).show()
                                Log.e(ContentValues.TAG, "onResponse: ${response.message()}")
                                Toast.makeText(this@UploadStoryActivity, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
                                val intent =
                                    Intent(this@UploadStoryActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@UploadStoryActivity, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
                                Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                                Toast.makeText(this@UploadStoryActivity,
                                    response.message(),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                            showLoading(false)
                            Toast.makeText(this@UploadStoryActivity, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
                            Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
                        }

                    })
                }
            }

        } else {
            Toast.makeText(this@UploadStoryActivity, getString(R.string.please_insert_image),Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAlertDialog(param: Boolean, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.NAME, user)
        if (param) {
            AlertDialog.Builder(this).apply {
                setTitle("information")
                setMessage("Upload Success")
                setPositiveButton("continue") { _, _ ->
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle("information")
                setMessage(getString(R.string.upload_failed) + ", $message")
                setPositiveButton("continue") { _, _ ->
                    binding.progressBar.visibility = View.GONE
                }
                create()
                show()
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture")
        launcherIntentGalley.launch(chooser)
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )
            binding.ivPreview.setImageBitmap(result)
        }
    }


    private val launcherIntentGalley = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@UploadStoryActivity)
            getFile = myFile

            binding.ivPreview.setImageURI(selectedImg)
        }
    }

    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        const val EXTRA_USER = "user"

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}