package com.masjidjalancahaya.kencelenganreminder.presentation

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.masjidjalancahaya.kencelenganreminder.R
import com.masjidjalancahaya.kencelenganreminder.databinding.ActivityAddBinding
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import com.masjidjalancahaya.kencelenganreminder.presentation.utils.DatePickerFragment
import com.masjidjalancahaya.kencelenganreminder.presentation.utils.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class AddActivity : AppCompatActivity(),
    DatePickerFragment.DialogDateListener,
    TimePickerFragment.DialogTimeListener
    {

    companion object{
        const val DATA_KEY = "send_data"
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
    }

    private lateinit var binding: ActivityAddBinding
    private val viewModel: KencelenganViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        getDataFromActivity()
        initDataProduct()

        setupViewsAndOnClickListeners()
    }


    private fun getDataFromActivity(){
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
        binding.apply {
            val inputName = edtName.text.toString().trim()
            val inputNomer = edtTelp.text.toString().trim()
            val inputAddress = edtAddress.text.toString().trim()

            var isEmptyFields = false
            if (inputName.isEmpty()) {
                isEmptyFields = true
                edtName.error = "Field ini tidak boleh kosong"
            }
            if (inputNomer.isEmpty()) {
                isEmptyFields = true
                edtTelp.error = "Field ini tidak boleh kosong"
            }
            if (inputAddress.isEmpty()) {
                isEmptyFields = true
                edtAddress.error = "Field ini tidak boleh kosong"
            }

            if (!isEmptyFields){
                val kencel = KencelenganModel(
                    name = binding.edtName.text.toString(),
                    nomor = binding.edtTelp.text.toString().toInt(),
                    address = binding.edtAddress.text.toString(),
                )

                viewModel.createKencelengan(kencel)
            }
        }
    }

    private fun resetForm(){
        binding.edtName.text?.clear()
        binding.edtTelp.text?.clear()
        binding.edtAddress.text?.clear()
    }

    private fun setupViewsAndOnClickListeners(){
        binding.btnAdd.setOnClickListener {
            createKencel()
        }

        binding.btnBack.setOnClickListener {
            val moveIntent = Intent(this@AddActivity, MainActivity::class.java)
            startActivity(moveIntent)
        }

        binding.ibLocation.setOnClickListener {
            val moveIntent = Intent(this@AddActivity, MapResultActivity::class.java)
            startActivity(moveIntent)
        }

        binding.pickDate.setOnClickListener {
            val datePickerFragment = DatePickerFragment(
                onResult = viewModel::setStartDate
            )
            datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
        }
        binding.pickTime.setOnClickListener {
            val timePickerFragmentOne = TimePickerFragment(
                onResult = viewModel::setStartTime
            )
            timePickerFragmentOne.show(supportFragmentManager, TIME_PICKER_ONCE_TAG)
        }
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

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        // Siapkan date formatter-nya terlebih dahulu
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        // Set text dari textview once
        binding.tvDate.text = dateFormat.format(calendar.time)
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.tvTime.text = dateFormat.format(calendar.time)
    }


}