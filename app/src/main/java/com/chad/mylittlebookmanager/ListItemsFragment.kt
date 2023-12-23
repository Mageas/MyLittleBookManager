package com.chad.mylittlebookmanager

import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
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
    private lateinit var recyclerView: RecyclerView
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.recyclerView = binding.recyclerViewItems

        val api = Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<Api>()

        val userId: String = run {
            val currentUser = auth.currentUser
            currentUser?.uid ?: throw Exception("No user is currently signed in.")
        }

        GlobalScope.launch {
            val favorites = getUserFavorites(userId)
            for (favorite in favorites) {
                Log.i("tag", "favorite: ${favorite}")
            }

            api.getItems().enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    response.body()?.let {
                        val items = ArrayList<Character>()
                        for (item in it.results) {
                            items.add(item)
                        }

                        recyclerView.layoutManager = LinearLayoutManager(context)
                        recyclerView.setHasFixedSize(true)
                        recyclerView.adapter = ListItemsAdapter(items.toList())
                        { character ->
                            val fragmentTransaction = parentFragmentManager.beginTransaction()
                            val detailedItemFragment = DetailedItemFragment(character)
                            fragmentTransaction.replace(R.id.fragment_container_view, detailedItemFragment)
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()
                        }

                    }
                }
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.i("TAG", "Error: ${t.message}")
                }
            })
        }

    }

    private suspend fun getUserFavorites(userId: String): List<Int> = suspendCoroutine { continuation ->
        val userFavoritesRef = db.collection("favorites").document(userId)

        userFavoritesRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val ids = document.get("ids") as List<Int>
                continuation.resume(ids)
            } else {
                continuation.resume(emptyList())
            }
        }.addOnFailureListener { e ->
            continuation.resumeWithException(e)
        }
    }

//    // Il faut refactor pour que les ids s'envoient a la fin
//    // de l'execution de .get()
//    private fun getUserFavorites(userId: String): List<String> {
//        val ids = mutableListOf<String>()
//        val userFavoritesRef = db.collection("favorites").document(userId)
//        userFavoritesRef.get().addOnSuccessListener { document ->
//            if (document.exists()) {
//                ids.addAll(document.get("ids") as List<String>)
//            }
//        }.addOnFailureListener { e ->
//            Log.i("tag", "Error: ${e.message}", e)
//        }
//
//        return ids
//    }

}
