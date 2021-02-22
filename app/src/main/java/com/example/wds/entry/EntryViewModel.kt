package com.example.wds.entry

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EntryViewModel(application: Application) : AndroidViewModel(application) {
    val allEntries: LiveData<List<Entry>>
    private val repository: EntryRepository

    init {
        val entryDao = EntryDatabase.getDatabase(application).entryDao()
        repository = EntryRepository(entryDao)
        allEntries = repository.allEntries.asLiveData()
    }

    fun insert(entry: Entry) = GlobalScope.launch(Dispatchers.IO) { repository.insert(entry) }
//        .also { println("DEBUG AnimalViewModel->insert()") }
}