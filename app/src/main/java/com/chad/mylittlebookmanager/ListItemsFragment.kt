package com.chad.mylittlebookmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private var page = 1

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
            val items = fetchItems(api, page)
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

    private suspend fun fetchItems(api: Api, page: Int): List<Character> = suspendCoroutine { continuation ->
        api.getItems(page).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                response.body()?.let {
                    continuation.resume(it.results)
                } ?: continuation.resumeWithException(RuntimeException("Response body is null"))
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
        this.page += 1
    }

    override fun onResume() {
        super.onResume()
        page = 1
    }

    private fun setupRecyclerView(items: List<Character>, favorites: List<Int>) {
        val linearLayoutManager = LinearLayoutManager(context)
        val adapter = ListItemsAdapter(items, favorites) { character ->
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            val detailedItemFragment = DetailedItemFragment(character, favorites.contains(character.id))
            fragmentTransaction.replace(R.id.fragment_container_view, detailedItemFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.recyclerViewItems.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            this.adapter = adapter
        }

        val api = createApi()
        var isLoading = false
        binding.recyclerViewItems.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = linearLayoutManager.itemCount
                val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= lastVisibleItem + 5) {
                    isLoading = true
                    scope.launch {
                        val newItems = fetchItems(api, page)
                        adapter.addItems(newItems)
                        isLoading = false
                    }
                }
            }
        })
    }

}
