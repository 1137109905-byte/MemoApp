package com.memo.app

import android.app.Application
import android.util.Log
import com.memo.app.data.AutoDeleteScheduler
import com.memo.app.data.MemoDatabase
import com.memo.app.ui.theme.ThemeManager

class MemoApplication : Application() {
    val database: MemoDatabase by lazy { MemoDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        try {
            ThemeManager.init(this)
        } catch (e: Exception) {
            Log.e("MemoApp", "ThemeManager init failed", e)
        }
        try {
            AutoDeleteScheduler.schedule(this)
        } catch (e: Exception) {
            Log.e("MemoApp", "AutoDeleteScheduler failed", e)
        }
    }
}
