package com.customemoji.keyboard

import android.content.ClipDescription
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputContentInfo
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.FileProvider

class CustomEmojiInputMethodService : android.inputmethodservice.InputMethodService() {
    private lateinit var root: View
    private lateinit var customRow: LinearLayout
    private lateinit var lettersGrid: GridLayout

    override fun onCreateInputView(): View {
        root = layoutInflater.inflate(R.layout.keyboard_view, null)
        customRow = root.findViewById(R.id.customRow)
        lettersGrid = root.findViewById(R.id.lettersGrid)

        buildLetters()
        root.findViewById<Button>(R.id.deleteButton).setOnClickListener { deleteCharacter() }
        root.findViewById<Button>(R.id.spaceButton).setOnClickListener { commitText(" ") }
        root.findViewById<Button>(R.id.enterButton).setOnClickListener { sendEnter() }
        root.findViewById<Button>(R.id.addButton).setOnClickListener { MainActivity.openFromKeyboard(this) }
        root.findViewById<Button>(R.id.tabText).setOnClickListener { showLetters() }
        root.findViewById<Button>(R.id.tabEmoji).setOnClickListener { showCustomEmojis() }
        showCustomEmojis()
        return root
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        if (::customRow.isInitialized) showCustomEmojis()
    }

    private fun buildLetters() {
        lettersGrid.removeAllViews()
        val keys = "QWERTYUIOPASDFGHJKLZXCVBNM"
        keys.forEach { letter ->
            val button = Button(this).apply {
                text = letter.toString()
                textSize = 16f
                setPadding(0, 0, 0, 0)
                minHeight = dp(42)
                minWidth = 0
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = dp(46)
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(dp(2), dp(2), dp(2), dp(2))
                }
                setOnClickListener { commitText(letter.toString().lowercase()) }
            }
            lettersGrid.addView(button)
        }
    }

    private fun showLetters() {
        lettersGrid.visibility = View.VISIBLE
    }

    private fun showCustomEmojis() {
        lettersGrid.visibility = View.GONE
        customRow.removeAllViews()

        ensureSampleBadge()

        EmojiStore.list(this).forEach { file ->
            val button = ImageButton(this).apply {
                layoutParams = LinearLayout.LayoutParams(dp(56), dp(56)).apply { setMargins(dp(4), dp(2), dp(4), dp(2)) }
                scaleType = ImageView.ScaleType.CENTER_CROP
                setPadding(dp(4), dp(4), dp(4), dp(4))
                background = ColorDrawable(Color.TRANSPARENT)
                setImageURI(Uri.fromFile(file))
                contentDescription = "Custom emoji"
                setOnClickListener { insertImage(FileProvider.getUriForFile(this@CustomEmojiInputMethodService, "com.customemoji.keyboard.fileprovider", file), "image/png") }
            }
            customRow.addView(button)
        }
    }


    private fun ensureSampleBadge() {
        val existing = EmojiStore.list(this).firstOrNull { it.name == "built_in_verified_badge.png" }
        if (existing != null) return
        try {
            val output = java.io.File(EmojiStore.directory(this), "built_in_verified_badge.png")
            resources.openRawResource(R.drawable.sample_verified_badge).use { input ->
                output.outputStream().use { input.copyTo(it) }
            }
            EmojiStore.add(this, output)
        } catch (_: Exception) {
        }
    }

    private fun addImageButton(resourceId: Int, description: String, action: () -> Unit) {
        val button = ImageButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(56), dp(56)).apply { setMargins(dp(4), dp(2), dp(4), dp(2)) }
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setPadding(dp(5), dp(5), dp(5), dp(5))
            background = ColorDrawable(Color.TRANSPARENT)
            setImageResource(resourceId)
            contentDescription = description
            setOnClickListener { action() }
        }
        customRow.addView(button)
    }

    private fun insertImage(uri: Uri, mimeType: String) {
        val connection = currentInputConnection ?: return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            Toast.makeText(this, "Image insertion needs Android 7.1 or newer", Toast.LENGTH_SHORT).show()
            return
        }

        val supported = currentInputEditorInfo?.contentMimeTypes?.any { mime ->
            mime == mimeType || mime == "image/*" || (mime.endsWith("/*") && mimeType.startsWith(mime.substringBefore('/'))) 
        } == true

        if (!supported) {
            Toast.makeText(this, "This app does not advertise image insertion support. Try the app's attachment/emoji field.", Toast.LENGTH_LONG).show()
            return
        }

        try {
            val description = ClipDescription("Custom emoji", arrayOf(mimeType))
            val contentInfo = InputContentInfo(uri, description, null)
            val accepted = connection.commitContent(
                contentInfo,
                InputConnection.INPUT_CONTENT_GRANT_READ_URI_PERMISSION,
                null
            )
            if (!accepted) Toast.makeText(this, "The current app rejected the custom emoji", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Could not insert image: ${e.message ?: "unknown error"}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun commitText(text: String) {
        currentInputConnection?.commitText(text, 1)
    }

    private fun deleteCharacter() {
        currentInputConnection?.deleteSurroundingTextInCodePoints(1, 0)
    }

    private fun sendEnter() {
        val connection = currentInputConnection ?: return
        if (!sendDefaultEditorAction(true)) connection.commitText("\n", 1)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
