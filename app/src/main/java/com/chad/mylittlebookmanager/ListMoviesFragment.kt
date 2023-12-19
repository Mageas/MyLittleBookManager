package com.chad.mylittlebookmanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chad.mylittlebookmanager.databinding.FragmentListMoviesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


class ListMoviesFragment : Fragment() {

    private lateinit var binding: FragmentListMoviesBinding;

    companion object {
        fun newInstance(): ListMoviesFragment {
            return ListMoviesFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.getMovies()
    }

    private fun getMovies() {
        val api = Retrofit.Builder()
            .baseUrl("https://freetestapi.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<Api>()

        api.getMovies().enqueue(object : Callback<List<Movie>> {
            override fun onResponse(call: Call<List<Movie>>, response: Response<List<Movie>>) {
                response.body()?.let {
                    for (movie in it) {
                        Log.i("TAG", "Movie: ${movie.title}")

//                        val bundle = Bundle()
//                        bundle.putString("movieTitle", movie.title)
//
//                        val fragmentTransaction = parentFragmentManager.beginTransaction()
//                        val movieFragment = MovieFragment.newInstance()
//
//                        movieFragment.arguments = bundle
//
//                        fragmentTransaction.add(R.id.movies_container, movieFragment)
//                        fragmentTransaction.commit()
                    }
                }
            }

            override fun onFailure(call: Call<List<Movie>>, t: Throwable) {
                Log.i("TAG", "Error: ${t.message}")
            }
        })
    }

}