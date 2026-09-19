package com.zuratouch.prizewheel.domain

/** Full prize catalog for card faces — independent of current machine stock. */
object PrizeProductCatalog {
    const val BASIC = "basic"
    const val COMMON = "common"
    const val RARE = "rare"
    const val EPIC = "epic"
    const val LEGENDARY = "legendary"
    const val MYTHIC = "mythic"

    val basicProducts = listOf(
        "Adesivo",
        "Pastilha",
        "Chaveiro Simples",
        "Balão",
        "Lápis",
        "Marcador",
    )

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
        "iPhone",
        "Jantar para Duas Pessoas",
        "Bilhetes para Concerto",
        "Camisola Oficial de Futebol",
        "Experiência de Aventura",
        "Noite em Hotel",
        "Bilhetes para Parque Temático",
    )

    val mythicProducts = listOf(
        "Viagem",
        "AirPods",
        "MacBook",
        "Trotinete Elétrica",
        "Bicicleta Elétrica",
        "Câmara GoPro",
    )

    fun productsFor(categoryId: String): List<String> = when (categoryId) {
        BASIC -> basicProducts
        COMMON -> commonProducts
        RARE -> rareProducts
        EPIC -> epicProducts
        LEGENDARY -> legendaryProducts
        MYTHIC -> mythicProducts
        else -> emptyList()
    }

    fun allProducts(): List<QueuedProduct> = buildList {
        basicProducts.forEach { add(QueuedProduct(BASIC, it)) }
        commonProducts.forEach { add(QueuedProduct(COMMON, it)) }
        rareProducts.forEach { add(QueuedProduct(RARE, it)) }
        epicProducts.forEach { add(QueuedProduct(EPIC, it)) }
        legendaryProducts.forEach { add(QueuedProduct(LEGENDARY, it)) }
        mythicProducts.forEach { add(QueuedProduct(MYTHIC, it)) }
    }

    private val categoryIdByProductName: Map<String, String> by lazy {
        allProducts().associate { it.productName to it.categoryId }
    }

    fun categoryIdForProduct(productName: String): String? = categoryIdByProductName[productName]

    fun catalogLabelFor(categoryId: String): String = when (categoryId) {
        BASIC -> "Básico"
        COMMON -> "Comum"
        RARE -> "Raro"
        EPIC -> "Épico"
        LEGENDARY -> "Lendário"
        MYTHIC -> "Mítico"
        else -> categoryId.replaceFirstChar { it.uppercase() }
    }

    fun catalogColorFor(categoryId: String): Long = when (categoryId) {
        BASIC -> 0xFFA0AEC0
        COMMON -> 0xFF718096
        RARE -> 0xFF3182CE
        EPIC -> 0xFF805AD5
        LEGENDARY -> 0xFFD69E2E
        MYTHIC -> 0xFFE53E3E
        else -> 0xFF718096
    }

    /** Display order: common → legendary (most rare last). */
    val rarityOrder: List<String> = listOf(BASIC, COMMON, RARE, EPIC, MYTHIC, LEGENDARY)

    /** Canonical tier for a product — catalog name wins over stock queue category. */
    fun resolveCategory(
        productName: String,
        stockCategoryId: String,
        categories: List<PrizeCategory>,
    ): PrizeCategory {
        val categoryId = categoryIdForProduct(productName) ?: stockCategoryId
        return categories.find { it.id == categoryId }
            ?: PrizeCategory(
                id = categoryId,
                label = catalogLabelFor(categoryId),
                color = catalogColorFor(categoryId),
            )
    }
}
