package com.zuratouch.prizewheel.domain

/** Full prize catalog for card faces — independent of current machine stock. */
object PrizeProductCatalog {
    const val COMMON = "common"
    const val RARE = "rare"
    const val EPIC = "epic"
    const val LEGENDARY = "legendary"
    const val MYTHIC = "mythic"

    val commonProducts = listOf(
        "Pato de Borracha",
        "Porta-chaves",
        "Livro",
        "Mini Ventoinha",
        "Meias",
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

    fun allProducts(): List<QueuedProduct> = buildList {
        commonProducts.forEach { add(QueuedProduct(COMMON, it)) }
        rareProducts.forEach { add(QueuedProduct(RARE, it)) }
        epicProducts.forEach { add(QueuedProduct(EPIC, it)) }
        legendaryProducts.forEach { add(QueuedProduct(LEGENDARY, it)) }
        mythicProducts.forEach { add(QueuedProduct(MYTHIC, it)) }
    }
}
