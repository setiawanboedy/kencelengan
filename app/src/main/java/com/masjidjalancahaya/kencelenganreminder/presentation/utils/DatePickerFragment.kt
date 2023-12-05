package com.masjidjalancahaya.kencelenganreminder.presentation.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDate
import java.util.*

class DatePickerFragment(
    private val onResult: (LocalDate) -> Unit
) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var mListener: DialogDateListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as DialogDateListener?
    }

    override fun onDetach() {
        super.onDetach()
        if (mListener != null) {
            mListener = null
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        // Tahun hari ini
        val year = calendar.get(Calendar.YEAR)

        // Bulan hari ini
        val month = calendar.get(Calendar.MONTH)

        // Tanggal hari ini
        val date = calendar.get(Calendar.DATE)

        return DatePickerDialog(activity as Context, this, year, month, date)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        mListener?.onDialogDateSet(tag, year, month, dayOfMonth)
        val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
        onResult(selectedDate)
    }

    interface DialogDateListener {
        fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int)
    }

}