package com.chad.mylittlebookmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/*
    Custom adapter for an ArrayAdapter<String>
    Display a list of Items
 */
//class ListItemsAdapter(context: Context, private val items: List<String>) :
//    ArrayAdapter<String>(context, R.layout.layout_item, items) {
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false)
//
//        val text = view.findViewById<TextView>(R.id.text_custom)
//        text.text = items[position]
//
//        return view
//    }
//
//}


class ListItemsAdapter(context: Context, private val items: List<Item>) :
    ArrayAdapter<Int>(context, R.layout.layout_item, items.map { it.id }) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false)

        val text = view.findViewById<TextView>(R.id.text_custom)
        text.text = items[position].title

        return view
    }
}