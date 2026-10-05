package com.bitsfabrik.bitsstrings.core.res

import android.content.Context

actual data class PluralsResource(
   /* @PluralsRes*/ val resourceId: Int
) {

    fun getQuantityString(context: Context, number: Int): String {
        return context.resources.getQuantityString(resourceId, number)
    }
}
