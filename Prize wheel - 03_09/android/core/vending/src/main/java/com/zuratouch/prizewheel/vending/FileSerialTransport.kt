package com.zuratouch.prizewheel.vending

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Reads and writes raw bytes on a Linux serial device path (e.g. /dev/ttyS0).
 * Configures 9600 8N1 via stty when available.
 */
class FileSerialTransport(private val devicePath: String) : SerialTransport {
    private var input: FileInputStream? = null
    private var output: FileOutputStream? = null
    private val frameReader = VmcFrameReader()

    override suspend fun open(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(devicePath)
            require(file.exists()) { "Serial device not found: $devicePath" }
            configurePort(devicePath)
            input = FileInputStream(file)
            output = FileOutputStream(file)
            frameReader.reset()
        }
    }

    override suspend fun write(data: ByteArray) {
        withContext(Dispatchers.IO) {
            output?.write(data) ?: error("Serial port is not open")
            output?.flush()
        }
    }

    override suspend fun readFrame(timeoutMs: Long): ByteArray? = withContext(Dispatchers.IO) {
        val stream = input ?: return@withContext null
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (stream.available() > 0) {
                val frame = frameReader.append(stream.read().toByte())
                if (frame != null) return@withContext frame
            } else {
                delay(10)
            }
        }
        null
    }

    override suspend fun discardInput() = withContext(Dispatchers.IO) {
        val stream = input ?: return@withContext
        while (stream.available() > 0) {
            stream.read()
        }
        frameReader.reset()
    }

    override suspend fun close() = withContext(Dispatchers.IO) {
        input?.close()
        output?.close()
        input = null
        output = null
        frameReader.reset()
    }

    override fun isAvailable(): Boolean = input != null && output != null

    private fun configurePort(path: String) {
        val process = ProcessBuilder("stty", "-F", path, "9600", "cs8", "-cstopb", "-parenb")
            .redirectErrorStream(true)
            .start()
        val exitCode = process.waitFor()
        require(exitCode == 0) { "Failed to configure serial port (stty exit code $exitCode)" }
    }
}
