package com.example.wds.fragments.choose

import android.app.ActionBar
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.wds.R
import com.example.wds.databinding.FragmentChooseBinding
import com.example.wds.utilities.toast
import com.google.firebase.firestore.FirebaseFirestore

class ChooseFragment : Fragment(R.layout.fragment_choose) {
    private lateinit var binding: FragmentChooseBinding
    private lateinit var btnArray: Array<ImageButton>
    private lateinit var modeArray: Array<ConstraintLayout>

    companion object {
        private val modeKeys = arrayOf("forest", "urban", "rural")
        private val animalKeys = arrayOf(
            "deer", "elk", "bear", "eagle", "wolf",
            "opossum", "raccoon", "rabbit", "squirrel", "cat",
            "snake", "cow", "sheep", "horse", "chicken")
        private val num = animalKeys.size
        //        private const val TAG = "DEBUG"
    }

    private var oldMode = modeKeys[0]
    private var newMode = modeKeys[0]
    private val oldSelected = Array(num) { false }
    private val newSelected = Array(num) { false }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseBinding.bind(view)
        with(binding) {
            modeArray = arrayOf(forest, urban, rural)
            for (mode in modeArray) {
                mode.setOnClickListener {
                    mode(it)
                }
            }
            btnArray = arrayOf(f1, f2, f3, f4, f5, u1, u2, u3, u4, u5, r1, r2, r3, r4, r5)
            load()
            for ((i, btn) in btnArray.withIndex()) {
                btn.setOnClickListener {
                    val new = !btn.isSelected
                    btn.isSelected = new
                    newSelected[i] = new
                }
                btn.setOnLongClickListener {
                    toast(requireContext(), btn.contentDescription.toString())
                    true
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
                if (it.contains("mode")) {
                    when (it.getString("mode")!!) {
                        modeKeys[1] -> {
                            oldMode = modeKeys[1]
                            mode(binding.urban)
                        }
                        modeKeys[2] -> {
                            oldMode = modeKeys[2]
                            mode(binding.rural)
                        }
                        else -> {
                            oldMode = modeKeys[0]
                            mode(binding.forest)
                        }
                    }
                    for ((i, btn) in btnArray.withIndex()) {
                        val sel = it.getBoolean(animalKeys[i])!!
                        btn.isSelected = sel
                        oldSelected[i] = sel
                        newSelected[i] = sel
                    }
                } else {
                    mode(binding.forest)
                    oldSelected[0] = true
                }
            }
            .addOnFailureListener {
                mode(binding.forest)
            }
    }

    private fun save() {
        val text =
            if (!newSelected.contentEquals(oldSelected) || oldMode != newMode) {
                val dataHash = HashMap<String, Any>()
                dataHash["mode"] = newMode
                for ((i, sel) in newSelected.withIndex()) {
                    dataHash[animalKeys[i]] = sel
                }
                FirebaseFirestore.getInstance().collection("Verify")
                    .document("configuration").set(dataHash.toMap())
                "Sending new configuration"
            } else {
                "No change to configuration"
            }
        toast(requireContext(), text)
    }

    private fun mode(view: View) {
        with(binding) {
            val btnsArr = arrayOf(forestBtns, urbanBtns, ruralBtns)
            for ((i, mode) in modeArray.withIndex()) {
                if (view == mode) {
                    newMode = modeKeys[i]
                    btnsArr[i].expand()
                    mode.layoutParams.height = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        0F,
                        resources.displayMetrics
                    ).toInt()
                } else {
                    btnsArr[i].collapse()
                    mode.layoutParams.height = ActionBar.LayoutParams.WRAP_CONTENT
                }
            }
        }
    }
}