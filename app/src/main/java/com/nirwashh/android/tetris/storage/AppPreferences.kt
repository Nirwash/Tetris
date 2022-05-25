package com.nirwashh.android.tetris.storage

import android.content.Context
import android.content.SharedPreferences

const val APP_PREFERENCES = "App preferences"
const val HIGH_SCORE = "high score"
class AppPreferences(context: Context) {
    var data: SharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)


    fun saveHighScore(highScore: Int) {
       data.edit().putInt(HIGH_SCORE, highScore).apply()
    }

    fun getHighScore(): Int {
        return data.getInt(HIGH_SCORE, 0)
    }

    fun clearHighScore() {
        data.edit().putInt(HIGH_SCORE, 0).apply()
    }
}