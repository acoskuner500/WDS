package com.example.wds.fragments.choose

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wds.R
import com.example.wds.databinding.FragmentChooseBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChooseFragment : Fragment(R.layout.fragment_choose) {
    private lateinit var binding: FragmentChooseBinding
    private lateinit var btnArray: Array<ImageButton>
    private val prefs by lazy {
        requireContext().getSharedPreferences(
            prefsKey,
            Context.MODE_PRIVATE
        )
    }

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
        private const val prefsKey = "prefsKey"
        private const val btnSelKey = "btnSelKey"
        private const val btnEnKey = "btnEnKey"
        private const val numSelKey = "numSelKey"
        private const val TAG = "DEBUG"
    }
    private var numSelected = 0
    private val oldSelected = Array(num) { false }
    private val newSelected = Array(num) { false }
    private val oldEnabled = Array(num) { false }
    private val newEnabled = Array(num) { false }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseBinding.bind(view)
        with(binding) {
            btnArray = arrayOf(
                btnBear, btnBoar, btnDeer, btnDog,
                btnRabbit, btnRaccoon, btnSkunk, btnSquirrel
            )
            loadData()
            for (btn in btnArray) {
                btn.setOnClickListener {
                    btnSelToggled(btn)
                    btnEnToggled()
                }
            }
        }
    }

    override fun onPause() {
        saveData()
        super.onPause()
    }

    private fun btnSelToggled(btn: ImageButton) {
        btn.isSelected = !(btn.isSelected)
        if (btn.isSelected) {
            numSelected++
        } else {
            numSelected--
        }
    }

    private fun btnEnToggled() {
        for (btn in btnArray) {
            if (numSelected >= num) {
                btn.isEnabled = btn.isSelected
            } else {
                btn.isEnabled = true
            }
        }
    }

    private fun saveData() {
        for ((i,btn) in btnArray.withIndex()) {
            newSelected[i] = btn.isSelected
            newEnabled[i] = btn.isEnabled
        }
        val change = !newSelected.contentEquals(oldSelected) || !newEnabled.contentEquals(oldEnabled)
        val text =
            if (change) {
                prefs.edit().apply {
                    putInt(numSelKey, numSelected)
                    for (btn in btnArray) {
                        putBoolean(btnSelKey + btn.id.toString(), btn.isSelected)
                        putBoolean(btnEnKey + btn.id.toString(), btn.isEnabled)
                    }
                }.apply()
                val dataHash = HashMap<String, Boolean>()
                for ((i, sel) in newSelected.withIndex()) {
                    dataHash[animalKeys[i]] = sel
                }
                updateConfig(dataHash)
                "Sending new configuration"
            } else {
                "No change to configuration"
            }
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.BOTTOM,0,200)
            show()
        }
    }

    private fun loadData() {
        numSelected = prefs.getInt(numSelKey, 0)
        var sel = false
        var en = true
        for ((i,btn) in btnArray.withIndex()) {
            if (numSelected != 0) {
                sel = prefs.getBoolean(btnSelKey + btn.id.toString(), false)
                en = prefs.getBoolean(btnEnKey + btn.id.toString(), true)
            }
            btn.isSelected = sel
            btn.isEnabled = en
            oldSelected[i] = sel
            oldEnabled[i] = en
            Log.d(TAG, "loadData: btn = ${btn.id}: sel = $sel, en = $en")
        }
    }

    private fun updateConfig(data: HashMap<String, Boolean>) {
        val configRef = Firebase.storage("gs://deterred/").reference
            .child("configuration.json")
        configRef.putBytes(data.toString().toByteArray())
            .addOnFailureListener { e ->
                Log.d(TAG, "updateConfig: $e")
            }
            .addOnSuccessListener {
                Log.d(TAG, "updateConfig: configuration.json updated successfully")
            }
    }
}