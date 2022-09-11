package com.distantlandgames.tetrisandwords.ui.main

import android.app.Activity
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.distantlandgames.tetrisandwords.FragmentCallBack
import com.distantlandgames.tetrisandwords.MainActivity
import com.distantlandgames.tetrisandwords.R
import com.distantlandgames.tetrisandwords.databinding.MainFragmentBinding
import com.distantlandgames.tetrisandwords.viewmodel.MainViewModel

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)

        binding.playGameButton.setOnClickListener {
            playGame()
        }

        binding.optionsButton.setOnClickListener {
            howTo()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Ain't they pretty to keep all around in the project?
    }

    private fun playGame() {
        //val gameFragment: GameFragment = GameFragment.newInstance()

        val callback = (requireActivity() as FragmentCallBack)
        callback.navigateToGame()
    }

    private fun howTo() {
        val callback = (requireActivity() as FragmentCallBack)
        callback.navigateToHowTo()
    }
}
