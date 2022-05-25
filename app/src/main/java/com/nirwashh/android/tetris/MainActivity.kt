package com.nirwashh.android.tetris

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.nirwashh.android.tetris.databinding.ActivityMainBinding
import com.nirwashh.android.tetris.storage.AppPreferences
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnNewGame.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
        b.btnExit.setOnClickListener {
            exitProcess(0)
        }
        b.btnResetScore.setOnClickListener {
            val preferences = AppPreferences(this@MainActivity)
            preferences.clearHighScore()
            Toast.makeText(this@MainActivity, "Score successfully reset", Toast.LENGTH_SHORT).show()
            b.tvHighScore.text = "High score: ${preferences.getHighScore()}"
        }
    }
}