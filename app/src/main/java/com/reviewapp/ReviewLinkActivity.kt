package com.reviewapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.reviewapp.databinding.ActivityReviewLinkBinding

class ReviewLinkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewLinkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val link = intent.getStringExtra("generated_link") ?: ""
        val aiReview = intent.getStringExtra("ai_review") ?: "कोई रिव्यू नहीं"

        binding.tvRlGeneratedLink.text = link
        binding.tvRlTitle.text = "AI रिव्यू: $aiReview"

        // QR जनरेट करें लिंक के लिए
        val bitmap = generateQrBitmap(link, 400, 400)
        binding.ivRlQr.setImageBitmap(bitmap)

        binding.btnRlCopy.setOnClickListener {
            android.content.ClipboardManager.getSystemService(requireContext(), android.content.Context.CLIPBOARD_SERVICE)
                .setPrimaryClip(android.content.ClipData.newPlainText("label", link))
            Toast.makeText(this, "लिंक कॉपी हो गया", Toast.LENGTH_SHORT).show()
        }

        binding.btnRlOpen.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(browserIntent)
            Toast.makeText(this, "रिव्यू पेज खुल रहा है। ऊपर दिखाया AI रिव्यू कॉपी करके पेस्ट करें।", Toast.LENGTH_LONG).show()
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
}