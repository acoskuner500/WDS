package com.example.wds.fragments.choose

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.content.SharedPreferences
import android.os.Bundle
import com.example.wds.R
import com.example.wds.databinding.FragmentChooseBinding

class ChooseFragment : Fragment(R.layout.fragment_choose) {
//    private var numSelected = 0
    private lateinit var binding: FragmentChooseBinding
    private lateinit var btnList: List<ImageButton>

    companion object {
        private const val prefsKey = "prefsKey"
        private const val btnSelKey = "btnSelKey"
//        private const val btnEnKey = "btnEnKey"
//        private const val numSelKey = "numSelKey"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseBinding.bind(view)
        with(binding) {
            btnList = listOf(
                btnBear, btnBoar, btnDeer, btnDog,
                btnRabbit, btnRaccoon, btnSkunk, btnSquirrel
            )
            for (btn in btnList) {
                btn.isSelected = false
//                btn.isEnabled = true
            }
            loadData()
            for (btn in btnList) {
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
//        for (btn in btnList) {
//            if (numSelected >= 3) {
//                btn.isEnabled = btn.isSelected
//            } else {
//                btn.isEnabled = true
//            }
//        }
//    }

    private fun saveData() {
        prefs().edit().apply {
//            putInt(numSelKey, numSelected)
            for (btn in btnList) {
                putBoolean(btnSelKey + btn.id.toString(), btn.isSelected)
//                putBoolean(btnEnKey + btn.id.toString(), btn.isEnabled)
            }
        }.apply()
    }

    private fun loadData() {
//        numSelected = prefs().getInt(numSelKey, 0)
//        if (numSelected == 0) {
//            for (btn in btnList) {
//                btn.isEnabled = true
//                btn.isSelected = false
//            }
//        } else {
            for (btn in btnList) {
                btn.isSelected = prefs().getBoolean(btnSelKey + btn.id.toString(), false)
//                btn.isEnabled = prefs().getBoolean(btnEnKey + btn.id.toString(), true)
            }
//        }
    }

    private fun prefs(): SharedPreferences {
        return requireContext().getSharedPreferences(prefsKey, Context.MODE_PRIVATE)
    }
}