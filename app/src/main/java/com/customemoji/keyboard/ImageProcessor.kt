package com.customemoji.keyboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

object ImageProcessor {
    private const val SIZE = 256

    fun createEmoji(context: Context, source: Uri): File {
        val resolver = context.contentResolver
        val input = resolver.openInputStream(source) ?: error("Unable to open selected image")
        val original = input.use { BitmapFactory.decodeStream(it) }
            ?: error("Selected file is not a readable image")

        val side = min(original.width, original.height)
        val left = (original.width - side) / 2
        val top = (original.height - side) / 2
        val cropped = Bitmap.createBitmap(original, left, top, side, side)
        if (cropped !== original) original.recycle()

        val scaled = Bitmap.createScaledBitmap(cropped, SIZE, SIZE, true)
        if (scaled !== cropped) cropped.recycle()

        val output = EmojiStore.newFile(context)
        FileOutputStream(output).use { stream ->
            scaled.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        scaled.recycle()
        return output
    }
}
