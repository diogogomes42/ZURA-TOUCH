package com.zuratouch.feature.salewheel

import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.File

class CustomerUiLeakTest {
    @Test
    fun `jackpot threshold is not exposed in customer strings`() {
        val stringsFile = locateStringsFile()
        val content = stringsFile.readText()
        listOf("6000", "6.000", "6,000", "pity", "garantia", "€6", "600000").forEach { forbidden ->
            assertFalse(
                "Customer strings must not contain '$forbidden'",
                content.contains(forbidden, ignoreCase = true),
            )
        }
    }

    private fun locateStringsFile(): File {
        val moduleRoot = File("src/main/res/values/strings.xml")
        if (moduleRoot.exists()) return moduleRoot
        return File("android/feature/sale-wheel/src/main/res/values/strings.xml")
    }
}
