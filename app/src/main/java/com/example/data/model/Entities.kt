package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val address: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "projects",
    indices = [Index(value = ["clientId"])]
)
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val clientId: Long,
    val totalAmount: Long,
    val myShareType: String = SHARE_TYPE_PERCENTAGE, // "PERCENTAGE" or "CUSTOM_AMOUNT"
    val mySharePercentage: Double = 15.0,
    val customAmount: Long = 0L,
    val myShareCalculated: Long = 0L,
    val description: String = "",
    val status: String = STATUS_IN_PROGRESS, // "IN_PROGRESS" or "COMPLETED"
    val startDate: String = "",
    val endDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val SHARE_TYPE_PERCENTAGE = "PERCENTAGE"
        const val SHARE_TYPE_CUSTOM = "CUSTOM_AMOUNT"

        const val STATUS_IN_PROGRESS = "IN_PROGRESS"
        const val STATUS_COMPLETED = "COMPLETED"

        fun calculateShare(totalAmount: Long, shareType: String, customAmount: Long, percentage: Double = 15.0): Long {
            return if (shareType == SHARE_TYPE_CUSTOM) {
                customAmount
            } else {
                (totalAmount * (percentage / 100.0)).toLong()
            }
        }
    }
}

@Entity(
    tableName = "receipts",
    indices = [Index(value = ["projectId"])]
)
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Long,
    val date: String,
    val paymentMethod: String = "کارت به کارت",
    val description: String = "",
    val projectId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "workdays",
    indices = [Index(value = ["solarDate"], unique = true)]
)
data class WorkdayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val solarDate: String, // e.g. "1405/08/09"
    val year: Int,
    val month: Int,
    val day: Int,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
