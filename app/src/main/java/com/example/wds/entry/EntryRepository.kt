package com.example.wds.entry

import kotlinx.coroutines.flow.Flow

class EntryRepository(private val entryDao: EntryDao) {
    val allEntries : Flow<List<Entry>> = entryDao.getAllEntries()

    suspend fun insert(entry: Entry) {
        entryDao.insert(entry)
    }
}