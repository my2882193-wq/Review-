package com.reviewapp

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.reviewapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)

        binding.btnGoogleLogin.setOnClickListener {
            doGoogleLogin()
        }

        binding.btnLinkLogin.setOnClickListener {
            val link = binding.etBusinessLink.text.toString().trim()
            if (link.isEmpty()) {
                Toast.makeText(this, "प्लीज बिजनेस लिंक डालें", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            db.saveUser(null, null)
            db.saveLinkStat(link)
            startActivity(Intent(this, ReviewLinkActivity::class.java).apply {
                putExtra("generated_link", link)
                putExtra("ai_review", "यह ऑटो-जेनरेटेड रिव्यू लिंक है। कृपया यहां अपना रिव्यू सबमिट करें।")
            })
        }
    }

    private fun doGoogleLogin() {
        val accountManager = AccountManager.get(this)
        val accounts: Array<Account> = accountManager.getAccountsByType("com.google")

        if (accounts.isEmpty()) {
            Toast.makeText(
                this,
                "इस फोन में कोई गूगल अकाउंट नहीं मिला। नीचे बिजनेस लिंक डालें।",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // पहले अकाउंट को सेलेक्ट करें (डायरेक्ट लॉगिन, कोई क्लाइंट आईडी नहीं)
        val account = accounts[0]
        val email = account.name

        db.saveUser(email, email)
        Toast.makeText(this, "लॉगिन सफल: $email", Toast.LENGTH_SHORT).show()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}