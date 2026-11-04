package com.reviewapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
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

        val bitmap = generateQrBitmap(link, 400, 400)
        binding.ivRlQr.setImageBitmap(bitmap)

        binding.btnRlCopy.setOnClickListener {
            val link = binding.etQrLink?.text?.toString() ?: ""
            if (link.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("label", link))
                Toast.makeText(this, "लिंक कॉपी हो गया", Toast.LENGTH_SHORT).show()
                
                // Open Google Review page with the link
                val reviewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                startActivity(reviewIntent)
                Toast.makeText(this, "गूगल रिव्यू पेज खोला गया", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRlOpen.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(browserIntent)
            Toast.makeText(this, "रिव्यू पेज खुल रहा है। ऊपर दिखाया AI रिव्यू कॉपी करके पेस्ट करें।", Toast.LENGTH_SHORT).show()
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