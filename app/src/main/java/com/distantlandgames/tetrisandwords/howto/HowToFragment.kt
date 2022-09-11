package com.distantlandgames.tetrisandwords.howto

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.distantlandgames.tetrisandwords.R

class HowToFragment : Fragment() {

    companion object {
        fun newInstance() = HowToFragment()
    }

    private lateinit var viewModel: HowToViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.how_to_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HowToViewModel::class.java)
        // TODO: Use the ViewModel
    }

}