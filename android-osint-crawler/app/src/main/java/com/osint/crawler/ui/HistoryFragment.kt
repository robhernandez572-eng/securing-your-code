package com.osint.crawler.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.osint.crawler.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Future: wire up Room DB to show past scan sessions
        binding.tvEmpty.text = "Past scans will appear here.\nStart a new scan to get started."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
