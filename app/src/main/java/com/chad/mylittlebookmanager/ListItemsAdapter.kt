package com.chad.mylittlebookmanager

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ListItemsAdapter(private var characters: List<Character>, private val favorites: List<Int>, private val listener: (Character) -> Unit) : RecyclerView.Adapter<ListItemsAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = characters.size


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = characters[position]
        holder.title.text = item.name
        when (item.status) {
            "Alive" -> holder.alive.text = "\uD83D\uDFE2"
            "Dead" -> holder.alive.text = "\uD83D\uDD34"
            else -> holder.alive.text = "\uD83D\uDFE0"
        }
        holder.species.text = item.species
        holder.favorite.visibility = if (favorites.contains(item.id)) View.VISIBLE else View.INVISIBLE
        Glide.with(holder.itemView.context)
            .load(item.image)
            .into(holder.image)
        holder.card.setOnClickListener { listener(item) }
    }

    class ItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        val card: CardView = itemView.findViewById(R.id.card_item)
        val image: ImageView = itemView.findViewById(R.id.list_item_image)
        val title: TextView = itemView.findViewById(R.id.list_item_title)
        val favorite: TextView = itemView.findViewById(R.id.list_item_favorite)
        val alive: TextView = itemView.findViewById(R.id.list_item_alive)
        val species: TextView = itemView.findViewById(R.id.list_item_species)
    }

    fun addItems(newItems: List<Character>) {
        val startPosition = characters.size
        characters = characters.plus(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }

}