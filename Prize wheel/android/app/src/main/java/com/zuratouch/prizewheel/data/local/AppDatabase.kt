package com.zuratouch.prizewheel.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zuratouch.prizewheel.BuildConfig
import com.zuratouch.prizewheel.data.MachineLayoutSeed
import com.zuratouch.prizewheel.data.OperatorPinHasher

@Database(
    entities = [
        CategoryEntity::class,
        SpiralEntity::class,
        AppConfigEntity::class,
        SaleLogEntity::class,
    ],
    version = 7,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun spiralDao(): SpiralDao
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

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("UPDATE app_config SET operatorPin = '5018' WHERE operatorPin = '1234'")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.query("SELECT operatorPin FROM app_config WHERE id = 1").use { cursor ->
                    if (cursor.moveToFirst()) {
                        val pin = cursor.getString(0)
                        if (!OperatorPinHasher.isHashed(pin)) {
                            val hashed = OperatorPinHasher.hash(pin)
                            db.execSQL("UPDATE app_config SET operatorPin = ? WHERE id = 1", arrayOf(hashed))
                        }
                    }
                }
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val hash5018 = OperatorPinHasher.hash("5018")
                val hash1234 = OperatorPinHasher.hash("1234")
                db.execSQL(
                    "UPDATE app_config SET operatorPin = '' WHERE operatorPin IN (?, ?, ?, ?)",
                    arrayOf(hash5018, hash1234, "5018", "1234"),
                )
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE product_slots ADD COLUMN maxCapacity INTEGER NOT NULL DEFAULT 10",
                )
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS spirals (
                        id TEXT NOT NULL PRIMARY KEY,
                        shelf INTEGER NOT NULL,
                        slot INTEGER NOT NULL,
                        vmcLane INTEGER NOT NULL,
                        maxCapacity INTEGER NOT NULL,
                        queueJson TEXT NOT NULL DEFAULT '[]'
                    )
                    """.trimIndent(),
                )
                db.query("SELECT id, shelf, slot, vmcLane, maxCapacity, categoryId, productName, quantity FROM product_slots")
                    .use { cursor ->
                        val idIdx = cursor.getColumnIndex("id")
                        val shelfIdx = cursor.getColumnIndex("shelf")
                        val slotIdx = cursor.getColumnIndex("slot")
                        val laneIdx = cursor.getColumnIndex("vmcLane")
                        val capIdx = cursor.getColumnIndex("maxCapacity")
                        val catIdx = cursor.getColumnIndex("categoryId")
                        val nameIdx = cursor.getColumnIndex("productName")
                        val qtyIdx = cursor.getColumnIndex("quantity")
                        while (cursor.moveToNext()) {
                            val categoryId = cursor.getString(catIdx).orEmpty()
                            val productName = cursor.getString(nameIdx).orEmpty()
                            val quantity = cursor.getInt(qtyIdx).coerceAtLeast(0)
                            val queueJson = buildLegacyQueueJson(categoryId, productName, quantity)
                            db.execSQL(
                                "INSERT INTO spirals (id, shelf, slot, vmcLane, maxCapacity, queueJson) VALUES (?, ?, ?, ?, ?, ?)",
                                arrayOf(
                                    cursor.getString(idIdx),
                                    cursor.getInt(shelfIdx),
                                    cursor.getInt(slotIdx),
                                    cursor.getInt(laneIdx),
                                    cursor.getInt(capIdx),
                                    queueJson,
                                ),
                            )
                        }
                    }
                db.execSQL("DROP TABLE product_slots")
            }

            private fun buildLegacyQueueJson(categoryId: String, productName: String, quantity: Int): String {
                if (quantity <= 0 || categoryId.isBlank()) return "[]"
                val escapedCat = categoryId.replace("\\", "\\\\").replace("\"", "\\\"")
                val escapedName = productName.replace("\\", "\\\\").replace("\"", "\\\"")
                val items = (0 until quantity).joinToString(",") {
                    """{"categoryId":"$escapedCat","productName":"$escapedName"}"""
                }
                return "[$items]"
            }
        }

        internal val MIGRATIONS: Array<Migration> = arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6,
            MIGRATION_6_7,
        )

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "zura_touch.db",
            )
                .addMigrations(*MIGRATIONS)
                .apply {
                    if (BuildConfig.DEBUG) {
                        fallbackToDestructiveMigration()
                    }
                }
                .build()
                .also { instance = it }
        }

        suspend fun seedIfEmpty(database: AppDatabase) {
            if (database.categoryDao().getAll().isEmpty()) {
                database.categoryDao().insertAll(MachineLayoutSeed.defaultCategories())
            }
            if (database.spiralDao().getAll().isNotEmpty()) return
            database.spiralDao().insertAll(MachineLayoutSeed.allPhysicalSpirals())
            if (database.appConfigDao().get() == null) {
                database.appConfigDao().insert(
                    AppConfigEntity(
                        spinPriceCents = 200,
                        serialPortPath = "/dev/ttyS0",
                        mysteryBoxLabel = "Mystery Box",
                        operatorPin = "",
                        soundEnabled = true,
                    ),
                )
            }
        }
    }
}
