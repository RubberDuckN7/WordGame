package com.distantlandgames.tetrisandwords.ui.main

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.distantlandgames.tetrisandwords.R
import com.distantlandgames.tetrisandwords.tetris.WordDescription
import java.util.*
import kotlin.collections.ArrayList

class WordHistoryFragment(var listOfWords: Vector<String>) : Fragment() {
    companion object {
        private val KEY_LAYOUT_MANAGER = "layoutManager"
        private val SPAN_COUNT = 2
        private val DATASET_COUNT = 60

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WordsHistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(listOfWords: Vector<String>) = WordHistoryFragment(listOfWords)
    }

    enum class LayoutManagerType { GRID_LAYOUT_MANAGER, LINEAR_LAYOUT_MANAGER }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var currentLayoutManagerType: LayoutManagerType
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var wordsAndDescriptions = ArrayList<WordDescription>()
    //private var dataset: Array<String> = Array(DATASET_COUNT, {i -> "Words and description!"})

    private lateinit var dataset: List<String>
    private lateinit var viewModel: WordHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("arg 1")
            param2 = it.getString("arg 2")
        }

        dataset = listOfWords.distinct() //Array(DATASET_COUNT, {i -> "This is element # $i"})
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.word_history_fragment, container, false)

        // Recycler view!
        recyclerView = rootView.findViewById(R.id.my_recycler_view)

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        layoutManager = LinearLayoutManager(activity)

        currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            currentLayoutManagerType = savedInstanceState
                .getSerializable(KEY_LAYOUT_MANAGER) as LayoutManagerType
        }
        setRecyclerViewLayoutManager(currentLayoutManagerType)

        // Set CustomAdapter as the adapter for RecyclerView.
        recyclerView.adapter = CustomAdapter(dataset)

        //rootView.findViewById<RadioButton>(R.id.linear_layout_rb).setOnClickListener{
        //    setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER)
        //}
//
        //rootView.findViewById<RadioButton>(R.id.grid_layout_rb).setOnClickListener{
        //    setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER)
        //}

        return rootView


    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    private fun setRecyclerViewLayoutManager(layoutManagerType: LayoutManagerType) {
        var scrollPosition = 0

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.layoutManager != null) {
            scrollPosition = (recyclerView.layoutManager as LinearLayoutManager)
                .findFirstCompletelyVisibleItemPosition()
        }

        when (layoutManagerType) {
            WordHistoryFragment.LayoutManagerType.GRID_LAYOUT_MANAGER -> {
                layoutManager = GridLayoutManager(activity, SPAN_COUNT)
                currentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER
            }
            WordHistoryFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER -> {
                layoutManager = LinearLayoutManager(activity)
                currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER
            }
        }

        with(recyclerView) {
            layoutManager = this@WordHistoryFragment.layoutManager
            scrollToPosition(scrollPosition)
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WordHistoryViewModel::class.java)
        // TODO: Use the ViewModel
    }


}