package com.zuratouch.prizewheel.data

import java.security.MessageDigest

object OperatorPinHasher {
    const val MIN_LENGTH = 6
    const val MAX_LENGTH = 8

    private const val SALT = "zura_touch_prize_wheel_v1"

    fun isValidPin(pin: String): Boolean =
        pin.length in MIN_LENGTH..MAX_LENGTH && pin.all { it.isDigit() }

    fun hash(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest("$SALT:$pin".toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verify(pin: String, stored: String): Boolean {
        if (isHashed(stored)) return hash(pin) == stored
        return pin == stored
    }

    fun isHashed(value: String): Boolean =
        value.length == 64 && value.all { it in '0'..'9' || it in 'a'..'f' }

    fun hashIfNeeded(value: String): String = if (isHashed(value)) value else hash(value)
}
