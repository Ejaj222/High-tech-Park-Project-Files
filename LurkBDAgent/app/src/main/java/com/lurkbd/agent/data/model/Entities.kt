package com.lurkbd.agent.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TxType { INCOME, EXPENSE }

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val amount: Double,
    val category: String,
    val channel: String,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val party: String,
    val principal: Double,
    val paid: Double = 0.0,
    val dueDate: Long? = null,
    val note: String = ""
)

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val balance: Double = 0.0
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val qty: Double,
    val unit: String = "পিস",
    val lowThreshold: Double = 5.0
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

@Entity(tableName = "class_periods")
data class ClassPeriodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val dayOfWeek: Int,
    val startMinutes: Int,
    val endMinutes: Int,
    val alarmEnabled: Boolean = true,
    val alarmOffsetMinutes: Int = 15
)

@Entity(tableName = "study_blocks")
data class StudyBlockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val dayOfWeek: Int,
    val startMinutes: Int,
    val endMinutes: Int,
    val alarmEnabled: Boolean = true
)

@Entity(tableName = "study_notes")
data class StudyNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val title: String,
    val body: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val title: String,
    val dueAt: Long,
    val done: Boolean = false,
    val documentUri: String = ""
)

@Entity(tableName = "results")
data class ResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val examType: String,
    val score: Double,
    val total: Double,
    val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dose: String = "",
    val timeMinutes: Int,
    val active: Boolean = true
)

@Entity(tableName = "app_settings")
data class AppSettingEntity(
    @PrimaryKey val key: String,
    val value: String
)
