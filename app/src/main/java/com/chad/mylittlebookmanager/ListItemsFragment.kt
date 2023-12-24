package com.chad.mylittlebookmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.mylittlebookmanager.databinding.FragmentListItemsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ListItemsFragment : Fragment() {
    private lateinit var binding: FragmentListItemsBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val api = createApi()
        val userId = getCurrentUserId()

        scope.launch {
            val favorites = getUserFavorites(userId)
            val items = fetchItems(api)
            setupRecyclerView(items, favorites)
        }
    }

    private fun createApi(): Api {
        return Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

    private fun getCurrentUserId(): String {
        val currentUser = auth.currentUser
        return currentUser?.uid ?: throw Exception("No user is currently signed in.")
    }

    private suspend fun getUserFavorites(userId: String): List<Int> = suspendCoroutine { continuation ->
        val userFavoritesRef = db.collection("favorites").document(userId)
        userFavoritesRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val ids = document.get("ids") as? List<Double>
                val intIds = ids?.map { it.toInt() } ?: emptyList()
                continuation.resume(intIds)
            } else {
                continuation.resume(emptyList())
            }
        }.addOnFailureListener { e ->
            continuation.resumeWithException(e)
        }
    }

    private suspend fun fetchItems(api: Api): List<Character> = suspendCoroutine { continuation ->
        api.getItems().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                response.body()?.let {
                    continuation.resume(it.results)
                } ?: continuation.resumeWithException(RuntimeException("Response body is null"))
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }

    private fun setupRecyclerView(character: List<Character>, favorites: List<Int>) {
        binding.recyclerViewItems.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = ListItemsAdapter(character, favorites) { character ->
                val fragmentTransaction = parentFragmentManager.beginTransaction()
                val detailedItemFragment = DetailedItemFragment(character, favorites.contains(character.id))
                fragmentTransaction.replace(R.id.fragment_container_view, detailedItemFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }
    }
}