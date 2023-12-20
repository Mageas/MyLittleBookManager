package com.chad.mylittlebookmanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chad.mylittlebookmanager.databinding.FragmentListItemsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class ListItemsFragment : Fragment() {

    private lateinit var binding: FragmentListItemsBinding
    private lateinit var listItems: ListView

    companion object {
        fun newInstance(): ListItemsFragment {
            return ListItemsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.listItems = binding.listMovies

        val api = Retrofit.Builder()
            .baseUrl("https://freetestapi.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<Api>()

        api.getItems().enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                response.body()?.let {
                    val adapter = ListItemsAdapter(requireContext(), it)
                    listItems.adapter = adapter

                    listItems.setOnItemClickListener { _, _, position, _ ->
                        Toast.makeText(requireContext(), "Row: ${adapter.getItem(position)}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                Log.i("TAG", "Error: ${t.message}")
            }
        })
    }

}