package com.example.wds.fragments.log

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wds.R
import com.example.wds.databinding.FragmentLogBinding
import com.example.wds.entry.EntryViewModel

class LogFragment : Fragment(R.layout.fragment_log) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        println("DEBUG LogFragment")
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLogBinding.bind(view)
        val myAdapter = LogListAdapter()
        binding.rvLog.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(this.context)
            setHasFixedSize(true)
        }
        val entryViewModel = ViewModelProvider(this).get(EntryViewModel::class.java)
        entryViewModel.allEntries.observe(viewLifecycleOwner, { myAdapter.setEntries(it) })
    }
}