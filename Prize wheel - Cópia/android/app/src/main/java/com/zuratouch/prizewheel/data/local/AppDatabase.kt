package com.zuratouch.prizewheel.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        CategoryEntity::class,
        ProductSlotEntity::class,
        AppConfigEntity::class,
        SaleLogEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productSlotDao(): ProductSlotDao
    abstract fun appConfigDao(): AppConfigDao
    abstract fun saleLogDao(): SaleLogDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE categories ADD COLUMN weight INTEGER NOT NULL DEFAULT 100")
                db.execSQL("ALTER TABLE categories ADD COLUMN icon TEXT NOT NULL DEFAULT '🎁'")
                db.execSQL("UPDATE categories SET weight = 65, icon = '🥤' WHERE id = 'drink'")
                db.execSQL("UPDATE categories SET weight = 30, icon = '🍫' WHERE id = 'snack'")
                db.execSQL("UPDATE categories SET weight = 5, icon = '🎁' WHERE id = 'premium'")
                db.execSQL("ALTER TABLE app_config ADD COLUMN operatorPin TEXT NOT NULL DEFAULT '1234'")
                db.execSQL("ALTER TABLE app_config ADD COLUMN soundEnabled INTEGER NOT NULL DEFAULT 1")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS sale_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        timestampMs INTEGER NOT NULL,
                        categoryLabel TEXT NOT NULL,
                        productName TEXT,
                        vmcLane INTEGER NOT NULL,
                        result TEXT NOT NULL,
                        message TEXT
                    )
                    """.trimIndent(),
                )
            }
        }

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "zura_touch.db",
            )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
        }

        suspend fun seedIfEmpty(database: AppDatabase) {
            if (database.categoryDao().getAll().isNotEmpty()) return
            database.categoryDao().insertAll(
                listOf(
                    CategoryEntity("drink", "Bebida", 0xFF2B6CB0, 65, "🥤"),
                    CategoryEntity("snack", "Snack", 0xFFB7791F, 30, "🍫"),
                    CategoryEntity("premium", "Prémio Premium", 0xFF805AD5, 5, "🎁"),
                ),
            )
            database.productSlotDao().insertAll(
                listOf(
                    ProductSlotEntity("A1", "drink", "Água", 1, 1, 1, 4),
                    ProductSlotEntity("A2", "drink", "Refrigerante", 1, 2, 2, 3),
                    ProductSlotEntity("B1", "snack", "Barra de cereais", 2, 1, 3, 2),
                    ProductSlotEntity("C1", "premium", "Produto surpresa", 3, 1, 4, 1),
                ),
            )
            database.appConfigDao().insert(
                AppConfigEntity(
                    spinPriceCents = 200,
                    serialPortPath = "/dev/ttyS0",
                    mysteryBoxLabel = "Mystery Box",
                    operatorPin = "1234",
                    soundEnabled = true,
                ),
            )
        }
    }
}
