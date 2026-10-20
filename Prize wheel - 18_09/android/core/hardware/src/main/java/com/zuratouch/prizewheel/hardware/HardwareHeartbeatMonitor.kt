package com.zuratouch.prizewheel.hardware

import com.zuratouch.prizewheel.vending.VmcProtocol
import com.zuratouch.prizewheel.vending.VmcResponse
import com.zuratouch.prizewheel.vending.VmcSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HardwareHeartbeatMonitor(
    private val scope: CoroutineScope,
    private val sessionProvider: () -> VmcSession?,
    private val serialPathProvider: suspend () -> String,
    private val onUnresponsive: suspend (HardwareStatus.Degraded) -> Unit,
) {
    fun start() {
        scope.launch {
            while (isActive) {
                delay(HEARTBEAT_INTERVAL_MS)
                val session = sessionProvider() ?: continue
                val response = session.send(VmcProtocol.deviceId())
                if (response !is VmcResponse.DeviceId) {
                    onUnresponsive(
                        HardwareStatus.Degraded(
                            serialPath = serialPathProvider(),
                            reason = "Controlador VMC não responde.",
                        ),
                    )
                }
            }
        }
    }

    companion object {
        private const val HEARTBEAT_INTERVAL_MS = 60_000L
    }
}
