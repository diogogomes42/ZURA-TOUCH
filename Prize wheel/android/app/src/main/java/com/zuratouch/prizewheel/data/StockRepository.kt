package com.zuratouch.prizewheel.data

import com.zuratouch.prizewheel.data.local.AppConfigEntity
import com.zuratouch.prizewheel.data.local.CategoryEntity
import com.zuratouch.prizewheel.data.local.SaleLogEntity
import com.zuratouch.prizewheel.data.local.SpiralEntity
import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.domain.PrizeWheelEngine
import com.zuratouch.prizewheel.domain.QueuedProduct
import com.zuratouch.prizewheel.domain.SaleLogEntry
import com.zuratouch.prizewheel.domain.SaleLogResult
import com.zuratouch.prizewheel.domain.Spiral
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

data class AppConfig(
    val spinPriceCents: Long,
    val serialPortPath: String,
    val mysteryBoxLabel: String,
    val soundEnabled: Boolean,
)

data class StockSnapshot(
    val categories: List<PrizeCategory>,
    val stock: List<Spiral>,
    val config: AppConfig,
)

interface StockDataSource {
    val stockSnapshot: Flow<StockSnapshot>
    val recentSales: Flow<List<SaleLogEntry>>
    suspend fun getSnapshot(): StockSnapshot
    suspend fun registerDelivery(spiralId: String): Boolean
    suspend fun setSpiralQueue(spiralId: String, queue: List<QueuedProduct>, maxCapacity: Int)
    suspend fun updateConfig(
        priceCents: Long,
        serialPath: String,
        label: String,
        newPin: String?,
        soundEnabled: Boolean,
    )
    suspend fun logSale(
        categoryLabel: String,
        productName: String?,
        vmcLane: Int,
        result: SaleLogResult,
        message: String? = null,
    )
    suspend fun getSerialPortPath(): String
    suspend fun isOperatorPinConfigured(): Boolean
    suspend fun setOperatorPin(pin: String)
    suspend fun verifyOperatorPin(pin: String): Boolean
}

