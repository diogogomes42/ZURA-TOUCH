package com.zuratouch.prizewheel.data

import android.content.Context
import com.zuratouch.prizewheel.data.local.AppDatabase

object StockRepositoryFactory {
    suspend fun create(context: Context): StockRepository {
        val database = AppDatabase.get(context.applicationContext)
        AppDatabase.seedIfEmpty(database)
        val repository = StockRepository(
            categoryDao = database.categoryDao(),
            spiralDao = database.spiralDao(),
            appConfigDao = database.appConfigDao(),
            saleLogDao = database.saleLogDao(),
            digitalPurchaseDao = database.digitalPurchaseDao(),
        )
        repository.ensurePhysicalSpirals()
        return repository
    }
}
