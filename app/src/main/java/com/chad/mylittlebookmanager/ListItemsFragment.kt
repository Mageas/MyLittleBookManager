package com.chad.mylittlebookmanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.mylittlebookmanager.databinding.FragmentListItemsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class ListItemsFragment : Fragment() {

    private lateinit var binding: FragmentListItemsBinding
    private lateinit var listItems: RecyclerView

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
        this.listItems = binding.recyclerViewItems

        val api = Retrofit.Builder()
            .baseUrl("https://freetestapi.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<Api>()

        api.getItems().enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                response.body()?.let {
                    val items = ArrayList<Item>()
                    for (item in it) {
                        items.add(item)
                    }

                    listItems.layoutManager = LinearLayoutManager(context)
                    listItems.setHasFixedSize(true)
                    listItems.adapter = ListItemsAdapter(items.toList())
                    {
                        Toast.makeText(context,"Vous avez sélectionné ${it.id} ${it.title}",Toast.LENGTH_SHORT).show()
                    }

                }
            }

            override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                Log.i("TAG", "Error: ${t.message}")
            }
        })
    }

}