package com.namaanda.termuxbridge.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.namaanda.termuxbridge.server.HttpServer
import java.io.IOException

class MyAccessibilityService : AccessibilityService() {

    private var httpServer: HttpServer? = null
    private val port = 8080

    override fun onServiceConnected() {
        super.onServiceConnected()
        httpServer = HttpServer(this, port)
        try {
            httpServer?.start()
            println("Server started on port $port")
        } catch (e: IOException) {
            e.printStackTrace()
            println("Server failed to start.")
        }
    }

    override fun onInterrupt() {
        // Not used
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used for now
    }

    override fun onDestroy() {
        super.onDestroy()
        httpServer?.stop()
        println("Server stopped.")
    }

    // --- LOGIKA UTAMA AKSI ---

    fun clickOnText(text: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val nodes = rootNode.findAccessibilityNodeInfosByText(text)
        var clicked = false
        nodes?.forEach { node ->
            // Cari node yang bisa diklik, atau parent-nya yang bisa diklik
            var clickableNode: AccessibilityNodeInfo? = node
            while (clickableNode != null) {
                if (clickableNode.isClickable) {
                    clickableNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    clicked = true
                    break
                }
                clickableNode = clickableNode.parent
            }
        }
        rootNode.recycle()
        return clicked
    }
}