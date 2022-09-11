package com.distantlandgames.tetrisandwords.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.distantlandgames.tetrisandwords.tetris.GroupCell
import kotlinx.coroutines.*
import java.util.*

class GameViewModel : ViewModel() {
    override fun onCleared() {
        Log.d("VIOLET", "Clear viewmodel")
        super.onCleared()
    }

    // TODO: Implement the ViewModel
    val score: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val stateOfTheBoard: MutableLiveData<Vector<Vector<GroupCell>>> by lazy {
        MutableLiveData<Vector<Vector<GroupCell>>>()
    }

    fun setScore(score: String){
        this.score.value = score
    }

    fun setTetrisStateBoard(board: Vector<Vector<GroupCell>>) {
        this.stateOfTheBoard.value = Vector()

        for(r in 0 until board.size) {
            this.stateOfTheBoard.value!!.addElement(Vector<GroupCell>())
            for (c in 0 until board[r].size) {
                var cell = board[r][c]
                this.stateOfTheBoard.value!![r].addElement(cell)
            }
        }
    }

    fun clickScore() {
        Log.d("VIOLET", "Clicked button! calling function!")
        //val newScore = "20"
        //score.value = newScore


    }
}
