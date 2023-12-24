package com.chad.mylittlebookmanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.chad.mylittlebookmanager.databinding.FragmentDetailedItemBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class DetailedItemFragment(private val item: Character, private var isFavorite: Boolean) : Fragment() {

    private lateinit var binding: FragmentDetailedItemBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailedItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = getCurrentUserId()

        populateView()
        binding.detailedItemAddFavorite.setOnClickListener(onClickAddFavorite(userId))
        binding.detailedItemRemoveFavorite.setOnClickListener(onClickRemoveFavorite(userId))
    }

    private fun onClickAddFavorite(userId: String): View.OnClickListener {
        return OnClickListener {
            val userFavoritesRef = db.collection("favorites").document(userId)

            userFavoritesRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result?.exists() == true) {
                        userFavoritesRef.update("ids", FieldValue.arrayUnion(item.id)).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                isFavorite = true
                                updatableValues()
                            } else {
                                Toast.makeText(context, "Unable to add this item to favorite", Toast.LENGTH_LONG).show()
                                Log.w("TAG", "Error updating document", task.exception)
                            }
                        }
                    } else {
                        userFavoritesRef.set(hashMapOf("ids" to listOf(item.id)), SetOptions.merge()).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                isFavorite = true
                                updatableValues()
                            } else {
                                Toast.makeText(context, "Unable to add this item to favorite", Toast.LENGTH_LONG).show()
                                Log.w("TAG", "Error creating document", task.exception)
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Unable to add this item to favorite", Toast.LENGTH_LONG).show()
                    Log.w("TAG", "Error getting document", task.exception)
                }
            }
        }
    }

    private fun onClickRemoveFavorite(userId: String): View.OnClickListener {
        return OnClickListener {
            val userFavoritesRef = db.collection("favorites").document(userId)

            userFavoritesRef.update("ids", FieldValue.arrayRemove(item.id)).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isFavorite = false
                    updatableValues()
                } else {
                    Toast.makeText(context, "Unable to remove this item from favorite", Toast.LENGTH_LONG).show()
                    Log.w("TAG", "Error updating document", task.exception)
                }
            }
        }
    }

    private fun updatableValues() {
        binding.detailedItemTitle.text = if (isFavorite) "${getString(R.string.favorite)} ${item.name}" else item.name

        if (isFavorite) {
            binding.detailedItemAddFavorite.visibility = INVISIBLE
            binding.detailedItemRemoveFavorite.visibility = VISIBLE
        } else {
            binding.detailedItemAddFavorite.visibility = VISIBLE
            binding.detailedItemRemoveFavorite.visibility = INVISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateView() {
        when (item.status) {
            "Alive" -> binding.detailedItemAlive.text = "\uD83D\uDFE2"
            "Dead" -> binding.detailedItemAlive.text = "\uD83D\uDD34"
            else -> binding.detailedItemAlive.text = "\uD83D\uDFE0"
        }
        binding.detailedItemSpecies.text = item.species
        binding.detailedItemGender.text = item.gender
        binding.detailedItemOrigin.text = item.origin.name
        binding.detailedItemLocation.text = item.location.name
        binding.detailedItemEpisodes.text =
            item.episode.map { it.split("/").last() }.joinToString(", ")
        updatableValues()

        Glide.with(binding.detailedItemImage.context)
            .load(item.image)
            .into(binding.detailedItemImage)
    }

    private fun getCurrentUserId(): String {
        val currentUser = auth.currentUser
        return currentUser?.uid ?: throw Exception("No user is currently signed in.")
    }

}