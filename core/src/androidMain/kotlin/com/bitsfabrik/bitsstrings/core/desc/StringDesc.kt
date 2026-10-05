package com.bitsfabrik.bitsstrings.core.desc

import android.content.Context
import android.os.LocaleList
import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.res.PluralsResource
import com.bitsfabrik.bitsstrings.core.res.StringResource
import com.bitsfabrik.bitsstrings.core.utils.toBitsLocale
import com.bitsfabrik.bitsstrings.core.utils.toPlatformLocale
import java.util.Locale

actual interface StringDesc {
    fun toString(context: Context, localeType: LocaleType = LocaleType.System): String

    actual sealed class LocaleType {
        abstract val localeOverride: Locale?

        actual object System : LocaleType() {
            // no override needed. just use system locale
            override val localeOverride: Locale? = null
        }

        actual class Custom actual constructor(
            private val locale: BitsLocale
        ) : LocaleType() {

            constructor(locale : Locale):this(locale.toBitsLocale())

            // use provided locale for override
            override val localeOverride: Locale by lazy {
                locale.toPlatformLocale()
            }
        }
    }

    actual companion object{

    }
}
