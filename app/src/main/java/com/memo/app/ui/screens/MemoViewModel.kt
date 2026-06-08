package com.memo.app.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.memo.app.MemoApplication
import com.memo.app.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MemoViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as MemoApplication).database.memoDao()

    val allMemos: StateFlow<List<MemoWithChecks>> = dao.getAllMemosWithChecks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val filteredMemos: StateFlow<List<MemoWithChecks>> = combine(allMemos, _searchQuery) { memos, query ->
        if (query.isBlank()) memos
        else memos.filter {
            it.memo.title.contains(query, ignoreCase = true) ||
            it.memo.content.contains(query, ignoreCase = true) ||
            it.checkItems.any { item -> item.text.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getMemoById(id: Long): Flow<MemoWithChecks?> = dao.getMemoWithChecks(id)

    fun createMemo(
        title: String,
        content: String,
        isChecklist: Boolean,
        autoDelete: Boolean,
        autoDeleteTime: Long = 0L
    ): Long {
        var memoId = 0L
        viewModelScope.launch {
            memoId = dao.insertMemo(
                Memo(
                    title = title,
                    content = content,
                    isChecklist = isChecklist,
                    autoDelete = autoDelete,
                    autoDeleteTime = autoDeleteTime
                )
            )
        }
        return memoId
    }

    fun updateMemo(memo: Memo) {
        viewModelScope.launch {
            dao.updateMemo(memo.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteMemo(memo: Memo) {
        viewModelScope.launch {
            dao.deleteCheckItemsByMemoId(memo.id)
            dao.deleteMemo(memo)
        }
    }

    fun togglePin(memo: Memo) {
        viewModelScope.launch {
            dao.updateMemo(memo.copy(isPinned = !memo.isPinned, updatedAt = System.currentTimeMillis()))
        }
    }

    // Set auto-delete with a specific time
    fun setAutoDelete(memo: Memo, enabled: Boolean, deleteTime: Long = 0L) {
        viewModelScope.launch {
            dao.updateMemo(memo.copy(
                autoDelete = enabled,
                autoDeleteTime = if (enabled) deleteTime else 0L,
                updatedAt = System.currentTimeMillis()
            ))
        }
    }

    // Quick: delete tomorrow at midnight
    fun setDeleteTomorrow(memo: Memo) {
        val tomorrowMidnight = java.util.Calendar.getInstance().apply {
            add(java.util.Calendar.DAY_OF_YEAR, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        setAutoDelete(memo, true, tomorrowMidnight)
    }

    fun insertCheckItem(item: CheckItem) {
        viewModelScope.launch { dao.insertCheckItem(item) }
    }

    fun updateCheckItem(item: CheckItem) {
        viewModelScope.launch { dao.updateCheckItem(item) }
    }

    fun deleteCheckItem(item: CheckItem) {
        viewModelScope.launch { dao.deleteCheckItem(item) }
    }

    suspend fun getCheckItems(memoId: Long): List<CheckItem> {
        return dao.getCheckItemsForMemo(memoId)
    }
}
