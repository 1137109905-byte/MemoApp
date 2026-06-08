package com.memo.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "memos")
data class Memo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val isChecklist: Boolean = false,
    val isPinned: Boolean = false,
    val autoDelete: Boolean = false,
    val autoDeleteTime: Long = 0L,   // specific timestamp for auto-delete (0 = disabled)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "check_items")
data class CheckItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memoId: Long = 0,
    val text: String = "",
    val isChecked: Boolean = false,
    val order: Int = 0
)

data class MemoWithChecks(
    @Embedded val memo: Memo,
    @Relation(
        parentColumn = "id",
        entityColumn = "memoId"
    )
    val checkItems: List<CheckItem>
)

@Dao
interface MemoDao {
    @Transaction
    @Query("SELECT * FROM memos ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllMemosWithChecks(): Flow<List<MemoWithChecks>>

    @Transaction
    @Query("SELECT * FROM memos WHERE id = :id")
    fun getMemoWithChecks(id: Long): Flow<MemoWithChecks?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemo(memo: Memo): Long

    @Update
    suspend fun updateMemo(memo: Memo)

    @Delete
    suspend fun deleteMemo(memo: Memo)

    // Delete memos where autoDelete is true AND autoDeleteTime has passed
    @Query("DELETE FROM memos WHERE autoDelete = 1 AND autoDeleteTime > 0 AND autoDeleteTime < :now")
    suspend fun deleteExpiredMemos(now: Long): Int

    // Also support legacy: autoDelete=true with autoDeleteTime=0, delete those created before threshold
    @Query("DELETE FROM memos WHERE autoDelete = 1 AND autoDeleteTime = 0 AND createdAt < :threshold")
    suspend fun deleteLegacyExpiredMemos(threshold: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckItem(item: CheckItem): Long

    @Update
    suspend fun updateCheckItem(item: CheckItem)

    @Delete
    suspend fun deleteCheckItem(item: CheckItem)

    @Query("DELETE FROM check_items WHERE memoId = :memoId")
    suspend fun deleteCheckItemsByMemoId(memoId: Long)

    @Query("SELECT * FROM check_items WHERE memoId = :memoId ORDER BY `order` ASC")
    suspend fun getCheckItemsForMemo(memoId: Long): List<CheckItem>
}
