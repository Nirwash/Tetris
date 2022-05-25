package com.nirwashh.android.tetris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nirwashh.android.tetris.databinding.ActivityGameBinding
import com.nirwashh.android.tetris.storage.AppPreferences

class GameActivity : AppCompatActivity() {
    private lateinit var preferences: AppPreferences
    private lateinit var b: ActivityGameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityGameBinding.inflate(layoutInflater)
        setContentView(b.root)
        preferences = AppPreferences(this)
        updateHighScore()
        updateCurrentScore()
    }

    private fun updateHighScore() {
        b.tvHighScore.text = "${preferences.getHighScore()}"
    }

    private fun updateCurrentScore() {
        b.tvCurrentScore.text = "0"
    }
}