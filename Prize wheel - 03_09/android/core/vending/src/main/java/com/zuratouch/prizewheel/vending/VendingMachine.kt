package com.zuratouch.prizewheel.vending

import com.zuratouch.prizewheel.domain.DispenseResult
import com.zuratouch.prizewheel.domain.Spiral

/** Replace FakeVendingMachine with the manufacturer's UART implementation. */
interface VendingMachine {
    suspend fun dispense(spiral: Spiral): DispenseResult
}
