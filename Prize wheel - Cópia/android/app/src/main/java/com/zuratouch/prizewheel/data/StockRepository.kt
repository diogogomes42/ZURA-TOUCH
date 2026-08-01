package com.zuratouch.prizewheel.data

import com.zuratouch.prizewheel.data.local.AppConfigEntity
import com.zuratouch.prizewheel.data.local.CategoryEntity
import com.zuratouch.prizewheel.data.local.ProductSlotEntity
import com.zuratouch.prizewheel.data.local.SaleLogEntity
import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.domain.ProductSlot
import com.zuratouch.prizewheel.domain.SaleLogEntry
import com.zuratouch.prizewheel.domain.SaleLogResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

data class AppConfig(
    val spinPriceCents: Long,
    val serialPortPath: String,
    val mysteryBoxLabel: String,
    val operatorPin: String,
    val soundEnabled: Boolean,
)

data class StockSnapshot(
    val categories: List<PrizeCategory>,
    val stock: List<ProductSlot>,
    val config: AppConfig,
)

class StockRepository(
    private val categoryDao: com.zuratouch.prizewheel.data.local.CategoryDao,
    private val productSlotDao: com.zuratouch.prizewheel.data.local.ProductSlotDao,
    private val appConfigDao: com.zuratouch.prizewheel.data.local.AppConfigDao,
    private val saleLogDao: com.zuratouch.prizewheel.data.local.SaleLogDao,
) {
    val stockSnapshot: Flow<StockSnapshot> = combine(
        categoryDao.observeAll(),
        productSlotDao.observeAll(),
        appConfigDao.observe(),
    ) { categories, slots, config ->
        StockSnapshot(
            categories = categories.map { it.toDomain() },
            stock = slots.map { it.toDomain() },
            config = config?.toDomain() ?: defaultConfig(),
        )
    }

    val recentSales: Flow<List<SaleLogEntry>> = saleLogDao.observeRecent(50).map { logs ->
        logs.map { it.toDomain() }
    }

    suspend fun getSnapshot(): StockSnapshot {
        val config = appConfigDao.get()?.toDomain() ?: defaultConfig()
        return StockSnapshot(
            categories = categoryDao.getAll().map { it.toDomain() },
            stock = productSlotDao.getAll().map { it.toDomain() },
            config = config,
        )
    }

    suspend fun registerDelivery(slotId: String): Boolean = productSlotDao.decrementQuantity(slotId) > 0

    suspend fun setSlotQuantity(slotId: String, quantity: Int) {
        productSlotDao.setQuantity(slotId, quantity.coerceAtLeast(0))
    }

    suspend fun updateConfig(
        priceCents: Long,
        serialPath: String,
        label: String,
        pin: String,
        soundEnabled: Boolean,
    ) {
        appConfigDao.update(priceCents, serialPath, label, pin, soundEnabled)
    }

    suspend fun logSale(
        categoryLabel: String,
        productName: String?,
        vmcLane: Int,
        result: SaleLogResult,
        message: String? = null,
    ) {
        saleLogDao.insert(
            SaleLogEntity(
                timestampMs = System.currentTimeMillis(),
                categoryLabel = categoryLabel,
                productName = productName,
                vmcLane = vmcLane,
                result = result.name,
                message = message,
            ),
        )
    }

    suspend fun getSerialPortPath(): String = appConfigDao.get()?.serialPortPath ?: "/dev/ttyS0"

    suspend fun verifyOperatorPin(pin: String): Boolean = appConfigDao.get()?.operatorPin == pin

    private fun defaultConfig() = AppConfig(200, "/dev/ttyS0", "Mystery Box", "1234", true)
}

private fun CategoryEntity.toDomain() = PrizeCategory(
    id = id,
    label = label,
    color = color,
    weight = weight,
    icon = icon,
)

private fun ProductSlotEntity.toDomain() = ProductSlot(
    id = id,
    categoryId = categoryId,
    productName = productName,
    shelf = shelf,
    slot = slot,
    vmcLane = vmcLane,
    quantity = quantity,
)

private fun AppConfigEntity.toDomain() = AppConfig(
    spinPriceCents = spinPriceCents,
    serialPortPath = serialPortPath,
    mysteryBoxLabel = mysteryBoxLabel,
    operatorPin = operatorPin,
    soundEnabled = soundEnabled,
)

private fun SaleLogEntity.toDomain() = SaleLogEntry(
    id = id,
    timestampMs = timestampMs,
    categoryLabel = categoryLabel,
    productName = productName,
    vmcLane = vmcLane,
    result = SaleLogResult.valueOf(result),
    message = message,
)
