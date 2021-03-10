package com.example.wds.fragments.log

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wds.utilities.Entry
import com.example.wds.R
import com.example.wds.databinding.FragmentLogBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LogFragment : Fragment(R.layout.fragment_log) {
    private lateinit var collection: CollectionReference
    private lateinit var myAdapter: LogListAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        println("DEBUG LogFragment")
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLogBinding.bind(view)
        collection = FirebaseFirestore.getInstance().collection("Log")
        val query = collection.orderBy("textTime", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions
            .Builder<Entry>()
            .setQuery(query, Entry::class.java)
            .build()
        myAdapter = LogListAdapter(options)
        binding.rvLog.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(this.context)
            setHasFixedSize(true)
        }
    }

    override fun onResume() {
        super.onResume()
        myAdapter.startListening()
    }

    override fun onPause() {
        myAdapter.stopListening()
        super.onPause()
    }
}