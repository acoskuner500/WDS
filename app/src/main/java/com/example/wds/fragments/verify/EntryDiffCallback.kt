package com.example.wds.fragments.verify

import androidx.recyclerview.widget.DiffUtil
import com.example.wds.entry.Entry

class EntryDiffCallback(
        private val old: List<Entry>,
        private val new: List<Entry>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].entryId == new[newPosition].entryId
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}
