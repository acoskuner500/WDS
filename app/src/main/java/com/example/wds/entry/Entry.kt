package com.example.wds.entry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entry_table")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val entryId : Int,
    val imgSrc : String,
    val textAnimal : String,
    val textTime : String
)
