package com.zuratouch.prizewheel.domain

enum class DirectPurchaseKind {
    PHYSICAL,
    DIGITAL,
}

enum class DirectPurchaseSection {
    MACHINE,
    DIGITAL,
}

enum class DirectPurchaseDigitalType {
    EXPERIENCE,
    GIFT_CARD,
}

data class DirectPurchaseOffer(
    val id: String,
    val productName: String,
    val categoryId: String,
    val categoryLabel: String,
    val categoryColor: Long,
    val priceCents: Long,
    val kind: DirectPurchaseKind,
    val section: DirectPurchaseSection,
    val digitalType: DirectPurchaseDigitalType? = null,
    val dispensePlan: DispensePlan? = null,
)

data class DirectPurchaseCatalogSections(
    val machineOffers: List<DirectPurchaseOffer>,
    val digitalOffers: List<DirectPurchaseOffer>,
) {
    val allOffers: List<DirectPurchaseOffer> = machineOffers + digitalOffers
}

enum class DigitalPurchaseStatus {
    PENDING_FULFILLMENT,
}

data class DigitalPurchaseRecord(
    val id: Long,
    val timestampMs: Long,
    val productName: String,
    val categoryLabel: String,
    val priceCents: Long,
    val customerEmail: String,
    val status: DigitalPurchaseStatus,
)
