package com.memo.app.data

import android.content.Context
import androidx.work.*
import com.memo.app.worker.AutoDeleteWorker
import java.util.concurrent.TimeUnit

object AutoDeleteScheduler {
    private const val WORK_NAME = "auto_delete_memos"

    fun schedule(context: Context) {
        val now = java.util.Calendar.getInstance()
        val midnight = java.util.Calendar.getInstance().apply {
            add(java.util.Calendar.DAY_OF_YEAR, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val delay = midnight.timeInMillis - now.timeInMillis

        val request = PeriodicWorkRequestBuilder<AutoDeleteWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }

    fun runOnceNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<AutoDeleteWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
