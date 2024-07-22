package com.masjidjalancahaya.kencelenganreminder.presentation

import android.app.AlertDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.masjidjalancahaya.kencelenganreminder.R
import com.masjidjalancahaya.kencelenganreminder.databinding.ActivityAddBinding
import com.masjidjalancahaya.kencelenganreminder.model.LatLang
import com.masjidjalancahaya.kencelenganreminder.presentation.utils.DatePickerFragment
import com.masjidjalancahaya.kencelenganreminder.presentation.utils.TimePickerFragment
import com.masjidjalancahaya.kencelenganreminder.utils.DateTimeConversion
import com.masjidjalancahaya.kencelenganreminder.utils.convertLatLngToAddress
import com.masjidjalancahaya.kencelenganreminder.utils.dateTimeDoubleToDateString
import com.masjidjalancahaya.kencelenganreminder.utils.dateTimeDoubleToTimeString
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class AddActivity : AppCompatActivity(),
    DatePickerFragment.DialogDateListener,
    TimePickerFragment.DialogTimeListener{


    companion object{
        const val DATA_KEY = "send_data"
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
    }

    @Inject
    lateinit var dateTimeConversion: DateTimeConversion

    private var itemId: String? = null
    private var isBlue: Boolean? = false
    private lateinit var binding: ActivityAddBinding
    private val viewModel: AddViewModel by viewModels()

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
                viewModel.setLatLng(latLng = latLng)
                val address = latLng.convertLatLngToAddress(this)
                if (address != null) {
                    binding.locationAddress.text = address
                    viewModel.setAddress(address = address)
                }
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

        viewInputForm()
        setupViewsAndOnClickListeners()
    }


    private fun viewInputForm(){
        viewModel.uiState.observe(this) { state ->
            state?.let {
                binding.btnAdd.isEnabled = it.isDataValid
            }
        }

        binding.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setDonateName(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.edtTelp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null){
                viewModel.setNoHp(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun setupViewsAndOnClickListeners(){
        viewModel.uiState.observe(this) { state ->
            if (state != null){
                itemId = state.id
                if (binding.edtName.text.toString() != state.donateName){
                    binding.edtName.setText(state.donateName)
                }
                if (binding.edtTelp.text.toString() != state.phone){
                    binding.edtTelp.setText(state.phone)
                }
                binding.tvDate.text = state.startDate?.dateTimeDoubleToDateString() ?: "Atur Tanggal"
                binding.tvTime.text = state.startTime?.dateTimeDoubleToTimeString() ?: "Atur Waktu"
                binding.locationAddress.text = state.latLng?.convertLatLngToAddress(this) ?: "Atur Lokasi"

                if (state.redirect){
                    redirectBack()
                }
            }
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
                viewModel.setStartDate(date)

            }
            datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
        }
        binding.pickTime.setOnClickListener {
            val timePickerFragmentOne = TimePickerFragment{ time ->
                viewModel.setStartTime(time)
            }
            timePickerFragmentOne.show(supportFragmentManager, TIME_PICKER_ONCE_TAG)
        }

        binding.btnAdd.setOnClickListener {
            viewModel.submit()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val shouldHideIcon = checkCondition()
        menu?.findItem(R.id.action_delete)?.isVisible = !shouldHideIcon
        return super.onPrepareOptionsMenu(menu)
    }

    private fun checkCondition(): Boolean {
        return itemId == null
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val moveIntent = Intent(this@AddActivity, MainActivity::class.java)
                startActivity(moveIntent)

                return true
            }
            R.id.action_delete -> {
                if (itemId != null){
                    showDeleteDialog(itemId)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
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

    private fun redirectBack(){
        val moveWithObjectIntent = Intent(this, MainActivity::class.java)
        startActivity(moveWithObjectIntent)
    }

    private fun showDeleteDialog(id: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Hapus Kencelengan")
        builder.setMessage("Apakah Anda yakin akan menghapus kencelengan ini?")
        builder.setPositiveButton("Hapus") { _, _ ->
            if (id != null){
                viewModel.deleteItem(id)
            }
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}