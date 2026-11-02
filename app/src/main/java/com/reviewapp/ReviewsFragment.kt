package com.reviewapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.reviewapp.databinding.FragmentReviewsBinding

class ReviewsFragment : Fragment() {

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        db = DatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAiReply.setOnClickListener {
            val reviewText = binding.etReviewText.text.toString().trim()
            if (reviewText.isEmpty()) {
                Toast.makeText(requireContext(), "प्लीज रिव्यू डालें", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ApiService.generateReply(reviewText, object : ApiService.AiCallback {
                override fun onSuccess(text: String) {
                    binding.tvAiReply.text = text
                    db.saveReview(reviewText, text)
                    Toast.makeText(requireContext(), "AI रिप्लाई सेव हो गया", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: String) {
                    Toast.makeText(requireContext(), "एरर: $error", Toast.LENGTH_LONG).show()
                }
            })
        }

        binding.btnOpenReviewPage.setOnClickListener {
            // रिव्यू पेज खोलने के लिए लिंक की जरूरत होगी, यहां टोस्ट दिखाएं
            Toast.makeText(requireContext(), "बिजनेस लिंक सेक्शन से लिंक जनरेट करें", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}