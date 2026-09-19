package com.zuratouch.prizewheel.domain

/** Static digital catalog and direct-purchase pricing — independent from play tiers. */
object DirectPurchaseCatalog {
    private val allowedPhysicalCategories = setOf(
        PrizeProductCatalog.COMMON,
        PrizeProductCatalog.RARE,
    )

    fun isAllowedPhysicalCategory(categoryId: String): Boolean =
        categoryId in allowedPhysicalCategories

    fun physicalPriceCents(categoryId: String): Long = when (categoryId) {
        PrizeProductCatalog.COMMON -> 800L
        PrizeProductCatalog.RARE -> 1_500L
        else -> error("Direct purchase not allowed for category: $categoryId")
    }

    private data class DigitalProductDef(
        val productName: String,
        val priceCents: Long,
        val digitalType: DirectPurchaseDigitalType,
        val categoryLabel: String,
        val categoryColor: Long,
    )

    private val digitalProducts: List<DigitalProductDef> = listOf(
        DigitalProductDef(
            productName = "Experiência de Aventura",
            priceCents = 4_500L,
            digitalType = DirectPurchaseDigitalType.EXPERIENCE,
            categoryLabel = "Experiência",
            categoryColor = PrizeProductCatalog.catalogColorFor(PrizeProductCatalog.LEGENDARY),
        ),
        DigitalProductDef(
            productName = "Jantar para Duas Pessoas",
            priceCents = 5_500L,
            digitalType = DirectPurchaseDigitalType.EXPERIENCE,
            categoryLabel = "Experiência",
            categoryColor = PrizeProductCatalog.catalogColorFor(PrizeProductCatalog.LEGENDARY),
        ),
        DigitalProductDef(
            productName = "Bilhetes para Concerto",
            priceCents = 4_000L,
            digitalType = DirectPurchaseDigitalType.EXPERIENCE,
            categoryLabel = "Experiência",
            categoryColor = PrizeProductCatalog.catalogColorFor(PrizeProductCatalog.LEGENDARY),
        ),
        DigitalProductDef(
            productName = "Noite em Hotel",
            priceCents = 6_500L,
            digitalType = DirectPurchaseDigitalType.EXPERIENCE,
            categoryLabel = "Experiência",
            categoryColor = PrizeProductCatalog.catalogColorFor(PrizeProductCatalog.LEGENDARY),
        ),
        DigitalProductDef(
            productName = "Viagem",
            priceCents = 12_000L,
            digitalType = DirectPurchaseDigitalType.EXPERIENCE,
            categoryLabel = "Experiência",
            categoryColor = PrizeProductCatalog.catalogColorFor(PrizeProductCatalog.MYTHIC),
        ),
        DigitalProductDef(
            productName = "Cartão Presente (5€–10€)",
            priceCents = 1_000L,
            digitalType = DirectPurchaseDigitalType.GIFT_CARD,
            categoryLabel = "Gift Card",
            categoryColor = PrizeProductCatalog.catalogColorFor(PrizeProductCatalog.RARE),
        ),
        DigitalProductDef(
            productName = "Cartão Presente (20€–50€)",
            priceCents = 3_500L,
            digitalType = DirectPurchaseDigitalType.GIFT_CARD,
            categoryLabel = "Gift Card",
            categoryColor = PrizeProductCatalog.catalogColorFor(PrizeProductCatalog.EPIC),
        ),
    )

    fun digitalOffers(): List<DirectPurchaseOffer> =
        digitalProducts.map { product ->
            DirectPurchaseOffer(
                id = "digital:${product.digitalType.name.lowercase()}:${product.productName}",
                productName = product.productName,
                categoryId = when (product.digitalType) {
                    DirectPurchaseDigitalType.EXPERIENCE -> "experience"
                    DirectPurchaseDigitalType.GIFT_CARD -> "gift_card"
                },
                categoryLabel = product.categoryLabel,
                categoryColor = product.categoryColor,
                priceCents = product.priceCents,
                kind = DirectPurchaseKind.DIGITAL,
                section = DirectPurchaseSection.DIGITAL,
                digitalType = product.digitalType,
            )
        }
}
