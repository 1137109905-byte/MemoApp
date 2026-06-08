package com.memo.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.memo.app.data.MemoDatabase

class AutoDeleteWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = MemoDatabase.getDatabase(applicationContext)
        val dao = db.memoDao()
        val now = System.currentTimeMillis()

        // Delete memos with specific autoDeleteTime that has passed
        dao.deleteExpiredMemos(now)

        // Delete legacy auto-delete memos (no specific time, created before today)
        val todayMidnight = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        dao.deleteLegacyExpiredMemos(todayMidnight)

        return Result.success()
    }
}
