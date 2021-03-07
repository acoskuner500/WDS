package com.example.wds.fragments.verify

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wds.Entry
import com.example.wds.GlideApp
import com.example.wds.R
import com.example.wds.databinding.VerifyItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CardStackAdapter(options: FirestoreRecyclerOptions<Entry>) :
    FirestoreRecyclerAdapter<Entry, CardStackAdapter.CardViewHolder>(options) {
    class CardViewHolder(private val binding: VerifyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: Entry) {
            binding.tvAnimal.text = entry.getTextAnimal()
            binding.tvTimestamp.text = entry.getTextTime()

            GlideApp.with(itemView.context)
                .load(entry.getImgSrc())
                .placeholder(R.drawable.ic_image_400)
                .error(R.drawable.ic_image_400)
                .fitCenter()
                .into(binding.verifyPic)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder( VerifyItemBinding.inflate(LayoutInflater.from(parent.context), parent,false) )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int, model: Entry) {
        holder.bind(model)
    }

    override fun getItemCount() = snapshots.size

    fun getEntry(position: Int) = snapshots.getSnapshot(position).reference
}