package com.distantlandgames.tetrisandwords.ui.main

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.distantlandgames.tetrisandwords.FragmentCallBack

import com.distantlandgames.tetrisandwords.R
import com.distantlandgames.tetrisandwords.databinding.GameFragmentBinding
import com.distantlandgames.tetrisandwords.tetris.TetrisGameView

import com.distantlandgames.tetrisandwords.viewmodel.GameViewModel
import com.distantlandgames.violet.InputHandler
import com.distantlandgames.violet.InputHandlerDepr
import com.distantlandgames.violet.Vector2

class GameFragment : Fragment() {
    companion object {
        fun newInstance() = GameFragment()
    }

    private lateinit var viewModel: GameViewModel
    private lateinit var binding: GameFragmentBinding
    private lateinit var renderer: TetrisGameView
    private var input = InputHandlerDepr()
    private var debugInput = InputHandler()
    //private var game: Game = Game()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)

        if(hasOpenGLEs20Support()) {
            initializeOpenGl2()
        }
        else {
            executeOpenGlNotSupported()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)

        Log.d("VIOLET", "Starting up!")

        val scoreObserver = Observer<String> { score ->
            binding.textScore.text = score
            Log.d("VIOLET", "Setting data score: $score")
        }

        viewModel.score.observe(this, scoreObserver)
        binding.viewmodel = viewModel
        renderer.viewmodel = viewModel

        binding.onClickListener = View.OnClickListener {
            showHistory()
            /* do things */
            /* like getting the id of the clicked view: */
            //val idOfTheClickedView = it.id
            ///* or get variables from your databinding layout: */
            //val bankAccount = binding.bankAccount
        }

        //binding.debugAddButton.setOnClickListener { view ->
//
        //}

        var floatCurrentX = 0f
        var floatCurrentY = 0f

        var steppingX = 0f
        var steppingY = 0f
    }



    override fun onResume() {
        super.onResume()
        renderer.onResume()
        Log.d("VIOLET", "Fragment OnResume")
    }

    override fun onStart() {
        super.onStart()
        renderer.onStart()
        Log.d("VIOLET", "Fragment OnStart")
    }

    override fun onPause() {
        Log.d("VIOLET", "Fragment OnPause")
        renderer.onPause()
        super.onPause()
    }

    override fun onStop() {
        Log.d("VIOLET", "Fragment OnStop")
        renderer.onStop()
        super.onStop()
        //finish()
    }

    override fun onDestroy() {
        Log.d("VIOLET", "Fragment OnDestroy")
        renderer.onDestroy()
        super.onDestroy()
    }

    private fun hasOpenGLEs20Support(): Boolean {
        val activityManager = activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        return configurationInfo.reqGlEsVersion >= 0x20000
    }

    private fun initializeOpenGl2() {
        //var input = InputHandler()
        val scale = createBufferBitMap()
        //input.applyToView(binding.glSurface, scale.x, scale.y)
        binding.glSurface.setEGLContextClientVersion(2)
        renderer = TetrisGameView()
        renderer.context = context!!
        Log.d("VIOLET", "Fragment Init OpenGL")
        binding.glSurface.setRenderer(renderer)

        debugInput.applyToView(binding.glSurface, scale.x, scale.y)

        debugInput.onEventCatched = { touchEvent ->
            renderer.onTouchEvent(touchEvent)
        }
    }

    private fun createBufferBitMap(): Vector2 {
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val frameBufferWidth = if (isLandscape) 480 else 320
        val frameBufferHeight = if (isLandscape) 320 else 480
        val frameBuffer = Bitmap.createBitmap(frameBufferWidth,
            frameBufferHeight, Bitmap.Config.RGB_565)

        val windowManager = activity?.windowManager
        val scaleX = frameBufferWidth.toFloat() / windowManager!!.defaultDisplay.width
        val scaleY = frameBufferHeight.toFloat() / windowManager!!.defaultDisplay.height

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        return Vector2(scaleX, scaleY)
    }

    private fun executeOpenGlNotSupported() {
        // Ooooh crap, how can this even happen?
        // Maybe print some cookie fragment with a text?
    }

    private fun showHistory() {
        // historyFragment: WordHistoryFragment = WordHistoryFragment.newInstance()

        val callback = (requireActivity() as FragmentCallBack)
        callback.navigateToHistory(renderer.allWords)
    }

    private fun navigateToPause() {
        val callback = (requireActivity() as FragmentCallBack)
        callback.navigateToPause()
    }
}
