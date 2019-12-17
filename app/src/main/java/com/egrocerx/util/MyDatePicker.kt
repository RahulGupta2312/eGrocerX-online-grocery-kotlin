package com.egrocerx.util

import android.app.DatePickerDialog
import android.widget.DatePicker
import com.egrocerx.core.MyApplication
import java.util.*

class TbmDatepicker(private val callback: OnDateSelectedCallback) :
    DatePickerDialog.OnDateSetListener {

    private val calendar: Calendar = Calendar.getInstance(Locale.getDefault())
    private var type: DATE_PICKER_TYPE? = null


    enum class DATE_PICKER_TYPE {
        START_DATE, END_DATE
    }

    fun launchDatePicker(type: DATE_PICKER_TYPE, minDate: Long, maxDate: Long) {
        this.type = type
        val datePicker = DatePickerDialog(
            MyApplication.instance.getContext(),
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        if (minDate > 0)
            datePicker.datePicker.minDate = minDate
        if (maxDate > 0)
            datePicker.datePicker.maxDate = maxDate

        datePicker.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val mnth = month + 1
        val date =
            "$year-" + (if (mnth > 9) mnth else "0$mnth") + "-" + (if (dayOfMonth > 9) dayOfMonth else "0$dayOfMonth").toString()
        callback.onDateSelected(date, type)
    }

    interface OnDateSelectedCallback {
        fun onDateSelected(date: String, type: DATE_PICKER_TYPE?)
    }
}