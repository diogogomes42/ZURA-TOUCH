package com.zuratouch.prizewheel.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val label: String,
    val color: Long,
    val weight: Int = 100,
    val icon: String = "🎁",
)

@Entity(tableName = "product_slots")
data class ProductSlotEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val productName: String,
    val shelf: Int,
    val slot: Int,
    val vmcLane: Int,
    val quantity: Int,
)

@Entity(tableName = "app_config")
data class AppConfigEntity(
    @PrimaryKey val id: Int = 1,
    val spinPriceCents: Long,
    val serialPortPath: String,
    val mysteryBoxLabel: String,
    val operatorPin: String = "1234",
    val soundEnabled: Boolean = true,
)

@Entity(tableName = "sale_logs")
data class SaleLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestampMs: Long,
    val categoryLabel: String,
    val productName: String?,
    val vmcLane: Int,
    val result: String,
    val message: String?,
)
