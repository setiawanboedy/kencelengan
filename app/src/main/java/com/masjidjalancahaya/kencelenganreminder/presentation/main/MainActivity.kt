package com.masjidjalancahaya.kencelenganreminder.presentation.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.masjidjalancahaya.kencelenganreminder.R
import com.masjidjalancahaya.kencelenganreminder.adapter.KencelenganAdapter
import com.masjidjalancahaya.kencelenganreminder.core.ResourceState
import com.masjidjalancahaya.kencelenganreminder.databinding.ActivityMainBinding
import com.masjidjalancahaya.kencelenganreminder.data.model.KencelenganModel
import com.masjidjalancahaya.kencelenganreminder.presentation.add.AddActivity
import com.masjidjalancahaya.kencelenganreminder.presentation.auth.AuthActivity
import com.masjidjalancahaya.kencelenganreminder.presentation.auth.AuthState
import com.masjidjalancahaya.kencelenganreminder.presentation.auth.AuthViewModel
import com.masjidjalancahaya.kencelenganreminder.utils.conversion.DateTimeConversion
import com.masjidjalancahaya.kencelenganreminder.utils.OnItemAdapterListener
import com.masjidjalancahaya.kencelenganreminder.utils.helper.calculateDistance
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnItemAdapterListener {


    private lateinit var binding: ActivityMainBinding
    private val viewModel: KencelenganViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var kencelengAdapter: KencelenganAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatLng: LatLng? = null

    @Inject
    lateinit var dateTimeConversion: DateTimeConversion

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false ->{
                    Toast.makeText(this, "Location Granted", Toast.LENGTH_SHORT).show()
                }
                permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false -> {
                    Toast.makeText(this, "Notification Granted", Toast.LENGTH_SHORT).show()
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        permissionGranted()
        if (permissionGranted()){
            viewModel.swipeRefreshKencel()
        }else{
            viewModel.getKencelengans()
        }
        setupLogout()
        initDataKencel()
        setupViewAndClick()
        initRecyclerView()

    }
    private fun setupLogout(){
        authViewModel.authState.observe(this){ isAuth ->
            when(isAuth){
                is AuthState.Unauthenticated -> {
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is AuthState.Authenticated -> {}
                is AuthState.Error -> {
                    Toast.makeText(this, isAuth.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun permissionGranted(): Boolean{
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.POST_NOTIFICATIONS)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLatLng = LatLng(it.latitude, it.longitude)
                }
            }
            return true
        } else {

            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
            return false
        }


    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupViewAndClick(){
        binding.floatingActionButton.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(moveIntent)
        }

        binding.swipeRefresh.setOnRefreshListener {
            permissionGranted()
            viewModel.swipeRefreshKencel()
        }
    }
    private fun initDataKencel(){
        viewModel.allKencelengan.observe(this) {items->

            when(items){
                is ResourceState.Loading -> {
                    binding.pbLoading.visibility = View.VISIBLE
                }
                is ResourceState.Success ->{
                    items.data?.let { onGetListKencelengans(it) }
                    binding.pbLoading.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                }
                else -> {
                    binding.pbLoading.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false

                }
            }

        }

    }

    private fun onGetListKencelengans(items: List<KencelenganModel>){
        if (currentLatLng != null){

            val listBlue = items.filter { it.isBlue!! }
                .map { it.copy(distance = LatLng(it.lat!!, it.lang!!).calculateDistance(currentLatLng!!)) }
                .sortedBy { it.distance }
            val listGrey = items.filter {
                !it.isBlue!!
            }
            val itemsCombine = listBlue+listGrey
            kencelengAdapter.submitList(itemsCombine)
        }else{
            kencelengAdapter.submitList(items)
        }
    }

    private fun initRecyclerView(){
        kencelengAdapter = KencelenganAdapter(this,this)
        binding.rvList.apply {
            adapter = kencelengAdapter
            layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    override fun onPrimaryClick(item: KencelenganModel) {
        val moveWithObjectIntent = Intent(this@MainActivity, AddActivity::class.java)
        moveWithObjectIntent.putExtra(AddActivity.DATA_KEY, item)
        startActivity(moveWithObjectIntent)
    }

    override fun onSecondaryClick(item: KencelenganModel) {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${item.lat}, ${item.lang}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
    }

    override fun onLongPressedClick(item: KencelenganModel) {}


    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout Akun")
        builder.setMessage("Apakah Anda yakin akan logout ?")
        builder.setPositiveButton("Logout") { _, _ ->
            authViewModel.logout()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {

                return true
            }
            R.id.action_logout -> {
                showLogoutDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}