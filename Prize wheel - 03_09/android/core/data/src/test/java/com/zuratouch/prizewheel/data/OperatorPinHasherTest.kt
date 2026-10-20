package com.zuratouch.prizewheel.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OperatorPinHasherTest {
    @Test fun `hash is stable and verifiable`() {
        val hashed = OperatorPinHasher.hash("123456")
        assertTrue(OperatorPinHasher.isHashed(hashed))
        assertTrue(OperatorPinHasher.verify("123456", hashed))
        assertFalse(OperatorPinHasher.verify("999999", hashed))
    }

    @Test fun `verify supports legacy plaintext until migrated`() {
        assertTrue(OperatorPinHasher.verify("123456", "123456"))
        assertFalse(OperatorPinHasher.verify("000000", "123456"))
    }

    @Test fun `hashIfNeeded leaves hashed values unchanged`() {
        val hashed = OperatorPinHasher.hash("654321")
        assertTrue(OperatorPinHasher.hashIfNeeded(hashed) == hashed)
    }

    @Test fun `isValidPin requires at least six digits`() {
        assertFalse(OperatorPinHasher.isValidPin("12345"))
        assertTrue(OperatorPinHasher.isValidPin("123456"))
        assertTrue(OperatorPinHasher.isValidPin("12345678"))
        assertFalse(OperatorPinHasher.isValidPin("123456789"))
        assertFalse(OperatorPinHasher.isValidPin("12ab56"))
    }
}
