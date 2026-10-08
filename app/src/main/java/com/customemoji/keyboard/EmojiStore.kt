package com.customemoji.keyboard

import android.content.Context
import java.io.File
import java.util.UUID

object EmojiStore {
    private const val PREFS = "emoji_store"
    private const val KEY_FILES = "files"

    fun directory(context: Context): File = File(context.filesDir, "emojis").apply { mkdirs() }

    fun list(context: Context): List<File> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val names = prefs.getStringSet(KEY_FILES, emptySet()).orEmpty()
        return names.map { File(directory(context), it) }.filter { it.exists() }.sortedBy { it.name }
    }

    fun add(context: Context, file: File) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = prefs.getStringSet(KEY_FILES, emptySet()).orEmpty().toMutableSet()
        current.add(file.name)
        prefs.edit().putStringSet(KEY_FILES, current).apply()
    }

    fun newFile(context: Context): File = File(directory(context), "emoji_${UUID.randomUUID()}.png")

    fun remove(context: Context, file: File) {
        file.delete()
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = prefs.getStringSet(KEY_FILES, emptySet()).orEmpty().toMutableSet()
        current.remove(file.name)
        prefs.edit().putStringSet(KEY_FILES, current).apply()
    }
}
