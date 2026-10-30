package com.example.util

import java.util.Calendar

data class JalaliDate(
    val year: Int,
    val month: Int, // 1 to 12
    val day: Int    // 1 to 31
) : Comparable<JalaliDate> {
    fun formatToString(): String {
        return String.format("%04d/%02d/%02d", year, month, day)
    }

    fun formatPersianFriendly(): String {
        val monthName = PersianDateUtils.getMonthName(month)
        return "$day $monthName $year"
    }

    override fun compareTo(other: JalaliDate): Int {
        if (this.year != other.year) return this.year.compareTo(other.year)
        if (this.month != other.month) return this.month.compareTo(other.month)
        return this.day.compareTo(other.day)
    }
}

object PersianDateUtils {

    val monthNames = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val weekDayNames = listOf(
        "ش", "ی", "د", "س", "چ", "پ", "ج"
    )

    val weekDayFullNames = listOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه"
    )

    fun getMonthName(month: Int): String {
        return if (month in 1..12) monthNames[month - 1] else ""
    }

    fun isLeapYear(year: Int): Boolean {
        // Jalali 33-year cycle leap year algorithm
        val r = year % 33
        return r == 1 || r == 5 || r == 9 || r == 13 || r == 17 || r == 22 || r == 26 || r == 30
    }

    fun getDaysInMonth(year: Int, month: Int): Int {
        return when {
            month in 1..6 -> 31
            month in 7..11 -> 30
            month == 12 -> if (isLeapYear(year)) 30 else 29
            else -> 30
        }
    }

    /**
     * Converts Gregorian (year, month, day) to JalaliDate.
     * Month in Gregorian here is 1-based (1..12).
     */
    fun gregorianToJalali(gYear: Int, gMonth: Int, gDay: Int): JalaliDate {
        val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val isGLeap = (gYear % 4 == 0 && gYear % 100 != 0) || (gYear % 400 == 0)
        if (isGLeap) gDaysInMonth[2] = 29

        var gy = gYear - 1600
        var gm = gMonth - 1
        var gd = gDay - 1

        var gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400
        for (i in 0 until gm) {
            gDayNo += gDaysInMonth[i + 1]
        }
        gDayNo += gd

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        var jd = 0
        if (jDayNo < 186) {
            jm = 1 + jDayNo / 31
            jd = 1 + (jDayNo % 31)
        } else {
            jm = 7 + (jDayNo - 186) / 30
            jd = 1 + ((jDayNo - 186) % 30)
        }

        return JalaliDate(jy, jm, jd)
    }

    /**
     * Converts JalaliDate to Gregorian calendar Date
     */
    fun jalaliToGregorian(jYear: Int, jMonth: Int, jDay: Int): Calendar {
        var jy = jYear - 979
        var jm = jMonth - 1
        var jd = jDay - 1

        var jDayNo = 365 * jy + (jy / 33) * 8 + ((jy % 33 + 3) / 4)
        for (i in 0 until jm) {
            jDayNo += if (i < 6) 31 else 30
        }
        jDayNo += jd

        var gDayNo = jDayNo + 79
        var gy = 1600 + 400 * (gDayNo / 146097)
        gDayNo %= 146097

        var leap = true
        if (gDayNo >= 36525) {
            gDayNo--
            gy += 100 * (gDayNo / 36524)
            gDayNo %= 36524
            if (gDayNo >= 365) {
                gDayNo++
            } else {
                leap = false
            }
        }

        gy += 4 * (gDayNo / 1461)
        gDayNo %= 1461

        if (gDayNo >= 366) {
            leap = false
            gDayNo--
            gy += gDayNo / 365
            gDayNo %= 365
        }

        val gDaysInMonth = intArrayOf(0, 31, if (leap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gm = 0
        while (gDayNo >= gDaysInMonth[gm + 1]) {
            gDayNo -= gDaysInMonth[gm + 1]
            gm++
        }
        val gd = gDayNo + 1

        val cal = Calendar.getInstance()
        cal.set(gy, gm, gd, 12, 0, 0)
        return cal
    }

    /**
     * Returns 0 for شنبه (Saturday), 1 for یکشنبه (Sunday), ..., 6 for جمعه (Friday).
     */
    fun getFirstDayOfWeekInMonth(year: Int, month: Int): Int {
        val cal = jalaliToGregorian(year, month, 1)
        // Calendar.DAY_OF_WEEK: SUNDAY=1, MONDAY=2, ..., SATURDAY=7
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> 0
            Calendar.SUNDAY -> 1
            Calendar.MONDAY -> 2
            Calendar.TUESDAY -> 3
            Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 5
            Calendar.FRIDAY -> 6
            else -> 0
        }
    }

    fun getTodayJalali(): JalaliDate {
        val cal = Calendar.getInstance()
        return gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun parseJalali(dateStr: String): JalaliDate? {
        val parts = dateStr.split("/")
        if (parts.size == 3) {
            val y = parts[0].toIntOrNull()
            val m = parts[1].toIntOrNull()
            val d = parts[2].toIntOrNull()
            if (y != null && m != null && d != null) {
                return JalaliDate(y, m, d)
            }
        }
        return null
    }

    /**
     * Determines if a given Jalali date string falls within a specific filter period
     * filter: "TODAY", "THIS_WEEK", "THIS_MONTH", "THIS_YEAR", "ALL"
     */
    fun matchesFilter(dateStr: String, filter: String): Boolean {
        if (filter == "ALL") return true
        val target = parseJalali(dateStr) ?: return false
        val today = getTodayJalali()

        return when (filter) {
            "TODAY" -> target == today
            "THIS_WEEK" -> {
                // Check if within the current Persian week (Saturday to Friday)
                val todayCal = jalaliToGregorian(today.year, today.month, today.day)
                val targetCal = jalaliToGregorian(target.year, target.month, target.day)
                val diffMillis = Math.abs(todayCal.timeInMillis - targetCal.timeInMillis)
                val diffDays = diffMillis / (1000 * 60 * 60 * 24)
                diffDays <= 7 && (target.year == today.year || Math.abs(target.year - today.year) <= 1)
            }
            "THIS_MONTH" -> target.year == today.year && target.month == today.month
            "THIS_YEAR" -> target.year == today.year
            else -> true
        }
    }
}
