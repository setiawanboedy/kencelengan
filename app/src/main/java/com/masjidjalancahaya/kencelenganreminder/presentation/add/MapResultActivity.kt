package com.masjidjalancahaya.kencelenganreminder.presentation.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.masjidjalancahaya.kencelenganreminder.R
import com.masjidjalancahaya.kencelenganreminder.databinding.ActivityMapResultBinding
import com.masjidjalancahaya.kencelenganreminder.data.model.LatLang
import com.masjidjalancahaya.kencelenganreminder.utils.helper.WindowMapAdapter
import com.masjidjalancahaya.kencelenganreminder.utils.helper.convertLatLngToAddress
import timber.log.Timber

class MapResultActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapResultBinding
    private lateinit var mMap: GoogleMap
    private var currentMarker: Marker? = null
    private var latLang: LatLang? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMap()
        permissionLocationGranted()
        setClickAndView()
    }

    private fun permissionLocationGranted(){
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.myLocation.setOnClickListener {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    }
                }
            }
        }
    }
    private fun setClickAndView(){

        binding.pickLocation.setOnClickListener {
            val resultIntent = Intent()
            if (latLang != null){
                resultIntent.putExtra(EXTRA_SELECTED_VALUE, latLang)
                setResult(RESULT_CODE, resultIntent)
                finish()
            }

        }
    }

    private fun setupMap(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_donate) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.setInfoWindowAdapter(WindowMapAdapter(this))

        mMap.setOnMapClickListener { latLng ->
            setMarker(latLng)
        }

        getMyLocation()
        setupSearchAddress()
        initPosition()
    }

    private fun setMarker(latLng: LatLng){
        latLang = LatLang(latLng.latitude, latLng.longitude)
        val address = latLng.convertLatLngToAddress(this)
        currentMarker?.remove()
        currentMarker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Alamat")
                .snippet(address)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        )
        currentMarker?.showInfoWindow()
    }

    private fun setupSearchAddress(){

        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_search)
                as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {

            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
                if (latLng != null) {
                    setMarker(latLng)
                }
            }

            override fun onError(status: Status) {
                Timber.tag("search").d(status.toString())
            }
        })
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun initPosition(){
        val latLng = LatLng(-8.582929937664089, 116.10172080926952)
        val cameraPosition = CameraPosition.Builder()
            .target(latLng) // Set the center of the map to the LatLng
            .zoom(10f) // Set the zoom level (optional)
            .bearing(0f) // Orientation of the camera to east (optional)
            .tilt(30f) // Tilt of the camera (optional)
            .build()
        mMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                cameraPosition
            )
        )
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

    companion object {
        const val EXTRA_SELECTED_VALUE = "extra_selected_value"
        const val RESULT_CODE = 110
    }
}