package com.bitsfabrik.bitsstrings.generator.domain

/** Plugin ids that give a project an android target. */
internal object AndroidPluginIds {
    const val APPLICATION = "com.android.application"
    const val LIBRARY = "com.android.library"
    const val KMP_LIBRARY = "com.android.kotlin.multiplatform.library"

    val all: List<String> = listOf(APPLICATION, LIBRARY, KMP_LIBRARY)
}