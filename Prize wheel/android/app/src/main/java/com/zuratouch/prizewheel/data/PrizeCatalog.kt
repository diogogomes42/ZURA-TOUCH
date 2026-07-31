package com.zuratouch.prizewheel.data

import com.zuratouch.prizewheel.domain.QueuedProduct
import kotlin.random.Random

/** Official prize catalog — product names per wheel category. */
object PrizeCatalog {
    const val COMMON = "common"
    const val RARE = "rare"
    const val EPIC = "epic"
    const val LEGENDARY = "legendary"
    const val MYTHIC = "mythic"

    val categoryWeights = listOf(
        COMMON to 50,
        RARE to 25,
        EPIC to 15,
        LEGENDARY to 7,
        MYTHIC to 3,
    )

    val commonProducts = listOf(
        "Pato de Borracha",
        "Porta-chaves",
        "Livro",
        "Mini Ventoinha",
        "Meias",
        "Caneca",
        "Boné",
        "Saco Reutilizável",
    )

    val rareProducts = listOf(
        "Garrafa Reutilizável",
        "Cartas Pokémon",
        "Peluche",
        "Cartão Presente (5€–10€)",
        "Auriculares",
        "Kit de Beleza",
        "Livro Premium",
    )

    val epicProducts = listOf(
        "Funko Pop",
        "Headphones",
        "Teclado Gaming",
        "Rato Gaming",
        "Smartband",
        "Power Bank",
        "Cartão Presente (20€–50€)",
    )

    val legendaryProducts = listOf(
        "Jantar para Duas Pessoas",
        "Bilhetes para Concerto",
        "Camisola Oficial de Futebol",
        "Experiência de Aventura",
        "Noite em Hotel",
        "Bilhetes para Parque Temático",
    )

    val mythicProducts = listOf(
        "iPhone",
        "Viagem",
        "AirPods",
        "MacBook",
        "Trotinete Elétrica",
        "Bicicleta Elétrica",
        "Câmara GoPro",
    )

    fun productsFor(categoryId: String): List<String> = when (categoryId) {
        COMMON -> commonProducts
        RARE -> rareProducts
        EPIC -> epicProducts
        LEGENDARY -> legendaryProducts
        MYTHIC -> mythicProducts
        else -> emptyList()
    }

    /**
     * Builds [count] units for the test machine, spread across categories using the same
     * weights as the wheel (50 / 25 / 15 / 7 / 3) so stock levels differ per tier.
     */
    fun testMachinePool(count: Int, random: Random = Random(42)): List<QueuedProduct> {
        val totalWeight = categoryWeights.sumOf { it.second }
        return List(count) {
            var roll = random.nextInt(totalWeight)
            val categoryId = categoryWeights.first { (_, weight) ->
                roll -= weight
                roll < 0
            }.first
            val names = productsFor(categoryId)
            QueuedProduct(categoryId, names[random.nextInt(names.size)])
        }.shuffled(random)
    }
}
