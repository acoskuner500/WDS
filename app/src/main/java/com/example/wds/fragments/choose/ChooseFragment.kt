package com.example.wds.fragments.choose

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wds.R
import com.example.wds.databinding.FragmentChooseBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class ChooseFragment : Fragment(R.layout.fragment_choose) {
    private lateinit var binding: FragmentChooseBinding
    private lateinit var btnArray: Array<ImageButton>
    private lateinit var functions: FirebaseFunctions
    private val prefs by lazy {
        requireContext().getSharedPreferences(
            prefsKey,
            Context.MODE_PRIVATE
        )
    }

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
    //    private var numSelected = 0
    private val num = animalKeys.size
    private val oldBool = Array(num) { false }
    private val newBool = Array(num) { false }
    private val prefsKey = "prefsKey"
    private val btnSelKey = "btnSelKey"
//    private val btnEnKey = "btnEnKey"
//    private val numSelKey = "numSelKey"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseBinding.bind(view)
        functions = Firebase.functions
        with(binding) {
            btnArray = arrayOf(
                btnBear, btnBoar, btnDeer, btnDog,
                btnRabbit, btnRaccoon, btnSkunk, btnSquirrel
            )
            loadData()
            for (btn in btnArray) {
                btn.setOnClickListener {
                    btnSelToggled(btn)
//                    btnEnToggled()
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
//        if (btn.isSelected) {
//            numSelected++
//        } else {
//            numSelected--
//        }
    }

//    private fun btnEnToggled() {
//        for (btn in btnArray) {
//            if (numSelected >= 3) {
//                btn.isEnabled = btn.isSelected
//            } else {
//                btn.isEnabled = true
//            }
//        }
//    }

    private fun saveData() {
        prefs.edit().apply {
//            putInt(numSelKey, numSelected)
            for ((index, btn) in btnArray.withIndex()) {
                val btnSelected = btn.isSelected
                putBoolean(btnSelKey + btn.id.toString(), btnSelected)
                newBool[index] = btnSelected
//                putBoolean(btnEnKey + btn.id.toString(), btn.isEnabled)
            }
        }.apply()
        val text: String
        if (!oldBool.contentEquals(newBool)) {
            text = "Sending new configuration"
            val dataHash = HashMap<String, Boolean>()
            for (j in 0 until num) {
                dataHash[animalKeys[j]] = newBool[j]
            }
            updateConfig(dataHash)
        } else { text = "No change to configuration"}
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.BOTTOM,0,200)
            show()
        }
    }

    private fun loadData() {
//        numSelected = prefs().getInt(numSelKey, 0)
//        if (numSelected == 0) {
//            for (btn in btnArray) {
//                btn.isEnabled = true
//                btn.isSelected = false
//            }
//        } else {
        for ((index, btn) in btnArray.withIndex()) {
            val btnSelected = prefs.getBoolean(btnSelKey + btn.id.toString(), false)
            btn.isSelected = btnSelected
            oldBool[index] = btnSelected
//                btn.isEnabled = prefs().getBoolean(btnEnKey + btn.id.toString(), true)
        }
//        }
    }

    private fun updateConfig(data: HashMap<String, Boolean>): Task<String> {
        return functions
            .getHttpsCallable("updateConfig")
            .call(Gson().toJson(data))
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as String
                result
            }
    }
}