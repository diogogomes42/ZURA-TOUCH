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

    @Query("UPDATE categories SET weight = :weight WHERE id = :categoryId")
    suspend fun updateWeight(categoryId: String, weight: Int)
}

@Dao
interface ProductSlotDao {
    @Query("SELECT * FROM product_slots ORDER BY shelf, slot")
    fun observeAll(): Flow<List<ProductSlotEntity>>

    @Query("SELECT * FROM product_slots ORDER BY shelf, slot")
    suspend fun getAll(): List<ProductSlotEntity>

    @Query("UPDATE product_slots SET quantity = quantity - 1 WHERE id = :slotId AND quantity > 0")
    suspend fun decrementQuantity(slotId: String): Int

    @Query("UPDATE product_slots SET quantity = :quantity WHERE id = :slotId")
    suspend fun setQuantity(slotId: String, quantity: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(slots: List<ProductSlotEntity>)
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
}

@Dao
interface SaleLogDao {
    @Query("SELECT * FROM sale_logs ORDER BY timestampMs DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<SaleLogEntity>>

    @Insert
    suspend fun insert(log: SaleLogEntity)
}
