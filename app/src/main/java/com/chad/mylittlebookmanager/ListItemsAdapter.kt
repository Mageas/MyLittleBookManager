package com.chad.mylittlebookmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ListItemsAdapter(private val characters: List<Character>, private val listener: (Character) -> Unit) : RecyclerView.Adapter<ListItemsAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = characters.size


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = characters[position]
        holder.textCustom.text = item.name
        holder.cardItem.setOnClickListener { listener(item) }
    }

    class ItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        val textCustom: TextView = itemView.findViewById(R.id.text_custom)
        val cardItem: CardView = itemView.findViewById(R.id.card_item)
    }

}