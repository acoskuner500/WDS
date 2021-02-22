package com.example.wds.fragments.choose

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.content.SharedPreferences
import android.os.Bundle
import com.example.wds.R

class ChooseFragment : Fragment(R.layout.fragment_choose) {

    //    private var numSelected: Int = 0
    private lateinit var btnArray: Array<ImageButton>

    companion object {
        private const val prefsKey = "prefsKey"
        //        private const val numSelKey = "numSelKey"
        //        private const val btnEnKey = "btnEnKey"
        private const val btnSelKey = "btnSelKey"
        private val btnIdArr = arrayOf(
            R.id.btnBear, R.id.btnBoar, R.id.btnDeer, R.id.btnDog,
            R.id.btnRabbit, R.id.btnRaccoon, R.id.btnSkunk, R.id.btnSquirrel
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnArray = createBtnArray(view)
        loadData(btnArray)
        for (btn in btnArray) {
            btn.setOnClickListener {
                btnSelToggled(btn)
//                btnEnToggled(btnArray)
            }
        }
    }

    override fun onPause() {
        saveData(btnArray)
        super.onPause()
    }

    private fun createBtnArray(view: View): Array<ImageButton> {
        val btnList = ArrayList<ImageButton>()
        for (id in btnIdArr) {
            btnList.add(view.findViewById(id))
            val btn = btnList[btnList.size-1]
            btn.isSelected = false
            btn.isEnabled = true
        }
        return btnList.toTypedArray()
    }

    private fun btnSelToggled(btn: ImageButton) {
        btn.isSelected = !(btn.isSelected)
//        if (btn.isSelected) {
//            numSelected++
//        } else {
//            numSelected--
//        }
    }

//    private fun btnEnToggled(btnArray: Array<ImageButton>) {
//        for (btn in btnArray) {
//            if (numSelected >= 3) {
//                btn.isEnabled = btn.isSelected
//            } else {
//                btn.isEnabled = true
//            }
//        }
//    }

    private fun saveData(btnArray: Array<ImageButton>) {
        prefs().edit().apply {
//            putInt(numSelKey, numSelected)
            for (btn in btnArray) {
                putBoolean(btnSelKey + btn.id.toString(), btn.isSelected)
//                putBoolean(btnEnKey + btn.id.toString(), btn.isEnabled)
            }
        }.apply()
    }

    private fun loadData(btnArray: Array<ImageButton>) {
//        numSelected = prefs().getInt(numSelKey, 0)
//        if (numSelected == 0) {
//            for (btn in btnArray) {
//                btn.isEnabled = true
//                btn.isSelected = false
//            }
//        } else {
            for (btn in btnArray) {
                btn.isSelected = prefs().getBoolean(btnSelKey + btn.id.toString(), false)
//                btn.isEnabled = prefs().getBoolean(btnEnKey + btn.id.toString(), true)
            }
//        }
    }

    private fun prefs(): SharedPreferences {
        return requireContext().getSharedPreferences(prefsKey, Context.MODE_PRIVATE)
    }
}
