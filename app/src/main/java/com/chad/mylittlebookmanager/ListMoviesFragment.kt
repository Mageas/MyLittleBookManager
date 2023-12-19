package com.chad.mylittlebookmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chad.mylittlebookmanager.databinding.FragmentListMoviesBinding

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

}