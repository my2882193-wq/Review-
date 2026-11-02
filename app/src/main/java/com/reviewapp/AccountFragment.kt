package com.reviewapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.reviewapp.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        db = DatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = db.getLastUser()
        if (user != null && user.first != null) {
            binding.tvAccountEmail.text = user.first
            binding.tvAccountType.text = "गूगल अकाउंट से लॉगिन"
        } else {
            binding.tvAccountEmail.text = requireContext().getString(R.string.no_account)
            binding.tvAccountType.text = "कोई लॉगिन नहीं"
        }

        binding.btnSignOut.setOnClickListener {
            db.clearUsers()
            db.clearAllStats()
            Toast.makeText(requireContext(), "साइन आउट हो गए", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}