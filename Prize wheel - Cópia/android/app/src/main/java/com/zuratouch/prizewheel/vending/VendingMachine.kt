package com.zuratouch.prizewheel.vending

import com.zuratouch.prizewheel.domain.DispenseResult
import com.zuratouch.prizewheel.domain.ProductSlot

/** Replace FakeVendingMachine with the manufacturer's UART implementation. */
interface VendingMachine {
    suspend fun dispense(slot: ProductSlot): DispenseResult
}
