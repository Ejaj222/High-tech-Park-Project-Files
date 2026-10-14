package com.lurkbd.agent.data.repository

import com.lurkbd.agent.data.db.LurkDatabase
import com.lurkbd.agent.data.model.AssignmentEntity
import com.lurkbd.agent.data.model.ClassPeriodEntity
import com.lurkbd.agent.data.model.CustomerEntity
import com.lurkbd.agent.data.model.LoanEntity
import com.lurkbd.agent.data.model.MedicineEntity
import com.lurkbd.agent.data.model.ProductEntity
import com.lurkbd.agent.data.model.ResultEntity
import com.lurkbd.agent.data.model.StudyBlockEntity
import com.lurkbd.agent.data.model.StudyNoteEntity
import com.lurkbd.agent.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class LurkRepository(private val db: LurkDatabase) {
    fun transactions(): Flow<List<TransactionEntity>> = db.transactions().observeAll()
    fun todayTransactions(): Flow<List<TransactionEntity>> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = cal.timeInMillis
        return db.transactions().observeBetween(start, start + 86_400_000)
    }

    suspend fun addTransaction(
        type: String,
        amount: Double,
        category: String,
        channel: String,
        note: String = ""
    ) {
        db.transactions().insert(
            TransactionEntity(
                type = type,
                amount = amount,
                category = category,
                channel = channel,
                note = note
            )
        )
    }

    fun loans(): Flow<List<LoanEntity>> = db.loans().observeAll()
    suspend fun addLoan(party: String, principal: Double) {
        db.loans().insert(LoanEntity(party = party, principal = principal))
    }

    fun customers(): Flow<List<CustomerEntity>> = db.customers().observeAll()
    suspend fun addCustomerCredit(name: String, amount: Double) {
        val existing = db.customers().findByName(name)
        if (existing == null) {
            db.customers().insert(CustomerEntity(name = name, balance = amount))
        } else {
            db.customers().update(existing.copy(balance = existing.balance + amount))
        }
    }

    fun products(): Flow<List<ProductEntity>> = db.products().observeAll()
    suspend fun setStock(name: String, qty: Double, unit: String = "পিস") {
        val existing = db.products().findByName(name)
        if (existing == null) {
            db.products().upsert(ProductEntity(name = name, qty = qty, unit = unit))
        } else {
            db.products().update(existing.copy(qty = qty))
        }
    }

    fun classPeriods(): Flow<List<ClassPeriodEntity>> = db.classPeriods().observeAll()
    suspend fun addClass(subject: String, dayOfWeek: Int, start: Int, end: Int) {
        db.classPeriods().insert(
            ClassPeriodEntity(
                subjectName = subject,
                dayOfWeek = dayOfWeek,
                startMinutes = start,
                endMinutes = end
            )
        )
    }

    fun studyBlocks(): Flow<List<StudyBlockEntity>> = db.studyBlocks().observeAll()
    suspend fun addStudyBlock(subject: String, dayOfWeek: Int, start: Int, end: Int) {
        db.studyBlocks().insert(
            StudyBlockEntity(
                subjectName = subject,
                dayOfWeek = dayOfWeek,
                startMinutes = start,
                endMinutes = end
            )
        )
    }

    fun studyNotes(): Flow<List<StudyNoteEntity>> = db.studyNotes().observeAll()
    suspend fun addNote(subject: String, title: String, body: String) {
        db.studyNotes().insert(
            StudyNoteEntity(subjectName = subject, title = title, body = body)
        )
    }

    fun assignments(): Flow<List<AssignmentEntity>> = db.assignments().observeAll()
    suspend fun addAssignment(subject: String, title: String, dueAt: Long) {
        db.assignments().insert(
            AssignmentEntity(subjectName = subject, title = title, dueAt = dueAt)
        )
    }

    fun results(): Flow<List<ResultEntity>> = db.results().observeAll()
    suspend fun addResult(subject: String, examType: String, score: Double, total: Double) {
        db.results().insert(
            ResultEntity(
                subjectName = subject,
                examType = examType,
                score = score,
                total = total
            )
        )
    }

    fun medicines(): Flow<List<MedicineEntity>> = db.medicines().observeAll()
    suspend fun addMedicine(name: String, timeMinutes: Int, dose: String = "") {
        db.medicines().insert(
            MedicineEntity(name = name, dose = dose, timeMinutes = timeMinutes)
        )
    }

    suspend fun setMedicineActive(item: MedicineEntity, active: Boolean) {
        db.medicines().update(item.copy(active = active))
    }

    suspend fun setClassAlarm(item: ClassPeriodEntity, enabled: Boolean) {
        db.classPeriods().update(item.copy(alarmEnabled = enabled))
    }
}
