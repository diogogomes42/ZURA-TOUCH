package com.zuratouch.prizewheel.data

import com.zuratouch.prizewheel.data.local.AppConfigEntity
import com.zuratouch.prizewheel.data.local.CategoryDao
import com.zuratouch.prizewheel.data.local.DigitalPurchaseDao
import com.zuratouch.prizewheel.data.local.DigitalPurchaseEntity
import com.zuratouch.prizewheel.data.local.CategoryEntity
import com.zuratouch.prizewheel.data.local.SaleLogDao
import com.zuratouch.prizewheel.data.local.SaleLogEntity
import com.zuratouch.prizewheel.data.local.SpiralDao
import com.zuratouch.prizewheel.data.local.SpiralEntity
import com.zuratouch.prizewheel.domain.PrizeProductCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class JackpotPersistenceTest {
    @Test
    fun `counter persists across repository instances`() = runBlocking {
        val configDao = InMemoryAppConfigDao(
            AppConfigEntity(
                spinPriceCents = 200,
                serialPortPath = "/dev/ttyS0",
                mysteryBoxLabel = "Mystery Box",
                jackpotAccumulatedRevenueCents = 0L,
                jackpotEnabled = true,
            ),
        )
        val repository1 = createRepository(configDao)
        repository1.applyJackpotAfterSuccessfulPlay(250L, PrizeProductCatalog.COMMON)
        repository1.applyJackpotAfterSuccessfulPlay(650L, PrizeProductCatalog.RARE)

        val repository2 = createRepository(configDao)
        assertEquals(900L, repository2.getJackpotState().accumulatedRevenueCents)

        repository2.applyJackpotAfterSuccessfulPlay(5_000L, PrizeProductCatalog.LEGENDARY)
        assertEquals(0L, configDao.get()?.jackpotAccumulatedRevenueCents)
    }

    private fun createRepository(configDao: InMemoryAppConfigDao): StockRepository =
        StockRepository(
            categoryDao = EmptyCategoryDao,
            spiralDao = EmptySpiralDao,
            appConfigDao = configDao,
            saleLogDao = EmptySaleLogDao,
            digitalPurchaseDao = EmptyDigitalPurchaseDao,
        )
}

private class InMemoryAppConfigDao(initial: AppConfigEntity) : com.zuratouch.prizewheel.data.local.AppConfigDao {
    private var config: AppConfigEntity? = initial

    override fun observe(): Flow<AppConfigEntity?> = flowOf(config)

    override suspend fun get(): AppConfigEntity? = config

    override suspend fun insert(config: AppConfigEntity) {
        this.config = config
    }

    override suspend fun update(
        priceCents: Long,
        serialPath: String,
        label: String,
        pin: String,
        soundEnabled: Boolean,
    ) = Unit

    override suspend fun updateWithoutPin(
        priceCents: Long,
        serialPath: String,
        label: String,
        soundEnabled: Boolean,
    ) = Unit

    override suspend fun updateExperienceMode(mode: String) = Unit

    override suspend fun updateJackpotAccumulated(accumulatedCents: Long) {
        config = config?.copy(jackpotAccumulatedRevenueCents = accumulatedCents)
    }

    override suspend fun updateJackpotEnabled(enabled: Boolean) {
        config = config?.copy(jackpotEnabled = enabled)
    }
}

private object EmptyCategoryDao : CategoryDao {
    override fun observeAll(): Flow<List<CategoryEntity>> = flowOf(emptyList())
    override suspend fun getAll(): List<CategoryEntity> = emptyList()
    override suspend fun insertAll(categories: List<CategoryEntity>) = Unit
    override suspend fun upsert(category: CategoryEntity) = Unit
    override suspend fun delete(categoryId: String) = Unit
    override suspend fun updateWeight(categoryId: String, weight: Int) = Unit
}

private object EmptySpiralDao : SpiralDao {
    override fun observeAll(): Flow<List<SpiralEntity>> = flowOf(emptyList())
    override suspend fun getAll(): List<SpiralEntity> = emptyList()
    override suspend fun setQueue(spiralId: String, queueJson: String) = Unit
    override suspend fun insertAll(spirals: List<SpiralEntity>) = Unit
    override suspend fun upsert(spiral: SpiralEntity) = Unit
    override suspend fun delete(spiralId: String) = Unit
    override suspend fun getById(spiralId: String): SpiralEntity? = null
    override suspend fun deleteAll() = Unit
}

private object EmptySaleLogDao : SaleLogDao {
    override fun observeRecent(limit: Int): Flow<List<SaleLogEntity>> = flowOf(emptyList())
    override suspend fun insert(log: SaleLogEntity) = Unit
}

private object EmptyDigitalPurchaseDao : DigitalPurchaseDao {
    override suspend fun insert(purchase: DigitalPurchaseEntity): Long = 1L
    override suspend fun getRecent(limit: Int): List<DigitalPurchaseEntity> = emptyList()
}
