package com.masjidjalancahaya.kencelenganreminder.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.masjidjalancahaya.kencelenganreminder.R
import com.masjidjalancahaya.kencelenganreminder.databinding.ActivityAddBinding
import com.masjidjalancahaya.kencelenganreminder.databinding.ActivityMapResultBinding
import com.masjidjalancahaya.kencelenganreminder.utils.WindowMapAdapter
import com.masjidjalancahaya.kencelenganreminder.utils.convertLatLngToAddress
import timber.log.Timber

class MapResultActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapResultBinding
    private lateinit var mMap: GoogleMap
    private var currentMarker: Marker? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMap()
        setClickAndView()
    }

    private fun setClickAndView(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        binding.myLocation.setOnClickListener {

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
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
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBBSm8Dk3peSVXFiXbBPn3P2kiGZF-55yk") // Replace with your API key
        }
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
}