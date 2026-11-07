package com.bijoy.bangla.accessibility

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.bijoy.bangla.BijoyApp

class ChatContextService : AccessibilityService() {

    private val supportedApps = setOf(
        "com.facebook.orca",
        "com.facebook.katana",
        "com.whatsapp",
        "com.whatsapp.w4b",
        "org.telegram.messenger",
        "org.telegram.plus",
        "com.instagram.android",
        "com.google.android.apps.messaging",
        "com.samsung.android.messaging",
        "com.imo.android.imoim",
        "com.viber.voip",
        "com.discord"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val pkg = event.packageName?.toString() ?: return

        if (!supportedApps.contains(pkg)) {
            BijoyApp.chatManager.updateActiveContext(pkg, null)
            return
        }

        val rootNode = rootInActiveWindow ?: return
        try {
            val contactName = extractChatContactName(rootNode, pkg)
            BijoyApp.chatManager.updateActiveContext(pkg, contactName)
        } catch (_: Exception) {
            BijoyApp.chatManager.updateActiveContext(pkg, null)
        } finally {
            try {
                rootNode.recycle()
            } catch (_: Exception) {}
        }
    }

    private fun extractChatContactName(root: AccessibilityNodeInfo, pkg: String): String? {
        // Strategy 1: Look for common toolbar/actionbar title IDs
        val commonIdKeywords = listOf("title", "name", "conversation_title", "chat_title", "toolbar_title", "action_bar_title", "user_name")
        for (kw in commonIdKeywords) {
            val nodes = root.findAccessibilityNodeInfosByViewId("$pkg:id/$kw")
            if (nodes != null && nodes.isNotEmpty()) {
                val text = nodes[0].text?.toString()
                if (!text.isNullOrBlank() && text.length < 50) {
                    return text.trim()
                }
            }
        }

        // Strategy 2: Traverse upper screen nodes (header area) to find contact title
        val outRect = Rect()
        var candidateName: String? = null

        fun traverse(node: AccessibilityNodeInfo, depth: Int) {
            if (depth > 6 || candidateName != null) return

            node.getBoundsInScreen(outRect)
            // Look only in the top 220 pixels of the screen (typically header area)
            if (outRect.top >= 0 && outRect.bottom <= 450) {
                val text = node.text?.toString()
                if (!text.isNullOrBlank() &&
                    text.length in 2..40 &&
                    !text.equals("search", ignoreCase = true) &&
                    !text.equals("back", ignoreCase = true) &&
                    !text.equals("messages", ignoreCase = true) &&
                    !text.equals("chats", ignoreCase = true)
                ) {
                    // Check if parent or class looks like TextView
                    if (node.className?.contains("TextView", ignoreCase = true) == true) {
                        candidateName = text.trim()
                        return
                    }
                }
            }

            for (i in 0 until node.childCount) {
                val child = node.getChild(i) ?: continue
                traverse(child, depth + 1)
                try { child.recycle() } catch (_: Exception) {}
            }
        }

        traverse(root, 0)
        return candidateName
    }

    override fun onInterrupt() {}
}
