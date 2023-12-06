package com.masjidjalancahaya.kencelenganreminder.presentation

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.masjidjalancahaya.kencelenganreminder.R
import com.masjidjalancahaya.kencelenganreminder.adapter.KencelenganAdapter
import com.masjidjalancahaya.kencelenganreminder.core.ResourceState
import com.masjidjalancahaya.kencelenganreminder.databinding.ActivityMainBinding
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import com.masjidjalancahaya.kencelenganreminder.utils.DateTimeConversion
import com.masjidjalancahaya.kencelenganreminder.utils.OnItemAdapterListener
import com.masjidjalancahaya.kencelenganreminder.utils.dateTimeDoubleToDateString
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnItemAdapterListener {


    private lateinit var binding: ActivityMainBinding
    private val viewModel: KencelenganViewModel by viewModels()
    private lateinit var kencelengAdapter: KencelenganAdapter

    @Inject
    lateinit var dateTimeConversion: DateTimeConversion

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission rejected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        viewModel.getKencelengans()
        initDataKencel()
        setupViewAndClick()
        initRecyclerView()

    }
    private fun setupViewAndClick(){
        binding.floatingActionButton.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(moveIntent)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getKencelengans()

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

        viewModel.isDeleteKencelengan.observe(this){isDelete ->
            when(isDelete){
                is ResourceState.Loading -> {
                }
                is ResourceState.Success ->{
                    onDeleteKencel(isDelete.data ?: false)
                    viewModel.getKencelengans()
                }
                else -> {
                }
            }

        }

    }

    private fun onGetListKencelengans(items: List<KencelenganModel>){

        kencelengAdapter.submitList(items)
    }

    private fun initRecyclerView(){
        kencelengAdapter = KencelenganAdapter(this,this)
        binding.rvList.apply {
            adapter = kencelengAdapter
            layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            val moveIntent = Intent(this@MainActivity, SettingActivity::class.java)
            startActivity(moveIntent)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
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

    override fun onLongPressedClick(item: KencelenganModel) {
        showDeleteDialog(item.id)
    }

    private fun showDeleteDialog(id: String?) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Hapus Kencelengan")
        builder.setMessage("Apakah Anda yakin akan menghapus kencelengan ini?")

        // Set up the delete button
        builder.setPositiveButton("Hapus") { _, _ ->
            if (id != null){
                viewModel.deleteKencelengan(id)
            }
        }

        // Set up the cancel button
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        // Create and show the dialog
        builder.create().show()
    }

    private fun onDeleteKencel(isDelete: Boolean){
        if (isDelete) {
            Snackbar.make(binding.root, "Hapus data berhasil", Snackbar.LENGTH_LONG).show()
        }else
            Snackbar.make(binding.root, "Hapus data gagal", Snackbar.LENGTH_LONG).show()
    }

}