package com.customemoji.keyboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var status: TextView

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@registerForActivityResult
        try {
            val file = ImageProcessor.createEmoji(this, uri)
            EmojiStore.add(this, file)
            status.text = "Added ${file.name} to My Emojis. Open the keyboard and tap ⭐ My Emojis."
        } catch (e: Exception) {
            status.text = "Could not create emoji: ${e.message ?: "unknown error"}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        status = findViewById(R.id.statusText)
        findViewById<Button>(R.id.enableKeyboardButton).setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }
        findViewById<Button>(R.id.addEmojiButton).setOnClickListener {
            pickImage.launch("image/*")
        }
        status.text = "Saved custom emojis: ${EmojiStore.list(this).size}"
    }

    companion object {
        fun openFromKeyboard(service: android.inputmethodservice.InputMethodService) {
            val intent = Intent(service, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            service.startActivity(intent)
        }
    }
}
