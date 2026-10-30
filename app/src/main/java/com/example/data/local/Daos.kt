package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ClientEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ReceiptEntity
import com.example.data.model.WorkdayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients ORDER BY id DESC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    suspend fun getClientById(id: Long): ClientEntity?

    @Query("SELECT * FROM clients")
    suspend fun getAllClientsList(): List<ClientEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity): Long

    @Update
    suspend fun updateClient(client: ClientEntity)

    @Delete
    suspend fun deleteClient(client: ClientEntity)

    @Query("DELETE FROM clients")
    suspend fun deleteAllClients()
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY id DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): ProjectEntity?

    @Query("SELECT * FROM projects WHERE clientId = :clientId ORDER BY id DESC")
    fun getProjectsByClient(clientId: Long): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects")
    suspend fun getAllProjectsList(): List<ProjectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("DELETE FROM projects")
    suspend fun deleteAllProjects()
}

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM receipts ORDER BY id DESC")
    fun getAllReceipts(): Flow<List<ReceiptEntity>>

    @Query("SELECT * FROM receipts WHERE id = :id LIMIT 1")
    suspend fun getReceiptById(id: Long): ReceiptEntity?

    @Query("SELECT * FROM receipts WHERE projectId = :projectId ORDER BY id DESC")
    fun getReceiptsByProject(projectId: Long): Flow<List<ReceiptEntity>>

    @Query("SELECT * FROM receipts")
    suspend fun getAllReceiptsList(): List<ReceiptEntity>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM receipts")
    fun getTotalReceived(): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: ReceiptEntity): Long

    @Update
    suspend fun updateReceipt(receipt: ReceiptEntity)

    @Delete
    suspend fun deleteReceipt(receipt: ReceiptEntity)

    @Query("DELETE FROM receipts")
    suspend fun deleteAllReceipts()
}

@Dao
interface WorkdayDao {
    @Query("SELECT * FROM workdays ORDER BY year DESC, month DESC, day DESC")
    fun getAllWorkdays(): Flow<List<WorkdayEntity>>

    @Query("SELECT * FROM workdays WHERE year = :year AND month = :month")
    fun getWorkdaysForMonth(year: Int, month: Int): Flow<List<WorkdayEntity>>

    @Query("SELECT * FROM workdays WHERE solarDate = :solarDate LIMIT 1")
    suspend fun getWorkdayByDate(solarDate: String): WorkdayEntity?

    @Query("SELECT * FROM workdays")
    suspend fun getAllWorkdaysList(): List<WorkdayEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkday(workday: WorkdayEntity): Long

    @Delete
    suspend fun deleteWorkday(workday: WorkdayEntity)

    @Query("DELETE FROM workdays WHERE solarDate = :solarDate")
    suspend fun deleteBySolarDate(solarDate: String)

    @Query("DELETE FROM workdays")
    suspend fun deleteAllWorkdays()
}
