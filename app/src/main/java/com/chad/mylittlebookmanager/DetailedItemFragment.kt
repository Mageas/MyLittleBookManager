package com.chad.mylittlebookmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chad.mylittlebookmanager.databinding.FragmentDetailedItemBinding

class DetailedItemFragment(private val item: Character) : Fragment() {

    private lateinit var binding: FragmentDetailedItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailedItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailedId.text = item.name
    }

}