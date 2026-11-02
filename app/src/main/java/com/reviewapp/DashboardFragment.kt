package com.reviewapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.reviewapp.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        db = DatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadStats()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadStats() {
        val stats = db.getStats()
        binding.tvTotalReviews.text = stats["reviews"]?.toString() ?: "0"
        binding.tvLinkClicks.text = stats["clicks"]?.toString() ?: "0"
        binding.tvQrScans.text = stats["qr"]?.toString() ?: "0"
        binding.tvTotalReplies.text = stats["replies"]?.toString() ?: "0"
    }
}