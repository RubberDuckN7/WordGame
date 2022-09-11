package com.distantlandgames.tetrisandwords

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.distantlandgames.tetrisandwords.howto.HowToFragment
import com.distantlandgames.tetrisandwords.tetris.GameState
import com.distantlandgames.tetrisandwords.ui.main.GameFragment
import com.distantlandgames.tetrisandwords.ui.main.MainFragment
import com.distantlandgames.tetrisandwords.ui.main.PauseScreenFragment
import com.distantlandgames.tetrisandwords.ui.main.WordHistoryFragment
import java.util.*

interface FragmentCallBack {
    fun navigateToGame()
    fun navigateToHistory(listOfWords: Vector<String>)
    fun navigateToPause()
    fun navigateToHowTo()
}

class MainActivity : AppCompatActivity(), FragmentCallBack, WordHistoryFragment.OnFragmentInteractionListener {
    private var gameState = GameState.NotStarted

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    override fun navigateToGame() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, GameFragment.newInstance())
            .commitNow()
    }

    override fun navigateToHistory(listOfWords: Vector<String>) {
        Log.d("VIOLET", "Showing history")
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, WordHistoryFragment.newInstance(listOfWords))
            .addToBackStack(null)
            .commit()

        // supportFragmentManager.beginTransaction()
        //     .replace(R.id.container, WordHistoryFragment.newInstance())
        //     .commitNow()
        //val fragmentManager = supportFragmentManager
        //val fragmentTransaction = fragmentManager.beginTransaction()
        //val fragment = WordHistoryFragment()
        //fragmentTransaction.add(R.id.container, fragment)
        //fragmentTransaction.commit()
    }

    override fun navigateToPause() {
        Log.d("VIOLET", "Showing history")

        gameState = GameState.Paused
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, PauseScreenFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    override fun navigateToHowTo() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, HowToFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }
}
