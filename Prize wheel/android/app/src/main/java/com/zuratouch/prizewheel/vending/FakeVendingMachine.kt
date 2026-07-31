package com.zuratouch.prizewheel.vending

import com.zuratouch.prizewheel.domain.DispenseResult
import com.zuratouch.prizewheel.domain.Spiral
import kotlinx.coroutines.delay

class FakeVendingMachine : VendingMachine {
    override suspend fun dispense(spiral: Spiral): DispenseResult {
        delay(1_200)
        return DispenseResult.Delivered
    }
}
