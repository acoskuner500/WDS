package com.example.wds.utilities

import android.content.Context
import android.content.SharedPreferences
import android.view.Gravity
import android.widget.Toast

fun prefs(context: Context): SharedPreferences {
    return context.getSharedPreferences("prefsKey", Context.MODE_PRIVATE)
}

fun toast(context: Context, msg: String) {
    Toast.makeText(context,msg,Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.BOTTOM,0,400)
        show()
    }
}