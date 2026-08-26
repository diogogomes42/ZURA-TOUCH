package com.zuratouch.prizewheel.data

import com.zuratouch.prizewheel.domain.CardBoard
import com.zuratouch.prizewheel.domain.CardGameSession
import com.zuratouch.prizewheel.domain.CardSlot
import com.zuratouch.prizewheel.domain.DispensePlan
import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.domain.QueuedProduct
import com.zuratouch.prizewheel.domain.Spiral
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardGameSessionCodecTest {
    private val board = CardBoard(
        slots = listOf(sampleSlot(0, true), sampleSlot(1, false)),
        generatedAtMs = 1_700_000_000_000,
        boardSeed = 42L,
    )

    @Test
    fun encodeDecode_roundTrip_preservesSession() {
        val session = CardGameSession(board = board, paymentConfirmed = true, selectedSlot = board.slots.first())
        val json = CardGameSessionCodec.encode(session)
        val restored = CardGameSessionCodec.decode(json)
        assertNotNull(restored)
        assertTrue(restored!!.paymentConfirmed)
        assertEquals(42L, restored.board.boardSeed)
        assertEquals(2, restored.board.slots.size)
        assertEquals(0, restored.selectedSlot?.index)
        assertTrue(restored.selectedSlot!!.isTargetSlot)
    }

    @Test
    fun decode_invalidJson_returnsNull() {
        assertEquals(null, CardGameSessionCodec.decode("{not-json"))
    }

    private fun sampleSlot(index: Int, isTarget: Boolean): CardSlot {
        val category = PrizeCategory("common", "Comum", 0xFF718096)
        val product = QueuedProduct("common", "Água")
        val spiral = Spiral("A1", 1, 1, 11, 10, listOf(product))
        return CardSlot(index, DispensePlan(spiral, product), category, isTarget)
    }
}
