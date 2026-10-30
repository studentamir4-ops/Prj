package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ProjectEntity
import com.example.util.NumberFormatUtils
import com.example.util.PersianDateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("مدیریت پروژه و مالی", appName)
    }

    @Test
    fun `test financial 15 percent share calculation`() {
        val totalAmount = 100_000_000L
        val share = ProjectEntity.calculateShare(
            totalAmount = totalAmount,
            shareType = ProjectEntity.SHARE_TYPE_PERCENTAGE,
            customAmount = 0L,
            percentage = 15.0
        )
        assertEquals(15_000_000L, share)
    }

    @Test
    fun `test financial custom fixed amount share`() {
        val totalAmount = 100_000_000L
        val customAmount = 22_500_000L
        val share = ProjectEntity.calculateShare(
            totalAmount = totalAmount,
            shareType = ProjectEntity.SHARE_TYPE_CUSTOM,
            customAmount = customAmount,
            percentage = 15.0
        )
        assertEquals(customAmount, share)
    }

    @Test
    fun `test number format and persian digits conversion`() {
        val persianInput = "۱۲،۵۰۰،۰۰۰"
        val parsed = NumberFormatUtils.parseAmount(persianInput)
        assertEquals(12_500_000L, parsed)
        assertEquals("12,500,000", NumberFormatUtils.formatNumber(parsed))
    }

    @Test
    fun `test jalali date conversion`() {
        val today = PersianDateUtils.getTodayJalali()
        assertTrue(today.year >= 1400)
        assertTrue(today.month in 1..12)
        assertTrue(today.day in 1..31)
    }
}
