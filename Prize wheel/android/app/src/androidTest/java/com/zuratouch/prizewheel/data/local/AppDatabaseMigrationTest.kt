package com.zuratouch.prizewheel.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zuratouch.prizewheel.data.OperatorPinHasher
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {
    private val testDb = "migration-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    fun migrate1To2_addsWeightedCategoriesAndSaleLogs() {
        helper.createDatabase(testDb, 1).apply {
            execSQL(
                "INSERT INTO categories (id, label, color) VALUES ('drink', 'Bebida', ${0xFF2B6CB0})",
            )
            execSQL(
                "INSERT INTO app_config (id, spinPriceCents, serialPortPath, mysteryBoxLabel) " +
                    "VALUES (1, 200, '/dev/ttyS0', 'Mystery Box')",
            )
            close()
        }

        helper.runMigrationsAndValidate(testDb, 2, true, AppDatabase.MIGRATIONS[0]).apply {
            query("SELECT weight, icon FROM categories WHERE id = 'drink'").use { cursor ->
                require(cursor.moveToFirst())
                assertEquals(65, cursor.getInt(0))
                assertEquals("🥤", cursor.getString(1))
            }
            query("SELECT operatorPin, soundEnabled FROM app_config WHERE id = 1").use { cursor ->
                require(cursor.moveToFirst())
                assertEquals("1234", cursor.getString(0))
                assertEquals(1, cursor.getInt(1))
            }
            close()
        }
    }

    @Test
    fun migrate2To3_updatesDefaultOperatorPin() {
        helper.createDatabase(testDb, 2).apply {
            execSQL(
                "INSERT INTO app_config (id, spinPriceCents, serialPortPath, mysteryBoxLabel, operatorPin, soundEnabled) " +
                    "VALUES (1, 200, '/dev/ttyS0', 'Mystery Box', '1234', 1)",
            )
            close()
        }

        helper.runMigrationsAndValidate(testDb, 3, true, AppDatabase.MIGRATIONS[1]).apply {
            query("SELECT operatorPin FROM app_config WHERE id = 1").use { cursor ->
                require(cursor.moveToFirst())
                assertEquals("5018", cursor.getString(0))
            }
            close()
        }
    }

    @Test
    fun migrate3To4_hashesOperatorPin() {
        helper.createDatabase(testDb, 3).apply {
            execSQL(
                "INSERT INTO app_config (id, spinPriceCents, serialPortPath, mysteryBoxLabel, operatorPin, soundEnabled) " +
                    "VALUES (1, 200, '/dev/ttyS0', 'Mystery Box', '5018', 1)",
            )
            close()
        }

        helper.runMigrationsAndValidate(testDb, 4, true, AppDatabase.MIGRATIONS[2]).apply {
            query("SELECT operatorPin FROM app_config WHERE id = 1").use { cursor ->
                require(cursor.moveToFirst())
                assertEquals(OperatorPinHasher.hash("5018"), cursor.getString(0))
            }
            close()
        }
    }

    @Test
    fun migrate3To4_hashesOperatorPin() {
        helper.createDatabase(testDb, 3).apply {
            execSQL(
                "INSERT INTO app_config (id, spinPriceCents, serialPortPath, mysteryBoxLabel, operatorPin, soundEnabled) " +
                    "VALUES (1, 200, '/dev/ttyS0', 'Mystery Box', '5018', 1)",
            )
            close()
        }

        helper.runMigrationsAndValidate(testDb, 4, true, AppDatabase.MIGRATIONS[2]).apply {
            query("SELECT operatorPin FROM app_config WHERE id = 1").use { cursor ->
                require(cursor.moveToFirst())
                assertEquals(OperatorPinHasher.hash("5018"), cursor.getString(0))
            }
            close()
        }
    }

    @Test
    fun migrate4To5_clearsDefaultPins() {
        helper.createDatabase(testDb, 4).apply {
            execSQL(
                "INSERT INTO app_config (id, spinPriceCents, serialPortPath, mysteryBoxLabel, operatorPin, soundEnabled) " +
                    "VALUES (1, 200, '/dev/ttyS0', 'Mystery Box', '${OperatorPinHasher.hash("5018")}', 1)",
            )
            close()
        }

        helper.runMigrationsAndValidate(testDb, 5, true, AppDatabase.MIGRATIONS[3]).apply {
            query("SELECT operatorPin FROM app_config WHERE id = 1").use { cursor ->
                require(cursor.moveToFirst())
                assertEquals("", cursor.getString(0))
            }
            close()
        }
    }

    @Test
    fun migrate4To5_preservesCustomPin() {
        val customHash = OperatorPinHasher.hash("987654")
        helper.createDatabase(testDb, 4).apply {
            execSQL(
                "INSERT INTO app_config (id, spinPriceCents, serialPortPath, mysteryBoxLabel, operatorPin, soundEnabled) " +
                    "VALUES (1, 200, '/dev/ttyS0', 'Mystery Box', '$customHash', 1)",
            )
            close()
        }

        helper.runMigrationsAndValidate(testDb, 5, true, AppDatabase.MIGRATIONS[3]).apply {
            query("SELECT operatorPin FROM app_config WHERE id = 1").use { cursor ->
                require(cursor.moveToFirst())
                assertEquals(customHash, cursor.getString(0))
            }
            close()
        }
    }

    @Test
    fun migrate1To5_preservesExistingDataAndClearsDefaultPin() {
        helper.createDatabase(testDb, 1).apply {
            execSQL(
                "INSERT INTO categories (id, label, color) VALUES ('snack', 'Snack', ${0xFFB7791F})",
            )
            execSQL(
                "INSERT INTO product_slots (id, categoryId, productName, shelf, slot, vmcLane, quantity) " +
                    "VALUES ('B1', 'snack', 'Barra', 2, 1, 3, 5)",
            )
            execSQL(
                "INSERT INTO app_config (id, spinPriceCents, serialPortPath, mysteryBoxLabel) " +
                    "VALUES (1, 250, '/dev/ttyUSB0', 'Caixa Surpresa')",
            )
            close()
        }

        helper.runMigrationsAndValidate(testDb, 5, true, *AppDatabase.MIGRATIONS).apply {
            query("SELECT quantity FROM product_slots WHERE id = 'B1'").use { cursor ->
                require(cursor.moveToFirst())
                assertEquals(5, cursor.getInt(0))
            }
            query("SELECT mysteryBoxLabel, operatorPin FROM app_config WHERE id = 1").use { cursor ->
                require(cursor.moveToFirst())
                assertEquals("Caixa Surpresa", cursor.getString(0))
                assertEquals("", cursor.getString(1))
            }
            close()
        }
    }
}
