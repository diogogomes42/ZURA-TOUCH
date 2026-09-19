package com.zuratouch.prizewheel.domain

/** Builds direct-purchase offers from live machine stock and the static digital catalog. */
class DirectPurchaseEngine {
    fun buildCatalog(
        categories: List<PrizeCategory>,
        stock: List<Spiral>,
    ): DirectPurchaseCatalogSections {
        val machineOffers = physicalOffers(categories, stock)
        val digitalOffers = DirectPurchaseCatalog.digitalOffers()
        return DirectPurchaseCatalogSections(
            machineOffers = machineOffers,
            digitalOffers = digitalOffers,
        )
    }

    fun physicalOffers(
        categories: List<PrizeCategory>,
        stock: List<Spiral>,
    ): List<DirectPurchaseOffer> {
        val seenProducts = linkedSetOf<String>()
        return stock.mapNotNull { spiral ->
            val product = spiral.frontProduct ?: return@mapNotNull null
            val categoryId = PrizeProductCatalog.categoryIdForProduct(product.productName)
                ?: product.categoryId
            if (!DirectPurchaseCatalog.isAllowedPhysicalCategory(categoryId)) return@mapNotNull null
            if (!seenProducts.add(product.productName)) return@mapNotNull null
            val category = PrizeProductCatalog.resolveCategory(
                productName = product.productName,
                stockCategoryId = product.categoryId,
                categories = categories,
            )
            DirectPurchaseOffer(
                id = "physical:${spiral.id}:${product.productName}",
                productName = product.productName,
                categoryId = categoryId,
                categoryLabel = category.label,
                categoryColor = category.color,
                priceCents = DirectPurchaseCatalog.physicalPriceCents(categoryId),
                kind = DirectPurchaseKind.PHYSICAL,
                section = DirectPurchaseSection.MACHINE,
                dispensePlan = DispensePlan(spiral, product),
            )
        }
    }
}
