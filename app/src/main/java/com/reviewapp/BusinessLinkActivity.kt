package com.reviewapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.reviewapp.databinding.ActivityBusinessLinkBinding

class BusinessLinkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBusinessLinkBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)

        binding.btnBlGenerate.setOnClickListener {
            val link = binding.etBlLink.text.toString().trim()
            if (link.isEmpty()) {
                Toast.makeText(this, "प्लीज गूगल बिजनेस रिव्यू लिंक डालें", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // AI से रिव्यू जनरेट कराएं
            ApiService.generateReview(
                "यह एक गूगल बिजनेस रिव्यू पेज लिंक है: $link. इस लिंक के लिए एक छोटा, सकारात्मक और प्रोफेशनल गूगल बिजनेस रिव्यू हिंदी में लिखें जिसे ग्राहक कॉपी करके पेस्ट कर सके। केवल रिव्यू टेक्स्ट दें।",
                object : ApiService.AiCallback {
                    override fun onSuccess(aiReview: String) {
                        db.saveLinkStat(link)
                        val intent = Intent(this@BusinessLinkActivity, ReviewLinkActivity::class.java)
                        intent.putExtra("generated_link", link)
                        intent.putExtra("ai_review", aiReview)
                        startActivity(intent)
                    }

                    override fun onError(error: String) {
                        Toast.makeText(this@BusinessLinkActivity, "AI एरर: $error", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }
}