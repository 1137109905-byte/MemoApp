package com.memo.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.memo.app.data.AutoDeleteScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            AutoDeleteScheduler.schedule(context)
        }
    }
}
