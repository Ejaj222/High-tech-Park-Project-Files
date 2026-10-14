package com.lurkbd.agent.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions
        WHERE createdAt >= :start AND createdAt < :end
        ORDER BY createdAt DESC
        """
    )
    fun observeBetween(start: Long, end: Long): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insert(item: TransactionEntity): Long

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans ORDER BY id DESC")
    fun observeAll(): Flow<List<LoanEntity>>

    @Insert
    suspend fun insert(item: LoanEntity): Long

    @Update
    suspend fun update(item: LoanEntity)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun observeAll(): Flow<List<CustomerEntity>>

    @Insert
    suspend fun insert(item: CustomerEntity): Long

    @Update
    suspend fun update(item: CustomerEntity)

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :name || '%' LIMIT 1")
    suspend fun findByName(name: String): CustomerEntity?
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun observeAll(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ProductEntity): Long

    @Update
    suspend fun update(item: ProductEntity)

    @Query("SELECT * FROM products WHERE name LIKE '%' || :name || '%' LIMIT 1")
    suspend fun findByName(name: String): ProductEntity?
}

@Dao
interface ClassPeriodDao {
    @Query("SELECT * FROM class_periods ORDER BY dayOfWeek, startMinutes")
    fun observeAll(): Flow<List<ClassPeriodEntity>>

    @Insert
    suspend fun insert(item: ClassPeriodEntity): Long

    @Update
    suspend fun update(item: ClassPeriodEntity)
}

@Dao
interface StudyBlockDao {
    @Query("SELECT * FROM study_blocks ORDER BY dayOfWeek, startMinutes")
    fun observeAll(): Flow<List<StudyBlockEntity>>

    @Insert
    suspend fun insert(item: StudyBlockEntity): Long
}

@Dao
interface StudyNoteDao {
    @Query("SELECT * FROM study_notes ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<StudyNoteEntity>>

    @Insert
    suspend fun insert(item: StudyNoteEntity): Long
}

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments ORDER BY dueAt ASC")
    fun observeAll(): Flow<List<AssignmentEntity>>

    @Insert
    suspend fun insert(item: AssignmentEntity): Long

    @Update
    suspend fun update(item: AssignmentEntity)
}

@Dao
interface ResultDao {
    @Query("SELECT * FROM results ORDER BY date DESC")
    fun observeAll(): Flow<List<ResultEntity>>

    @Insert
    suspend fun insert(item: ResultEntity): Long
}

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines ORDER BY timeMinutes ASC")
    fun observeAll(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MedicineEntity?

    @Insert
    suspend fun insert(item: MedicineEntity): Long

    @Update
    suspend fun update(item: MedicineEntity)
}

@Dao
interface StudyDocumentDao {
    @Query("SELECT * FROM study_documents ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<StudyDocumentEntity>>

    @Insert
    suspend fun insert(item: StudyDocumentEntity): Long

    @Query("DELETE FROM study_documents WHERE id = :id")
    suspend fun delete(id: Long)
}
