package com.bitsfabrik.bitsstrings.core.utils

import android.content.Context
import android.content.res.Resources
import android.os.Build
import com.bitsfabrik.bitsstrings.core.desc.StringDesc
import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import java.util.Locale

object Utils {
    fun processArgs(args: List<Any?>, context: Context, localeType: LocaleType): Array<out Any?> {
        return args.map { (it as? StringDesc)?.toString(context, localeType) ?: it }.toTypedArray()
    }

    fun resourcesForContext(context: Context, localeType: LocaleType): Resources {
        val locale = localeType.localeOverride
        if(locale == null) {
            // no override locale -> use system locale
            return context.resources
        }

        return localizedContext(context, locale).resources
    }

    fun localeForContext(context: Context, localeType: LocaleType): Locale {
        return localeType.localeOverride ?: context.getSystemLocale()
    }

    private fun Context.getSystemLocale() : Locale{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            resources.configuration.locale
        }
    }

    private fun localizedContext(context: Context, locale : Locale): Context {
        val resources = context.resources
        val config = resources.configuration

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
            context
        }
    }
}