package com.distantlandgames.tetrisandwords.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.distantlandgames.tetrisandwords.R

class PauseScreenFragment : Fragment() {

    companion object {
        fun newInstance() = PauseScreenFragment()
    }

    private lateinit var viewModel: PauseScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pause_screen_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PauseScreenViewModel::class.java)
        // TODO: Use the ViewModel
    }

}