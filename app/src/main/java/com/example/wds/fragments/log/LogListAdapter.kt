package com.example.wds.fragments.log

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wds.R
import com.example.wds.databinding.LogItemBinding
import com.example.wds.entry.Entry

class LogListAdapter : RecyclerView.Adapter<LogListAdapter.LogViewHolder>() {
    private var logList = emptyList<Entry>()
    class LogViewHolder(private val binding: LogItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: Entry) {
            binding.tvAnimal.text = entry.textAnimal
            binding.tvTimestamp.text = entry.textTime

            val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_image_400)
                    .error(R.drawable.ic_image_400)
                    .fitCenter()

            Glide.with(itemView.context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(entry.imgSrc)
                    .into(binding.logPic)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        return LogViewHolder( LogItemBinding.inflate(LayoutInflater.from(parent.context), parent,false) )
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(logList[position])
    }

    override fun getItemCount() = logList.size

    fun setEntries(entries : List<Entry>) {
//        println("DEBUG LogListAdapter->setEntries()")
        logList = entries
        notifyDataSetChanged()
    }
}