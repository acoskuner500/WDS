package com.example.wds.fragments.log

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wds.utilities.Entry
import com.example.wds.utilities.GlideApp
import com.example.wds.R
import com.example.wds.databinding.LogItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class LogListAdapter(options: FirestoreRecyclerOptions<Entry>) :
    FirestoreRecyclerAdapter<Entry, LogListAdapter.LogViewHolder>(options) {
    class LogViewHolder(private val binding: LogItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: Entry) {
            binding.tvAnimal.text = entry.getTextAnimal()
            binding.tvTimestamp.text = entry.getTextTime()

            GlideApp.with(itemView.context)
                .load(entry.getImgSrc())
                .placeholder(R.drawable.ic_image_400)
                .error(R.drawable.ic_image_400)
                .fitCenter()
                .into(binding.logPic)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        return LogViewHolder( LogItemBinding.inflate(LayoutInflater.from(parent.context), parent,false) )
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int, model: Entry) {
        holder.bind(model)
    }

    override fun getItemCount() = snapshots.size
}