package com.zuratouch.prizewheel.domain

/**
 * Isolated hook for future email / voucher delivery integrations.
 * Default implementation only persists pending orders locally.
 */
interface DigitalPurchaseFulfillmentGateway {
    suspend fun onDigitalPurchaseRegistered(record: DigitalPurchaseRecord)
}

object NoOpDigitalPurchaseFulfillmentGateway : DigitalPurchaseFulfillmentGateway {
    override suspend fun onDigitalPurchaseRegistered(record: DigitalPurchaseRecord) = Unit
}
