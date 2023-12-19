package com.chad.mylittlebookmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chad.mylittlebookmanager.databinding.FragmentListMoviesBinding

class ListMoviesFragment : Fragment() {

    private lateinit var binding: FragmentListMoviesBinding
    private lateinit var listMovies: ListView;

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

        this.listMovies = binding.listMovies

        val simpleArray = arrayOf("Test1", "Test2", "Test3")

        val adapter = ItemListAdapter(requireContext(), simpleArray.toList())

        this.listMovies.adapter = adapter

        this.listMovies.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(requireContext(), "Row: ${adapter.getItem(position)}", Toast.LENGTH_LONG).show()
        }

//        val simpleArray = arrayOf(1, 2, 3, 4)
//
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, simpleArray)
//        this.listMovies.adapter = adapter
//
//        this.listMovies.setOnItemClickListener { parent, view, position, id ->
//            Toast.makeText(requireContext(), "Number: ${adapter.getItem(position)}", Toast.LENGTH_LONG).show()
//        }
    }
}