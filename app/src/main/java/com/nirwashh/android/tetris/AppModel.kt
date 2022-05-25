package com.nirwashh.android.tetris

import android.graphics.Point
import com.nirwashh.android.tetris.constants.CellConstants.*
import com.nirwashh.android.tetris.constants.FieldConstants.*
import com.nirwashh.android.tetris.helper.array2dofByte
import com.nirwashh.android.tetris.models.Block
import com.nirwashh.android.tetris.AppModel.Motions.*
import com.nirwashh.android.tetris.AppModel.Statues.*
import com.nirwashh.android.tetris.storage.AppPreferences

class AppModel {
    var score = 0
    private var preferences: AppPreferences? = null
    var currentBlock: Block? = null
    var currentState = Statues.AWAITING_START.name

    private var field: Array<ByteArray> = array2dofByte(ROW_COUNT.value, COLUMN_COUNT.value)



    fun setPreferences(preferences: AppPreferences?) {
        this.preferences = preferences
    }

    fun getCellStatus(row: Int, column: Int): Byte? {
        return field[row][column]
    }

    private fun setCellStatus(row: Int, column: Int, status: Byte?) {
        if (status != null) {
            field[row][column] = status
        }
    }

    fun isGameOver(): Boolean {
        return currentState == Statues.OVER.name
    }

    fun isGameActive(): Boolean {
        return currentState == Statues.ACTIVE.name
    }

    fun isGameAwaitStart(): Boolean {
        return currentState == Statues.AWAITING_START.name
    }

    private fun boostScore() {
        score += 10
        if (score > preferences?.getHighScore() as Int) {
            preferences?.saveHighScore(score)
        }
    }

    private fun generateNextBlock() {
        currentBlock = Block.createBlock()
    }

    private fun validTranslation(position: Point, shape: Array<ByteArray>): Boolean {
        return if (position.y < 0 || position.x < 0) {
            false
        } else if (position.y + shape.size > ROW_COUNT.value) {
            false
        } else if (position.x + shape[0].size > COLUMN_COUNT.value) {
            false
        } else {
            for (i in shape.indices) {
                for (j in 0 until shape[i].size) {
                    val y = position.y + i
                    val x = position.x + j
                    if (EMPTY.value != shape[i][j] && EMPTY.value != field[y][x]) {
                        return false
                    }
                }
            }
            return true
        }
    }

    private fun moveValid(position: Point, frameNumber: Int?): Boolean {
        val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber as Int)
        return validTranslation(position, shape as Array<ByteArray>)
    }

    fun generateField(action: String) {
        if (isGameActive()) {
            resetField()
            var frameNumber: Int? = currentBlock?.frameNumber
            val coordinate: Point? = Point()
            coordinate?.x = currentBlock?.position?.x
            coordinate?.y = currentBlock?.position?.y

            when (action) {
                LEFT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.minus(1)
                }
                RIGHT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.plus(1)
                }
                DOWN.name -> {
                    coordinate?.y = currentBlock?.position?.y?.plus(1)
                }
                ROTATE.name -> {
                    frameNumber = frameNumber?.plus(1)
                    if (frameNumber != null) {
                        if (frameNumber >= currentBlock?.frameCount as Int) {
                            frameNumber = 0
                        }
                    }
                }
            }

            if (!moveValid(coordinate as Point, frameNumber)) {
                translateBlock(currentBlock?.position as Point,
                currentBlock?.frameNumber as Int)
                if (DOWN.name == action) {
                    boostScore()
                    persistCellData()
                    assessField()
                    generateNextBlock()
                    if (!blockAdditionPossible()) {
                        currentState = OVER.name
                        currentBlock = null
                        resetField(false)
                    }
                }
            } else {
                if (frameNumber != null) {
                    translateBlock(coordinate, frameNumber)
                    currentBlock?.setState(frameNumber, coordinate)
                }
            }
        }


    }

    private fun resetField(ephemeralCellsOnly: Boolean = true) {
        for (i in 0 until ROW_COUNT.value) {
            (0 until COLUMN_COUNT.value)
                .filter { !ephemeralCellsOnly || field[i][it] == EPHEMERAL.value }
                .forEach { field[i][it] = EMPTY.value }
        }
    }

    private fun persistCellData() {
        for (i in field.indices) {
            for (j in 0 until field[i].size) {
                var status = getCellStatus(i, j)
                if (status == EPHEMERAL.value) {
                    status = currentBlock?.staticValue
                    setCellStatus(i, j, status)
                }
            }
        }
    }

    private fun assessField() {
        for (i in 0 until field.size) {
            var emptyCells = 0
            for (j in 0 until field[i].size) {
                val status = getCellStatus(i, j)
                val isEmpty = EMPTY.value == status
                if (isEmpty) emptyCells++
                if (emptyCells == 0) shiftRows(i)
            }
        }
    }

    private fun translateBlock(position: Point, frameNumber: Int) {
        synchronized(field) {
            val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber)
            if (shape != null) {
                for (i in shape.indices) {
                    for (j in 0 until shape[i].size) {
                        val y = position.y + i
                        val x = position.x + j
                        if (EMPTY.value != shape[i][j]) {
                            field[y][x] = shape[i][j]
                        }
                    }
                }
            }
        }
    }

    private fun blockAdditionPossible(): Boolean {
        if (!moveValid(currentBlock?.position as Point, currentBlock?.frameNumber)) {
            return false
        }
        return true
    }

    private fun shiftRows(ntoRow: Int) {
        if (ntoRow > 0) {
            for (j in ntoRow -1 downTo 0) {
                for (m in 0 until field[j].size) {
                    setCellStatus(j + 1, m, getCellStatus(j, m))
                }
            }
        }
        for (j in 0 until field[0].size) {
            setCellStatus(0, j, EMPTY.value)
        }
    }

    fun startGame() {
        if (!isGameActive()) {
            currentState = ACTIVE.name
            generateNextBlock()
        }
    }

    fun resetGame() {
        resetModel()
        startGame()
    }

    fun endGame() {
        score = 0
        currentState = OVER.name
    }

    private fun resetModel() {
        resetField(false)
        currentState = AWAITING_START.name
        score = 0
    }



    enum class Statues {
        AWAITING_START, ACTIVE, INACTIVE, OVER
    }

    enum class Motions {
        LEFT, RIGHT, UP, DOWN, ROTATE
    }
}