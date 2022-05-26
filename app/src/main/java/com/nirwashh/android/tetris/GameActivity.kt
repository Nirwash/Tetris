package com.nirwashh.android.tetris

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.nirwashh.android.tetris.databinding.ActivityGameBinding
import com.nirwashh.android.tetris.storage.AppPreferences

class GameActivity : AppCompatActivity() {
    lateinit var preferences: AppPreferences
    private val appModel: AppModel = AppModel()
    lateinit var b: ActivityGameBinding
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityGameBinding.inflate(layoutInflater)
        setContentView(b.root)
        preferences = AppPreferences(this)
        appModel.setPreferences(preferences)
        b.viewTetris.apply {
            setActivity(this@GameActivity)
            setModel(appModel)

        }
        b.viewTetris.setOnTouchListener(this::onTetrisViewTouch)
        b.btnRestart.setOnClickListener(this::btnRestartClick)

            updateHighScore()
            updateCurrentScore()
        }

    private fun btnRestartClick(view: View) {
        appModel.resetGame()
    }

    private fun onTetrisViewTouch(view: View, event: MotionEvent): Boolean {
        if (appModel.isGameOver() || appModel.isGameAwaitStart()) {
            appModel.startGame()
            b.viewTetris.setGameCommandWithDelay(AppModel.Motions.DOWN)
        } else if (appModel.isGameActive()) {
            when (resolveTouchDirection(view, event)) {
                0 -> moveTetromino(AppModel.Motions.LEFT)
                1 -> moveTetromino(AppModel.Motions.ROTATE)
                2 -> moveTetromino(AppModel.Motions.DOWN)
                3 -> moveTetromino(AppModel.Motions.RIGHT)
            }
        }
        return true
    }

    private fun resolveTouchDirection(view: View, event: MotionEvent): Int {
        val x = event.x / view.width
        val y = event.y / view.height
        val direction: Int = if (y > x) {
            if (x > 1 - y) 2 else 0
        } else {
            if (x > 1 - y) 3 else 1
        }
        return direction
    }

    private fun moveTetromino(motion: AppModel.Motions) {
        if (appModel.isGameActive()) {
            b.viewTetris.setGameCommand(motion)
        }
    }

    private fun updateHighScore() {
        b.tvHighScore.text = "${preferences.getHighScore()}"
    }

    private fun updateCurrentScore() {
        b.tvCurrentScore.text = "0"
    }
}