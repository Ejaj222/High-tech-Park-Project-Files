package com.lurkbd.agent.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lurkbd.agent.data.model.AssignmentEntity
import com.lurkbd.agent.data.model.ClassPeriodEntity
import com.lurkbd.agent.data.model.CustomerEntity
import com.lurkbd.agent.data.model.LoanEntity
import com.lurkbd.agent.data.model.MedicineEntity
import com.lurkbd.agent.data.model.ProductEntity
import com.lurkbd.agent.data.model.ResultEntity
import com.lurkbd.agent.data.model.StudyBlockEntity
import com.lurkbd.agent.data.model.StudyDocumentEntity
import com.lurkbd.agent.data.model.StudyNoteEntity
import com.lurkbd.agent.data.model.TransactionEntity
import java.util.Calendar

@Database(
    entities = [
        TransactionEntity::class,
        LoanEntity::class,
        CustomerEntity::class,
        ProductEntity::class,
        ClassPeriodEntity::class,
        StudyBlockEntity::class,
        StudyNoteEntity::class,
        AssignmentEntity::class,
        ResultEntity::class,
        MedicineEntity::class,
        StudyDocumentEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LurkDatabase : RoomDatabase() {
    abstract fun transactions(): TransactionDao
    abstract fun loans(): LoanDao
    abstract fun customers(): CustomerDao
    abstract fun products(): ProductDao
    abstract fun classPeriods(): ClassPeriodDao
    abstract fun studyBlocks(): StudyBlockDao
    abstract fun studyNotes(): StudyNoteDao
    abstract fun assignments(): AssignmentDao
    abstract fun results(): ResultDao
    abstract fun medicines(): MedicineDao
    abstract fun studyDocuments(): StudyDocumentDao

    companion object {
        @Volatile
        private var instance: LurkDatabase? = null

        fun get(context: Context): LurkDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LurkDatabase::class.java,
                    "lurkbd.db"
                ).fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
        }
    }
}

suspend fun seedIfEmpty(db: LurkDatabase) {
    // Use a cheap probe via medicine insert path: only seed when no medicines exist.
    // Callers check prefs flag; this is a helper used once from Application.
    val now = System.currentTimeMillis()
    val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    db.transactions().insert(
        TransactionEntity(
            type = "INCOME",
            amount = 12450.0,
            category = "বেতন",
            channel = "ব্যাংক",
            createdAt = now - 86_400_000
        )
    )
    db.transactions().insert(
        TransactionEntity(
            type = "EXPENSE",
            amount = 300.0,
            category = "বাজার",
            channel = "নগদ",
            createdAt = now - 3_600_000
        )
    )
    db.transactions().insert(
        TransactionEntity(
            type = "EXPENSE",
            amount = 50.0,
            category = "মোবাইল রিচার্জ",
            channel = "bKash",
            createdAt = now - 7_200_000
        )
    )
    db.customers().insert(CustomerEntity(name = "করিম ভাই", balance = 2500.0))
    db.customers().insert(CustomerEntity(name = "রহিমা বেগম", balance = 850.0))
    db.products().upsert(ProductEntity(name = "চাল", qty = 40.0, unit = "কেজি", lowThreshold = 10.0))
    db.products().upsert(ProductEntity(name = "তেল", qty = 12.0, unit = "লিটার", lowThreshold = 5.0))
    db.products().upsert(ProductEntity(name = "সাবান", qty = 3.0, unit = "প্যাকেট", lowThreshold = 5.0))
    db.classPeriods().insert(
        ClassPeriodEntity(
            subjectName = "গণিত",
            dayOfWeek = today,
            startMinutes = 9 * 60,
            endMinutes = 10 * 60
        )
    )
    db.classPeriods().insert(
        ClassPeriodEntity(
            subjectName = "ইংরেজি",
            dayOfWeek = today,
            startMinutes = 10 * 60 + 30,
            endMinutes = 11 * 60 + 30
        )
    )
    db.studyNotes().insert(
        StudyNoteEntity(subjectName = "গণিত", title = "অধ্যায় ৫", body = "সূত্র ও উদাহরণ নোট")
    )
    db.results().insert(
        ResultEntity(subjectName = "পদার্থ", examType = "ক্লাস টেস্ট", score = 18.0, total = 20.0)
    )
    db.medicines().insert(MedicineEntity(name = "Metformin", dose = "১টি", timeMinutes = 8 * 60))
    db.medicines().insert(MedicineEntity(name = "Vitamin D", dose = "১টি", timeMinutes = 22 * 60))
    db.loans().insert(LoanEntity(party = "ব্যাংক কিস্তি", principal = 50000.0, paid = 12000.0))
}
