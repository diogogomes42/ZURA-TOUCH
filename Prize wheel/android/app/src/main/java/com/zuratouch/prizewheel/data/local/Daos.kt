package com.zuratouch.prizewheel.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY label")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY label")
    suspend fun getAll(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun delete(categoryId: String)

    @Query("UPDATE categories SET weight = :weight WHERE id = :categoryId")
    suspend fun updateWeight(categoryId: String, weight: Int)
}

@Dao
interface SpiralDao {
    @Query("SELECT * FROM spirals ORDER BY shelf, slot")
    fun observeAll(): Flow<List<SpiralEntity>>

    @Query("SELECT * FROM spirals ORDER BY shelf, slot")
    suspend fun getAll(): List<SpiralEntity>

    @Query("UPDATE spirals SET queueJson = :queueJson WHERE id = :spiralId")
    suspend fun setQueue(spiralId: String, queueJson: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(spirals: List<SpiralEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(spiral: SpiralEntity)

    @Query("DELETE FROM spirals WHERE id = :spiralId")
    suspend fun delete(spiralId: String)

    @Query("SELECT * FROM spirals WHERE id = :spiralId")
    suspend fun getById(spiralId: String): SpiralEntity?

    @Query("DELETE FROM spirals")
    suspend fun deleteAll()
}

@Dao
interface AppConfigDao {
    @Query("SELECT * FROM app_config WHERE id = 1")
    fun observe(): Flow<AppConfigEntity?>

    @Query("SELECT * FROM app_config WHERE id = 1")
    suspend fun get(): AppConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: AppConfigEntity)

    @Query(
        "UPDATE app_config SET spinPriceCents = :priceCents, serialPortPath = :serialPath, " +
            "mysteryBoxLabel = :label, operatorPin = :pin, soundEnabled = :soundEnabled WHERE id = 1",
    )
    suspend fun update(
        priceCents: Long,
        serialPath: String,
        label: String,
        pin: String,
        soundEnabled: Boolean,
    )

    @Query(
        "UPDATE app_config SET spinPriceCents = :priceCents, serialPortPath = :serialPath, " +
            "mysteryBoxLabel = :label, soundEnabled = :soundEnabled WHERE id = 1",
    )
    suspend fun updateWithoutPin(
        priceCents: Long,
        serialPath: String,
        label: String,
        soundEnabled: Boolean,
    )
}

@Dao
interface SaleLogDao {
    @Query("SELECT * FROM sale_logs ORDER BY timestampMs DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<SaleLogEntity>>

    @Insert
    suspend fun insert(log: SaleLogEntity)
}
