package com.bitsfabrik.bitsstrings.core.res

import android.content.Context

actual data class StringResource(
    /*@StringRes*/ val resourceId: Int
) {

    fun getString(context: Context): String = context.getString(resourceId)
}
