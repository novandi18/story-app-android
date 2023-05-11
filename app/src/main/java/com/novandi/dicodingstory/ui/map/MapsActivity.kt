package com.novandi.dicodingstory.ui.map

import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.novandi.dicodingstory.R
import com.novandi.dicodingstory.databinding.ActivityMapsBinding
import com.novandi.dicodingstory.storage.LoginModel
import com.novandi.dicodingstory.storage.LoginPreference

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnInfoWindowClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mLoginPreference: LoginPreference
    private lateinit var loginModel: LoginModel
    private val boundsBuilder = LatLngBounds.Builder()
    private val mapsViewModel by viewModels<MapsViewModel>()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreference = LoginPreference(this)
        loginModel = mLoginPreference.getUserLogin()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.setOnInfoWindowClickListener(this)

        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        getMyLocation()
        setMapStyle()
        addStoryMarker()
    }

    override fun onInfoWindowClick(marker: Marker) {
        Log.d(TAG, marker.title.toString())
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun addStoryMarker() {
        mapsViewModel.getStoriesToMaps(loginModel.token.toString())
        mapsViewModel.stories.observe(this@MapsActivity) { stories ->
            mMap.clear()
            stories.forEach { story ->
                val latLng = LatLng(story.lat!!, story.lon!!)
                mMap.addMarker(MarkerOptions().position(latLng).title(story.name))
                boundsBuilder.include(latLng)
            }

            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    100
                )
            )
        }

        mapsViewModel.message.observe(this@MapsActivity) {
            it.getContentIfNotHandled()?.let { text ->
                if (text != "") Toast.makeText(this@MapsActivity, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}