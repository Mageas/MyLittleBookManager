package com.chad.mylittlebookmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chad.mylittlebookmanager.databinding.FragmentListItemsBinding

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

        val simpleArray = arrayOf("Test1", "Test2", "Test3")

        val adapter = ListItemsAdapter(requireContext(), simpleArray.toList())

        this.listItems.adapter = adapter

        this.listItems.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(requireContext(), "Row: ${adapter.getItem(position)}", Toast.LENGTH_LONG).show()
        }
    }

}