class StockRepository(
    private val categoryDao: com.zuratouch.prizewheel.data.local.CategoryDao,
    private val spiralDao: com.zuratouch.prizewheel.data.local.SpiralDao,
    private val appConfigDao: com.zuratouch.prizewheel.data.local.AppConfigDao,
    private val saleLogDao: com.zuratouch.prizewheel.data.local.SaleLogDao,
    private val engine: PrizeWheelEngine = PrizeWheelEngine(),
) : StockDataSource {
    override val stockSnapshot: Flow<StockSnapshot> = combine(
        categoryDao.observeAll(),
        spiralDao.observeAll(),
        appConfigDao.observe(),
    ) { categories, spirals, config ->
        StockSnapshot(
            categories = categories.map { it.toDomain() },
            stock = spirals.map { it.toDomain() },
            config = config?.toDomain() ?: defaultConfig(),
        )
    }

    override val recentSales: Flow<List<SaleLogEntry>> = saleLogDao.observeRecent(50).map { logs ->
        logs.map { it.toDomain() }
    }

    override suspend fun getSnapshot(): StockSnapshot {
        val config = appConfigDao.get()?.toDomain() ?: defaultConfig()
        return StockSnapshot(
            categories = categoryDao.getAll().map { it.toDomain() },
            stock = spiralDao.getAll().map { it.toDomain() },
            config = config,
        )
    }

    override suspend fun registerDelivery(spiralId: String): Boolean {
        val entity = spiralDao.getById(spiralId) ?: return false
        val queue = SpiralQueueCodec.decode(entity.queueJson)
        if (queue.isEmpty()) return false
        spiralDao.setQueue(spiralId, SpiralQueueCodec.encode(queue.drop(1)))
        return true
    }

    override suspend fun setSpiralQueue(spiralId: String, queue: List<QueuedProduct>, maxCapacity: Int) {
        val existing = spiralDao.getById(spiralId) ?: return
        val capacity = maxCapacity.coerceAtLeast(1)
        require(queue.size <= capacity) { "Fila excede capacidade da espiral ($capacity)" }
        spiralDao.upsert(
            existing.copy(
                maxCapacity = capacity,
                queueJson = SpiralQueueCodec.encode(queue),
            ),
        )
    }

    override suspend fun updateConfig(
        priceCents: Long,
        serialPath: String,
        label: String,
        newPin: String?,
        soundEnabled: Boolean,
    ) {
        if (newPin.isNullOrBlank()) {
            appConfigDao.updateWithoutPin(priceCents, serialPath, label, soundEnabled)
        } else {
            require(OperatorPinHasher.isValidPin(newPin)) { "PIN must be at least $MIN_PIN_LENGTH digits" }
            appConfigDao.update(
                priceCents,
                serialPath,
                label,
                OperatorPinHasher.hash(newPin),
                soundEnabled,
            )
        }
    }

    override suspend fun logSale(
        categoryLabel: String,
        productName: String?,
        vmcLane: Int,
        result: SaleLogResult,
        message: String?,
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

    override suspend fun getSerialPortPath(): String = appConfigDao.get()?.serialPortPath ?: "/dev/ttyS0"

    override suspend fun isOperatorPinConfigured(): Boolean {
        val pin = appConfigDao.get()?.operatorPin
        return !pin.isNullOrBlank()
    }

    override suspend fun setOperatorPin(pin: String) {
        require(OperatorPinHasher.isValidPin(pin)) { "PIN must be at least $MIN_PIN_LENGTH digits" }
        val config = appConfigDao.get() ?: return
        appConfigDao.update(
            config.spinPriceCents,
            config.serialPortPath,
            config.mysteryBoxLabel,
            OperatorPinHasher.hash(pin),
            config.soundEnabled,
        )
    }

    override suspend fun verifyOperatorPin(pin: String): Boolean {
        val stored = appConfigDao.get()?.operatorPin ?: return false
        if (stored.isBlank()) return false
        return OperatorPinHasher.verify(pin, stored)
    }

    suspend fun upsertCategory(category: PrizeCategory) {
        require(category.id.isNotBlank()) { "ID da categoria obrigatório" }
        require(category.label.isNotBlank()) { "Nome da categoria obrigatório" }
        categoryDao.upsert(
            CategoryEntity(
                id = category.id.trim(),
                label = category.label.trim(),
                color = category.color,
                weight = category.weight.coerceAtLeast(1),
                icon = category.icon.ifBlank { "🎁" },
            ),
        )
    }

    suspend fun deleteCategory(categoryId: String): String? {
        val stock = spiralDao.getAll().map { it.toDomain() }
        if (engine.countProductsInCategory(stock, categoryId) > 0) {
            return "Remova os produtos desta categoria antes de a apagar."
        }
        categoryDao.delete(categoryId)
        return null
    }

    suspend fun refillSpiral(spiral: Spiral) {
        require(spiral.id.isNotBlank()) { "Espiral inválida" }
        val existing = spiralDao.getById(spiral.id)
        require(existing != null) { "Espiral desconhecida — use o mapa 8×3" }
        require(spiral.queue.size <= spiral.maxCapacity.coerceAtLeast(1)) {
            "Fila excede capacidade da espiral"
        }
        spiral.queue.forEach { product ->
            require(product.productName.isNotBlank()) { "Nome do produto obrigatório" }
            require(product.categoryId.isNotBlank()) { "Tipo na roleta obrigatório" }
            require(categoryDao.getAll().any { it.id == product.categoryId }) { "Tipo na roleta inválido" }
        }
        spiralDao.upsert(
            existing.copy(
                maxCapacity = spiral.maxCapacity.coerceAtLeast(1),
                queueJson = SpiralQueueCodec.encode(spiral.queue),
            ),
        )
    }

    suspend fun clearSpiral(spiralId: String) {
        val existing = spiralDao.getById(spiralId) ?: return
        spiralDao.upsert(existing.copy(queueJson = "[]"))
    }

    suspend fun loadTestMachineLayout() {
        categoryDao.getAll().forEach { categoryDao.delete(it.id) }
        categoryDao.insertAll(MachineLayoutSeed.defaultCategories())
        spiralDao.deleteAll()
        spiralDao.insertAll(MachineLayoutSeed.allPhysicalSpirals())
        MachineLayoutSeed.testSpirals().forEach { spiralDao.upsert(it) }
    }

    suspend fun ensurePhysicalSpirals() {
        val existing = spiralDao.getAll().associateBy { it.id }
        val missing = MachineLayoutSeed.allPhysicalSpirals().filter { it.id !in existing }
        if (missing.isNotEmpty()) {
            spiralDao.insertAll(missing)
        }
        repairOverflowQueues()
    }

    /** Trims queues that exceed maxCapacity (legacy / bad seed data). */
    suspend fun repairOverflowQueues() {
        spiralDao.getAll().forEach { entity ->
            val queue = SpiralQueueCodec.decode(entity.queueJson)
            if (queue.size > entity.maxCapacity) {
                spiralDao.setQueue(entity.id, SpiralQueueCodec.encode(queue.take(entity.maxCapacity)))
            }
        }
    }

    private fun defaultConfig() = AppConfig(200, "/dev/ttyS0", "Mystery Box", true)

    private companion object {
        const val MIN_PIN_LENGTH = OperatorPinHasher.MIN_LENGTH
    }
}

private fun CategoryEntity.toDomain() = PrizeCategory(
    id = id,
    label = label,
    color = color,
    weight = weight,
    icon = icon,
)

private fun SpiralEntity.toDomain(): Spiral {
    val decoded = SpiralQueueCodec.decode(queueJson)
    val queue = if (decoded.size <= maxCapacity) decoded else decoded.take(maxCapacity)
    return Spiral(
        id = id,
        shelf = shelf,
        slot = slot,
        vmcLane = vmcLane,
        maxCapacity = maxCapacity,
        queue = queue,
    )
}

private fun AppConfigEntity.toDomain() = AppConfig(
    spinPriceCents = spinPriceCents,
    serialPortPath = serialPortPath,
    mysteryBoxLabel = mysteryBoxLabel,
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
