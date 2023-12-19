package com.chad.mylittlebookmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ItemListAdapter(context: Context, private val movies: List<String>) :
    ArrayAdapter<String>(context, R.layout.item_custom_layout, movies) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_custom_layout, parent, false)

        val text = view.findViewById<TextView>(R.id.text_custom)
        text.text = movies[position]

        return view
    }

}