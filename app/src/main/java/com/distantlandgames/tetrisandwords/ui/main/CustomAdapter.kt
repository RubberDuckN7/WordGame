package com.distantlandgames.tetrisandwords.ui.main

import android.content.Intent
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.distantlandgames.tetrisandwords.R


class CustomAdapter(private val dataSet: List<String>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textView: TextView
        val mooView: TextView

        init {
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener { Log.d(TAG, "Element $adapterPosition clicked.") }
            textView = v.findViewById(R.id.historyWordId)
            mooView = v.findViewById(R.id.historyDescriptionId)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.history_word_item, viewGroup, false)

        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d("VIOLET", "Element $position set.")

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.textView.text = "Word: ${dataSet[position]}"
        viewHolder.mooView.text = "https://en.wiktionary.org/wiki/${dataSet[position]}"

        viewHolder.mooView.movementMethod = LinkMovementMethod.getInstance()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    companion object {
        private val TAG = "CustomAdapter"
    }
}