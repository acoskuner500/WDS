package com.example.wds.fragments.verify

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wds.R
import com.example.wds.databinding.VerifyItemBinding
import com.example.wds.entry.Entry
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.storage.ktx.storage

class CardStackAdapter(private var entries : List<Entry>) : RecyclerView.Adapter<CardStackAdapter.CardViewHolder>() {
    class CardViewHolder(private val binding: VerifyItemBinding) : RecyclerView.ViewHolder(binding.root) {
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
//                    .load("https://storage.googleapis.com/genuine-cirrus-294302.appspot.com/img1.jpg")
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