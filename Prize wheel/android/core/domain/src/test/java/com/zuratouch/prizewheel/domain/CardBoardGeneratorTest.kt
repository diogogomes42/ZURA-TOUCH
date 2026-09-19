package com.zuratouch.prizewheel.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class CardBoardGeneratorTest {
    private val engine = PrizeEngine()
    private val generator = CardBoardGenerator(engine)

    private val categories = listOf(
        PrizeCategory("common", "Comum", 0xFF718096, weight = 90),
        PrizeCategory("premium", "Premium", 0xFF3182CE, weight = 10),
        PrizeCategory("snack", "Snack", 0xFFB7791F, weight = 30),
    )

    private fun spiral(
        id: String,
        shelf: Int,
        slot: Int,
        queue: List<QueuedProduct>,
    ) = Spiral(id, shelf, slot, shelf * 10 + slot, 15, queue)

    private fun wellStockedMachine() = listOf(
        spiral("A1", 1, 1, List(5) { QueuedProduct("common", "Água") }),
        spiral("B1", 1, 2, List(3) { QueuedProduct("premium", "Caixa Premium") }),
        spiral("C1", 2, 1, List(4) { QueuedProduct("snack", "Barra") }),
    )

    @Test
    fun `generate produces exactly 24 slots`() {
        val board = generator.generate(categories, wellStockedMachine())
        assertNotNull(board)
        assertEquals(24, board!!.slots.size)
    }

    @Test
    fun `every slot has a display product`() {
        val board = generator.generate(categories, wellStockedMachine())!!
        board.slots.forEach { slot ->
            assertTrue(slot.displayProduct.productName.isNotBlank())
        }
    }

    @Test
    fun `every slot has a valid dispense plan`() {
        val board = generator.generate(categories, wellStockedMachine())!!
        board.slots.forEach { slot ->
            assertFalse(slot.dispensePlan.spiral.isEmpty)
            assertNotNull(slot.dispensePlan.product.productName)
            assertTrue(slot.dispensePlan.product.productName.isNotBlank())
        }
    }

    @Test
    fun `exactly one target slot by default`() {
        val board = generator.generate(categories, wellStockedMachine())!!
        assertEquals(1, board.slots.count { it.isTargetSlot })
    }

    @Test
    fun `shuffle changes slot positions across seeds`() {
        val stock = wellStockedMachine()
        val boardA = CardBoardGenerator(engine, Random(1)).generate(categories, stock)!!
        val boardB = CardBoardGenerator(engine, Random(999)).generate(categories, stock)!!
        val orderA = boardA.slots.map { it.dispensePlan.spiral.id to it.isTargetSlot }
        val orderB = boardB.slots.map { it.dispensePlan.spiral.id to it.isTargetSlot }
        assertNotEquals(orderA, orderB)
    }

    @Test
    fun `decoy slots show shuffled catalog products beyond machine stock`() {
        val sparseStock = listOf(
            spiral("only", 1, 1, listOf(QueuedProduct("common", "Único"))),
            spiral("premium", 1, 2, listOf(QueuedProduct("premium", "Prémio"))),
        )
        val board = generator.generate(categories, sparseStock)!!
        assertEquals(24, board.slots.size)
        val machineProducts = sparseStock.flatMap { it.queue.map { product -> product.productName } }.toSet()
        val decoyNames = board.slots.filter { !it.isTargetSlot }.map { it.displayProduct.productName }
        assertTrue(decoyNames.any { it !in machineProducts })
        assertTrue(decoyNames.distinct().size > 1)
    }

    @Test
    fun `playable tiers exclude incompatible price levels`() {
        val stock = listOf(
            spiral("basic", 1, 1, listOf(QueuedProduct(PrizeProductCatalog.BASIC, "Adesivo"))),
            spiral("common", 1, 2, listOf(QueuedProduct(PrizeProductCatalog.COMMON, "Boné"))),
        )
        val categories = listOf(
            PrizeCategory(PrizeProductCatalog.BASIC, "Básico", 0xFFA0AEC0),
            PrizeCategory(PrizeProductCatalog.COMMON, "Comum", 0xFF718096),
            PrizeCategory(PrizeProductCatalog.RARE, "Raro", 0xFF3182CE),
        )
        val playable = generator.playableTiers(categories, stock, JackpotConfig.DEFAULT).map { it.id }
        assertTrue(PrizeTierCatalog.LITE in playable)
        assertTrue(PrizeTierCatalog.PLUS in playable)
        assertFalse(PrizeTierCatalog.PRO in playable)
        assertFalse(PrizeTierCatalog.PRIME in playable)
    }

    @Test
    fun `blocks when nothing is dispensable at front`() {
        val blockedStock = listOf(
            spiral("1", 1, 1, listOf(QueuedProduct("orphan", "Bloqueado"), QueuedProduct("common", "Frente"))),
        )
        assertFalse(generator.canGenerateBoard(categories, blockedStock))
        assertNull(generator.generate(categories, blockedStock))
    }

    @Test
    fun `blocks when machine has no front products`() {
        val emptyFront = listOf(
            spiral("1", 1, 1, emptyList()),
            spiral("2", 1, 2, emptyList()),
        )
        assertFalse(generator.canGenerateBoard(categories, emptyFront))
        assertNull(generator.generate(categories, emptyFront))
    }

    @Test
    fun `target slot uses weighted premium category logic`() {
        val stock = listOf(
            spiral("common", 1, 1, List(10) { QueuedProduct("common", "Comum") }),
            spiral("premium", 1, 2, List(10) { QueuedProduct("premium", "Prémio") }),
        )
        val weightedGenerator = CardBoardGenerator(PrizeEngine(Random(1)))
        val targetIds = List(500) {
            weightedGenerator.generate(categories, stock)!!
                .slots.single { it.isTargetSlot }
                .category.id
        }
        val premiumCount = targetIds.count { it == "premium" }
        assertTrue(premiumCount in 20..120)
    }

    @Test
    fun `slot indices are sequential after shuffle`() {
        val board = generator.generate(categories, wellStockedMachine())!!
        assertEquals((0 until 24).toList(), board.slots.map { it.index })
    }

    @Test
    fun `same seed produces reproducible board layout`() {
        val stock = wellStockedMachine()
        val gen = CardBoardGenerator(PrizeEngine(Random(42)), Random(42))
        val first = gen.generate(categories, stock, generatedAtMs = 0)!!
        val second = CardBoardGenerator(PrizeEngine(Random(42)), Random(42))
            .generate(categories, stock, generatedAtMs = 0)!!
        assertEquals(
            first.slots.map { Triple(it.index, it.dispensePlan.spiral.id, it.isTargetSlot) },
            second.slots.map { Triple(it.index, it.dispensePlan.spiral.id, it.isTargetSlot) },
        )
        assertEquals(first.boardSeed, second.boardSeed)
    }
}
