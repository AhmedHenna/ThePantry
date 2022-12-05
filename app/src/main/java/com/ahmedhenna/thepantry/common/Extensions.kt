package com.ahmedhenna.thepantry.common

import android.content.res.Resources.getSystem


val Int.px: Int get() = (this * getSystem().displayMetrics.density).toInt()


