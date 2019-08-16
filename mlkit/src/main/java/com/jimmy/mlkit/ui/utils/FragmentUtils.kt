package com.jimmy.mlkit.ui.utils

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction


inline fun FragmentManager.open(block: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        block()
        commit()
    }
}