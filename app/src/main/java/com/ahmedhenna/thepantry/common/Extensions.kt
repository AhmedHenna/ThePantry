package com.ahmedhenna.thepantry.common

import android.app.Activity
import android.content.Context
import android.content.res.Resources.getSystem
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.util.*


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

val imageURLRegex = "^https?:\\/\\/.+\\.(jpg|jpeg|png|webp|avif|gif|svg)".toRegex()

fun String.toBitmap(context: Context, onLoad: (Bitmap) -> Unit) {
    if (startsWith("http")) {
        Picasso.get().load(this).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                bitmap?.let(onLoad)
            }
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
        })
    } else {
        val bitmap = BitmapFactory.decodeResource(
            context.resources,
            context.getDrawableIdentifierFromString(this)
        )
        onLoad(bitmap)
    }
}


