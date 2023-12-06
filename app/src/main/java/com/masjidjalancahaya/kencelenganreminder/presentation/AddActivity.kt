package com.masjidjalancahaya.kencelenganreminder.presentation

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.masjidjalancahaya.kencelenganreminder.R
import com.masjidjalancahaya.kencelenganreminder.databinding.ActivityAddBinding
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import com.masjidjalancahaya.kencelenganreminder.model.LatLang
import com.masjidjalancahaya.kencelenganreminder.presentation.utils.DatePickerFragment
import com.masjidjalancahaya.kencelenganreminder.presentation.utils.TimePickerFragment
import com.masjidjalancahaya.kencelenganreminder.utils.DateTimeConversion
import com.masjidjalancahaya.kencelenganreminder.utils.convertLatLngToAddress
import com.masjidjalancahaya.kencelenganreminder.utils.dateTimeDoubleToDateString
import com.masjidjalancahaya.kencelenganreminder.utils.dateTimeDoubleToTimeString
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class AddActivity : AppCompatActivity(),
    DatePickerFragment.DialogDateListener,
    TimePickerFragment.DialogTimeListener {


    companion object{
        const val DATA_KEY = "send_data"
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
    }

    @Inject
    lateinit var dateTimeConversion: DateTimeConversion

    private var latLang: LatLang? = null
    private var localTime: LocalTime? = null
    private var localDate: LocalDate? = null
    private var isBlue: Boolean? = false
    private lateinit var binding: ActivityAddBinding
    private val viewModel: KencelenganViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == MapResultActivity.RESULT_CODE && result.data != null) {
            val location = if (Build.VERSION.SDK_INT >= 33) {
                result.data?.getParcelableExtra(MapResultActivity.EXTRA_SELECTED_VALUE, LatLang::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra(MapResultActivity.EXTRA_SELECTED_VALUE)
            }
            if (location != null){
                val latLng = LatLng(location.lat, location.lang)
                latLang = location
                val address = latLng.convertLatLngToAddress(this)
                binding.locationAddress.text = address
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
            val latLng = LatLng(kencel.lat!!, kencel.lang!!)
            val location = LatLang(kencel.lat!!, kencel.lang!!)
            val localDateTime = dateTimeConversion.zonedEpochMilliToLocalDateTime(kencel.startDateAndTime!!)
            localDate = localDateTime.toLocalDate()
            localTime = localDateTime.toLocalTime()
            viewModel.setStartDate(localDate!!)
            viewModel.setStartTime(localTime!!)
            latLang = location

            binding.edtName.setText(kencel.name)
            binding.edtTelp.setText(kencel.nomor.toString())
            binding.edtAddress.setText(kencel.address)
            binding.btnSwitch.isChecked = kencel.isBlue!!
            binding.locationAddress.text = latLng.convertLatLngToAddress(this)
            binding.tvDate.text = localDateTime.dateTimeDoubleToDateString()
            binding.tvTime.text = localDateTime.dateTimeDoubleToTimeString()

            binding.btnUpdate.visibility = View.VISIBLE
            binding.btnAdd.visibility = View.INVISIBLE
            binding.btnUpdate.setOnClickListener {

                updateKencel(kencel)
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

        }else
            Snackbar.make(binding.root, "Update data gagal", Snackbar.LENGTH_LONG).show()
    }

    private fun updateKencel(kencelenganModel: KencelenganModel){
        val validationForm = validationForm()
        if (!validationForm){
            val kencel = KencelenganModel(
                id = kencelenganModel.id,
                name = binding.edtName.text.toString(),
                nomor = binding.edtTelp.text.toString().toLong(),
                address = binding.edtAddress.text.toString(),
                isBlue = isBlue,
                startDateAndTime = kencelenganModel.startDateAndTime,
                lat = latLang!!.lat,
                lang = latLang!!.lang
            )

            viewModel.updateKencelengan(kencel)
        }
    }
    private fun createKencel(){
        val validationForm = validationForm()
        if (!validationForm){
            val kencel = KencelenganModel(
                name = binding.edtName.text.toString(),
                nomor = binding.edtTelp.text.toString().toLong(),
                address = binding.edtAddress.text.toString(),
                lat = latLang!!.lat,
                lang = latLang!!.lang
            )

            viewModel.createKencelengan(kencel)
        }
    }

    private fun validationForm(): Boolean{
        var isEmptyFields = false
        binding.apply {
            val inputName = edtName.text.toString().trim()
            val inputNomer = edtTelp.text.toString().trim()
            val inputAddress = edtAddress.text.toString().trim()


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
            if (latLang == null){
                isEmptyFields = true
                Snackbar.make(binding.root, "Pilih alamat lebih dahulu", Snackbar.LENGTH_LONG).show()
            }
            if (localDate == null){
                isEmptyFields = true
                Snackbar.make(binding.root, "Atur tanggal dulu", Snackbar.LENGTH_LONG).show()
            }
            if (localTime == null){
                isEmptyFields = true
                Snackbar.make(binding.root, "Atur waktu dulu", Snackbar.LENGTH_LONG).show()
            }
        }

        return isEmptyFields
    }

    private fun resetForm(){
        binding.edtName.text?.clear()
        binding.edtTelp.text?.clear()
        binding.edtAddress.text?.clear()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupViewsAndOnClickListeners(){
        binding.btnAdd.setOnClickListener {
            createKencel()
        }

        binding.btnBack.setOnClickListener {
            val moveIntent = Intent(this@AddActivity, MainActivity::class.java)
            startActivity(moveIntent)
        }

        binding.ibLocation.setOnClickListener {
            val moveForResultIntent = Intent(this@AddActivity, MapResultActivity::class.java)
            resultLauncher.launch(moveForResultIntent)
        }

        binding.pickDate.setOnClickListener {
            val datePickerFragment = DatePickerFragment { date ->
                localDate = date
                viewModel.setStartDate(date)

            }
            datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
        }
        binding.pickTime.setOnClickListener {
            val timePickerFragmentOne = TimePickerFragment{ time ->
                localTime = time
                viewModel.setStartTime(time)
            }
            timePickerFragmentOne.show(supportFragmentManager, TIME_PICKER_ONCE_TAG)
        }

        binding.btnSwitch.setOnCheckedChangeListener { _, isChecked ->
            isBlue = isChecked
        }
    }

    override fun onOptionsItemSelected( item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val moveIntent = Intent(this@AddActivity, MainActivity::class.java)
                startActivity(moveIntent)
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