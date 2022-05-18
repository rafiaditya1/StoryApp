package com.bangkit.storyapp.ui.map

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.model.UserLogin
import com.bangkit.storyapp.data.preference.SettingPreference
import com.bangkit.storyapp.databinding.ActivityMapBinding
import com.bangkit.storyapp.ui.home.ViewModelFactory
import com.bangkit.storyapp.ui.home.dataStore
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: MapViewModel
    private lateinit var userLogin: UserLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preference = SettingPreference.getInstance(dataStore)

        viewModel = ViewModelProvider(this, ViewModelFactory(preference, this))[MapViewModel::class.java]

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        viewModel.getUser().observe(this) {
            userLogin = UserLogin(
                it.name,
                it.email,
                it.password,
                it.userId,
                it.token,
                true
            )
            viewModel.getLocation(it.token)

        }
        viewModel.listStory.observe(this) {
            for (story in it) {
                if (story.lat != null && story.lon != null) {
                    val position = LatLng(story.lat, story.lon)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(story.name)
                            .snippet(story.description)
                    )
                }
            }
            val firstLocation = LatLng(it[0].lat as Double, it[0].lon as Double)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 15f))
        }

        getMyLocation()
        setMapStyle()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(ContentValues.TAG, "Style Parsing Failed.")
            }
        } catch (exeception: Resources.NotFoundException) {
            Log.e(ContentValues.TAG, "Can't Find Style. Error: ", exeception)
        }
    }
}