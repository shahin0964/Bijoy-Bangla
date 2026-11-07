package com.bijoy.bangla

import android.app.Application
import com.bijoy.bangla.data.ChatContextManager
import com.bijoy.bangla.data.HistoryDbHelper
import com.bijoy.bangla.data.PreferencesManager

class BijoyApp : Application() {

    companion object {
        lateinit var instance: BijoyApp
            private set
        val prefs: PreferencesManager by lazy { PreferencesManager(instance) }
        val dbHelper: HistoryDbHelper by lazy { HistoryDbHelper(instance) }
        val chatManager: ChatContextManager by lazy { ChatContextManager(instance) }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
