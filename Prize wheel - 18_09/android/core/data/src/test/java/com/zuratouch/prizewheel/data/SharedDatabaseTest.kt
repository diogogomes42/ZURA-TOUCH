package com.zuratouch.prizewheel.data

import org.junit.Assert.assertEquals
import org.junit.Test

class SharedDatabaseTest {
    @Test
    fun ownerPackage_isCustomerApp() {
        assertEquals("com.zuratouch.customer", SharedDatabase.OWNER_PACKAGE)
    }

    @Test
    fun databaseName_matchesAppDatabase() {
        assertEquals(
            com.zuratouch.prizewheel.data.local.AppDatabase.DATABASE_NAME,
            SharedDatabase.DATABASE_NAME,
        )
    }
}
