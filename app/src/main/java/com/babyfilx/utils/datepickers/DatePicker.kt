package com.babyfilx.utils.datepickers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class DatePicker {

    companion object {

        var todayCalendar: Calendar = Calendar.getInstance()

        var selectedDay: Int = 0
        var selectedMonth: Int = 0
        var selectedYear: Int = 0
        var selectedHour: Int = 0
        var selectedMinute: Int = 0


        var selectedDOB = ""
        fun openDatePikerDialog(
            date: DatePickerDialog.OnDateSetListener,
            myCalendar: Calendar,
            context: Context

        ) {
            val mDatePicker = DatePickerDialog(
                context, date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            )

// myCalendar.add(Calendar.YEAR, -20)
// mDatePicker.datePicker.minDate = myCalendar.timeInMillis //  used to hide previous date,month and year

            val minCalendar: Calendar = Calendar.getInstance()
            minCalendar.add(Calendar.YEAR, -13)
            mDatePicker.datePicker.maxDate =
                minCalendar.timeInMillis //  used to hide future date,month and year

            todayCalendar = minCalendar


// mDatePicker.datePicker.maxDate = System.currentTimeMillis()
            mDatePicker.show()
        }

        @SuppressLint("SimpleDateFormat")
        fun Context.setPastDate(onClick: (String) -> Unit) {

            // val sdf = SimpleDateFormat("dd MMM EEE")
            val calendar = Calendar.getInstance()

            val y: Int
            val m: Int
            val d: Int

            if (selectedDay > 0) {
                y = selectedYear
                m = selectedMonth
                d = selectedDay
            } else {
                y = calendar.get(Calendar.YEAR)
                m = calendar.get(Calendar.MONTH)
                d = calendar.get(Calendar.DAY_OF_MONTH)
            }
            val datePickerDialog =
                DatePickerDialog(this, { _, year, month, day ->
                    val setCalendarValue: Calendar = Calendar.getInstance()
                    setCalendarValue.set(Calendar.YEAR, year)
                    setCalendarValue.set(Calendar.MONTH, month)
                    setCalendarValue.set(Calendar.DAY_OF_MONTH, day)

                    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                    val todayDateString = simpleDateFormat.format(todayCalendar.timeInMillis)
                    val selectDateString = simpleDateFormat.format(setCalendarValue.timeInMillis)
                    val todayDate: Date = simpleDateFormat.parse(todayDateString) as Date
                    val todayMillis = todayDate.time
                    val selectedDate: Date = simpleDateFormat.parse(selectDateString) as Date
                    val selectedMillis = selectedDate.time
                    if (selectedMillis <= todayMillis) {
                        selectedDay = day
                        selectedMonth = month
                        selectedYear = year
                        selectedDOB = "${getDays(day)}/${getDays(month + 1)}/$year"
                       onClick(selectedDOB)
                    }

                }, y, m, d)
            val minCalendar: Calendar = Calendar.getInstance()
            //minCalendar.add(Calendar.YEAR, -13)
            datePickerDialog.datePicker.maxDate =
                minCalendar.timeInMillis //  used to hide future date,month and year
            todayCalendar = minCalendar
            datePickerDialog.show()


        }


        fun getMonthName(month: Int): String {
            when (month) {
                1 -> return "Jan"
                2 -> return "Feb"
                3 -> return "Mar"
                4 -> return "Apr"
                5 -> return "May"
                6 -> return "Jun"
                7 -> return "Jul"
                8 -> return "Aug"
                9 -> return "Sep"
                10 -> return "Oct"
                11 -> return "Nov"
                12 -> return "Dec"
                else -> return ""


            }
        }

        private fun getDays(day: Int): String {
            return if (day < 10) {
                "0$day"
            } else {
                day.toString()
            }
        }

    }
}