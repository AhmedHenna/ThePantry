package com.ahmedhenna.thepantry.common

import android.app.Activity
import android.content.Context
import android.content.res.Resources.getSystem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import java.util.Locale


fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it ->
    it.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }
}

fun Context.getDrawableIdentifierFromString(drawable: String): Int {
    return resources.getIdentifier(
        drawable,
        "drawable",
        packageName
    )
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


val Int.px: Int get() = (this * getSystem().displayMetrics.density).toInt()


