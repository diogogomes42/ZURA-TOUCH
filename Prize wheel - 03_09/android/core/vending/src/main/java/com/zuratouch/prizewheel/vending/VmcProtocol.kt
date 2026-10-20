package com.zuratouch.prizewheel.vending

/**
 * Reyeah VMC protocol codec.
 * UART settings: 9600 bps, 8 data bits, 1 stop bit, no parity, no flow control.
 * Frame: FF 00 [header] [command] [payload length] [payload] [checksum].
 * The checksum is the low byte of header + command + payload length + payload.
 */
object VmcProtocol {
    private const val ADDRESS = 0xFF
    private const val FRAME_NUMBER = 0x00
    private const val APP_HEADER = 0x55
    private const val VMC_HEADER = 0xAA

    object Command {
        const val DEVICE_ID = 0x31
        const val DISPENSE = 0x41
        const val PAYMENT = 0x11
        const val CLEAR_FAULT = 0xA2
        const val REFUND_COINS = 0xB1
        const val CANCEL_CASHLESS = 0xB2
        const val QUERY_STATUS = 0xE1
        const val QUERY_CHANGE_STATUS = 0x07
    }

    enum class PaymentMethod(val value: Int) {
        CANCEL(0), COINS(1), CASHLESS(2), BANKNOTE(3)
    }

    fun deviceId(): ByteArray = command(Command.DEVICE_ID, byteArrayOf(0xAD.toByte()))

    fun dispense(lane: Int, quantity: Int = 1): ByteArray {
        require(lane in 1..255) { "Lane must be between 1 and 255" }
        require(quantity in 1..255) { "Quantity must be between 1 and 255" }
        return command(Command.DISPENSE, byteArrayOf(lane.toByte(), quantity.toByte()))
    }

    /** Amount is in euro cents. The VMC requires a four-byte little-endian integer. */
    fun startPayment(amountCents: Long, method: PaymentMethod, lane: Int): ByteArray {
        require(amountCents in 0..0xFFFF_FFFFL) { "Amount is outside VMC range" }
        require(lane in 1..255) { "Lane must be between 1 and 255" }
        return command(Command.PAYMENT, littleEndianUInt(amountCents) + byteArrayOf(method.value.toByte(), lane.toByte()))
    }

    fun queryStatus(lane: Int, quantity: Int = 1): ByteArray = command(Command.QUERY_STATUS, byteArrayOf(lane.toByte(), quantity.toByte()))
    fun clearFault(): ByteArray = command(Command.CLEAR_FAULT, byteArrayOf(0xFF.toByte()))
    fun refundCoins(): ByteArray = command(Command.REFUND_COINS, byteArrayOf(0xFF.toByte()))
    fun cancelCashless(): ByteArray = command(Command.CANCEL_CASHLESS, byteArrayOf(0xFF.toByte()))

    fun parse(frame: ByteArray): VmcResponse {
        require(frame.size >= 6) { "Frame is too short" }
        require(frame[0].unsigned() == ADDRESS && frame[1].unsigned() == FRAME_NUMBER) { "Unexpected frame prefix" }
        require(frame[2].unsigned() == VMC_HEADER) { "Frame is not a VMC response" }
        val payloadSize = frame[4].unsigned()
        require(frame.size == payloadSize + 6) { "Frame length does not match payload length" }
        require(frame.last().unsigned() == checksum(frame.copyOfRange(2, frame.lastIndex))) { "Invalid VMC checksum" }
        val command = frame[3].unsigned()
        val payload = frame.copyOfRange(5, frame.lastIndex)
        return when (command) {
            Command.DEVICE_ID -> VmcResponse.DeviceId(payload.toString(Charsets.US_ASCII).trimEnd('\u0000'))
            Command.DISPENSE -> VmcResponse.DispenseAccepted(payload.firstOrNull()?.unsigned() ?: 0, payload.getOrNull(1)?.unsigned() ?: 0)
            Command.QUERY_STATUS -> when (payload.size) {
                1 -> if (payload[0].unsigned() == 0x01) VmcResponse.DeliveryCompleted else VmcResponse.DeliveryFailed(payload[0].unsigned())
                4 -> VmcResponse.PaymentCompleted(readLittleEndianUInt(payload))
                else -> VmcResponse.Unknown(command, payload)
            }
            else -> VmcResponse.Acknowledged(command, payload)
        }
    }

    private fun command(command: Int, payload: ByteArray): ByteArray {
        require(payload.size <= 255)
        val unverified = byteArrayOf(ADDRESS.toByte(), FRAME_NUMBER.toByte(), APP_HEADER.toByte(), command.toByte(), payload.size.toByte()) + payload
        return unverified + checksum(unverified.copyOfRange(2, unverified.size)).toByte()
    }

    private fun checksum(bytes: ByteArray): Int = bytes.fold(0) { sum, byte -> (sum + byte.unsigned()) and 0xFF }
    private fun littleEndianUInt(value: Long) = ByteArray(4) { index -> ((value shr (index * 8)) and 0xFF).toByte() }
    private fun readLittleEndianUInt(bytes: ByteArray): Long = bytes.foldIndexed(0L) { index, value, byte -> value or (byte.unsigned().toLong() shl (8 * index)) }
    private fun Byte.unsigned(): Int = toInt() and 0xFF
}

sealed interface VmcResponse {
    data class DeviceId(val value: String) : VmcResponse
    data class DispenseAccepted(val lane: Int, val quantity: Int) : VmcResponse
    data object DeliveryCompleted : VmcResponse
    data class DeliveryFailed(val code: Int) : VmcResponse
    data class PaymentCompleted(val amountCents: Long) : VmcResponse
    data class Acknowledged(val command: Int, val payload: ByteArray) : VmcResponse
    data class Unknown(val command: Int, val payload: ByteArray) : VmcResponse
}
