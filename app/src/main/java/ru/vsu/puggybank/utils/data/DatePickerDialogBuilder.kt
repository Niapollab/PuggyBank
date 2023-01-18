package ru.vsu.puggybank.utils.data

import android.app.DatePickerDialog
import android.content.Context
import java.time.LocalDate

class DatePickerDialogBuilder {
    companion object {
        fun build(context: Context, startDate: LocalDate, listener: (LocalDate) -> Unit): DatePickerDialog {
            return DatePickerDialog(context, { _, year, month, day ->
                listener(LocalDate.of(year, month, day))
            }, startDate.year, startDate.monthValue - 1 , startDate.dayOfMonth)
        }
    }
}