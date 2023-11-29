package com.masjidjalancahaya.kencelenganreminder.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.masjidjalancahaya.kencelenganreminder.R
import com.masjidjalancahaya.kencelenganreminder.databinding.ActivityAddBinding
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddActivity : AppCompatActivity() {

    companion object{
        const val DATA_KEY = "send_data"
    }

    private lateinit var binding: ActivityAddBinding
    private val viewModel: KencelenganViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)


        binding.btnAdd.setOnClickListener {
            createKencel()
        }

        binding.btnBack.setOnClickListener {
            val moveIntent = Intent(this@AddActivity, MainActivity::class.java)
            startActivity(moveIntent)
        }

        initDataProduct()

        val kencel = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(DATA_KEY, KencelenganModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(DATA_KEY)
        }
        if (kencel != null) {
            binding.edtName.setText(kencel.name)
            binding.edtTelp.setText(kencel.nomor.toString())
            binding.edtAddress.setText(kencel.address)

            binding.btnAdd.text = "Update"
            binding.btnAdd.setOnClickListener {
                updateKencel(kencel.id!!)
            }
        }


    }

    private fun initDataProduct(){
        viewModel.isCreateKencelengan.observe(this){
            it.data?.let { it1 -> onCreateKencel(it1) }
        }

        viewModel.isUpdateKencelengan.observe(this){
            it.data?.let { it1 -> onUpdateKencel(it1) }
        }

    }

    private fun onCreateKencel(isCreated: Boolean){
        if (isCreated) {
            Snackbar.make(binding.root, "Tambah data berhasil", Snackbar.LENGTH_LONG).show()
            resetForm()
        }else
            Snackbar.make(binding.root, "Tambah data gagal", Snackbar.LENGTH_LONG).show()
    }

    private fun onUpdateKencel(isUpdate: Boolean){
        if (isUpdate) {
            Snackbar.make(binding.root, "Update data berhasil", Snackbar.LENGTH_LONG).show()
            resetForm()
        }else
            Snackbar.make(binding.root, "Update data gagal", Snackbar.LENGTH_LONG).show()
    }

    private fun updateKencel(id: String){
        if (binding.edtName.text.toString().isNotEmpty() && binding.edtTelp.text.toString().isNotEmpty() && binding.edtAddress.text.toString().isNotEmpty()){
            val kencel = KencelenganModel(
                id = id,
                name = binding.edtName.text.toString(),
                nomor = binding.edtTelp.text.toString().toInt(),
                address = binding.edtAddress.text.toString(),
            )
            viewModel.updateKencelengan(kencel)
        }
    }
    private fun createKencel(){
        if (binding.edtName.text.toString().isNotEmpty() && binding.edtTelp.text.toString().isNotEmpty() && binding.edtAddress.text.toString().isNotEmpty()){
            val kencel = KencelenganModel(
                name = binding.edtName.text.toString(),
                nomor = binding.edtTelp.text.toString().toInt(),
                address = binding.edtAddress.text.toString(),
            )

            viewModel.createKencelengan(kencel)
        }
    }

    private fun resetForm(){
        binding.edtName.text?.clear()
        binding.edtTelp.text?.clear()
        binding.edtAddress.text?.clear()
    }
    override fun onOptionsItemSelected( item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}