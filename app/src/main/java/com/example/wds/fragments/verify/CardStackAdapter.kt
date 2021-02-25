package com.example.wds.fragments.verify

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wds.GlideApp
import com.example.wds.R
import com.example.wds.databinding.VerifyItemBinding
import com.example.wds.entry.Entry

class CardStackAdapter(private var entries : List<Entry>) : RecyclerView.Adapter<CardStackAdapter.CardViewHolder>() {
    class CardViewHolder(private val binding: VerifyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: Entry) {
            val filename = entry.filename
            binding.tvAnimal.text = filename.substring(0,filename.length-8)
//            binding.tvAnimal.text = entry.textAnimal
            binding.tvTimestamp.text = entry.textTime

            GlideApp.with(itemView.context)
                .load(entry.imgSrc)
                .placeholder(R.drawable.ic_image_400)
                .error(R.drawable.ic_image_400)
                .fitCenter()
                .into(binding.verifyPic)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder( VerifyItemBinding.inflate(LayoutInflater.from(parent.context), parent,false) )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount() = entries.size

    fun getEntries() = entries

    fun setEntries(entries: List<Entry>) {
        this.entries = entries
    }
}