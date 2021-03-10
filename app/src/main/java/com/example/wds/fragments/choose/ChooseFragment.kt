package com.example.wds.fragments.choose

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.wds.R
import com.example.wds.databinding.FragmentChooseBinding
import com.example.wds.utilities.toast
import com.google.firebase.firestore.FirebaseFirestore

class ChooseFragment : Fragment(R.layout.fragment_choose) {
    private lateinit var binding: FragmentChooseBinding
    private lateinit var btnArray: Array<ImageButton>

    companion object {
        private val animalKeys = arrayOf(
            "bear",
            "boar",
            "deer",
            "dog",
            "rabbit",
            "raccoon",
            "skunk",
            "squirrel"
        )
        private val num = animalKeys.size
//        private const val TAG = "DEBUG"
    }
    private val oldSelected = Array(num) { false }
    private val newSelected = Array(num) { false }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseBinding.bind(view)
        with(binding) {
            btnArray = arrayOf(
                btnBear, btnBoar, btnDeer, btnDog,
                btnRabbit, btnRaccoon, btnSkunk, btnSquirrel
            )
            load()
            for ((i,btn) in btnArray.withIndex()) {
                btn.setOnClickListener {
                    val new = !btn.isSelected
                    btn.isSelected = new
                    newSelected[i] = new
                }
            }
        }
    }

    override fun onPause() {
        save()
        super.onPause()
    }

    private fun load() {
        FirebaseFirestore.getInstance().collection("Verify")
            .document("configuration").get()
            .addOnSuccessListener {
                for ((i, btn) in btnArray.withIndex()) {
                    val sel = it.getBoolean(animalKeys[i])!!
                    btn.isSelected = sel
                    oldSelected[i] = sel
                    newSelected[i] = sel
                }
            }
            .addOnFailureListener {
                for (btn in btnArray) {
                    btn.isSelected = false
                }
            }
    }

    private fun save() {
        val text =
            if (!newSelected.contentEquals(oldSelected)) {
                val dataHash = HashMap<String, Boolean>()
                for ((i, sel) in newSelected.withIndex()) {
                    dataHash[animalKeys[i]] = sel
                }
                FirebaseFirestore.getInstance().collection("Verify")
                    .document("configuration").update(dataHash as Map<String, Boolean>)
                "Sending new configuration"
            } else {
                "No change to configuration"
            }
        toast(requireContext(),text)
    }
}