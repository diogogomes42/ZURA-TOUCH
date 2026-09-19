package com.zuratouch.feature.salecards.components

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class ShowcaseGridMetricsTest {
    @Test
    fun `showcase uses wider spacing between cards`() {
        assertEquals(12.dp, CardGridSpec.SHOWCASE_SPACING)
    }
}
