package com.reviewapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.reviewapp.databinding.FragmentQrBinding

class QRFragment : Fragment() {

    private var _binding: FragmentQrBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrBinding.inflate(inflater, container, false)
        db = DatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGenerateQr.setOnClickListener {
            val link = binding.etQrLink.text.toString().trim()
            if (link.isEmpty()) {
                Toast.makeText(requireContext(), "प्लीज लिंक डालें", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            db.saveLinkStat(link)
            val bitmap = generateQrBitmap(link, 400, 400)
            binding.ivQrCode.setImageBitmap(bitmap)
        }

        binding.btnCopyLinkQr.setOnClickListener {
            val link = binding.etQrLink.text.toString().trim()
            if (link.isNotEmpty()) {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("label", link))
                Toast.makeText(requireContext(), "लिंक कॉपी हो गया", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateQrBitmap(text: String, width: Int, height: Int): Bitmap {
        val bitMatrix: BitMatrix
        try {
            bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height)
        } catch (e: Exception) {
            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}