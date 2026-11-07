package com.bijoy.bangla.data

import android.content.Context
import android.content.SharedPreferences

data class SavedChatLayout(
    val key: String,
    val packageName: String,
    val contactName: String,
    val layoutId: String
)

class ChatContextManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("bijoy_chat_layout_memory", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        var currentPackage: String = ""
        @Volatile
        var currentContact: String = ""
        @Volatile
        var lastUpdatedTime: Long = 0L
    }

    fun updateActiveContext(packageName: String, contactName: String?) {
        currentPackage = packageName
        currentContact = contactName?.trim() ?: ""
        lastUpdatedTime = System.currentTimeMillis()
    }

    fun getActiveKey(): String {
        return if (currentContact.isNotBlank()) {
            "$currentPackage:$currentContact"
        } else {
            currentPackage
        }
    }

    fun getSavedLayoutForActiveChat(): String? {
        val key = getActiveKey()
        if (key.isBlank()) return null
        // First check exact contact key (package:contact)
        var layout = prefs.getString(key, null)
        // Fallback to package key if contact-specific layout is not set
        if (layout == null && currentPackage.isNotBlank()) {
            layout = prefs.getString(currentPackage, null)
        }
        return layout
    }

    fun saveLayoutForActiveChat(layoutId: String) {
        val key = getActiveKey()
        if (key.isNotBlank()) {
            prefs.edit().putString(key, layoutId).apply()
        }
    }

    fun getAllSavedMappings(): List<SavedChatLayout> {
        val all = prefs.all
        val list = mutableListOf<SavedChatLayout>()
        for ((k, v) in all) {
            val layoutId = v as? String ?: continue
            val parts = k.split(":", limit = 2)
            val pkg = parts.getOrNull(0) ?: ""
            val contact = parts.getOrNull(1) ?: "(App Default)"
            list.add(
                SavedChatLayout(
                    key = k,
                    packageName = pkg,
                    contactName = contact,
                    layoutId = layoutId
                )
            )
        }
        return list.sortedBy { it.contactName }
    }

    fun removeMapping(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
