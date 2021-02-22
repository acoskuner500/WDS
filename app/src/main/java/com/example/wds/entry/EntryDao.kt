package com.example.wds.entry

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Entry)
    @Query("SELECT * FROM entry_table ORDER BY textTime DESC")
    fun getAllEntries() : Flow<List<Entry>>
}