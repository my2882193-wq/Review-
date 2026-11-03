package com.reviewapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Date

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "review_app_db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE user_account (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT,
                name TEXT,
                login_time TEXT
            )
        """)
        db.execSQL("""
            CREATE TABLE reviews (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                review_text TEXT,
                ai_reply TEXT,
                created_at TEXT
            )
        """)
        db.execSQL("""
            CREATE TABLE link_stats (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                link TEXT,
                clicks INTEGER DEFAULT 0,
                qr_scans INTEGER DEFAULT 0,
                replies INTEGER DEFAULT 0,
                created_at TEXT
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS user_account")
        db.execSQL("DROP TABLE IF EXISTS reviews")
        db.execSQL("DROP TABLE IF EXISTS link_stats")
        onCreate(db)
    }

    fun saveUser(email: String?, name: String?) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("email", email)
        cv.put("name", name)
        cv.put("login_time", Date().toString())
        db.insert("user_account", null, cv)
        db.close()
    }

    fun getLastUser(): Pair<String?, String?>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT email, name FROM user_account ORDER BY id DESC LIMIT 1", null)
        var result: Pair<String?, String?>? = null
        if (cursor.moveToFirst()) {
            result = Pair(cursor.getString(0), cursor.getString(1))
        }
        cursor.close()
        db.close()
        return result
    }

    fun clearUsers() {
        val db = this.writableDatabase
        db.delete("user_account", null, null)
        db.close()
    }

    fun saveReview(reviewText: String, aiReply: String?) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("review_text", reviewText)
        cv.put("ai_reply", aiReply)
        cv.put("created_at", Date().toString())
        db.insert("reviews", null, cv)
        db.close()
    }

    fun getAllReviews(): List<Map<String, String?>> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT review_text, ai_reply, created_at FROM reviews ORDER BY id DESC", null)
        val list = mutableListOf<Map<String, String?>>()
        while (cursor.moveToNext()) {
            list.add(mapOf(
                "review" to cursor.getString(0),
                "reply" to cursor.getString(1),
                "date" to cursor.getString(2)
            ))
        }
        cursor.close()
        db.close()
        return list
    }

    fun saveLinkStat(link: String) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("link", link)
        cv.put("clicks", 0)
        cv.put("qr_scans", 0)
        cv.put("replies", 0)
        cv.put("created_at", Date().toString())
        db.insert("link_stats", null, cv)
        db.close()
    }

    fun incrementClick(link: String) {
        val db = this.writableDatabase
        db.execSQL("UPDATE link_stats SET clicks = clicks + 1 WHERE link = ?", arrayOf(link))
        db.close()
    }

    fun incrementQrScan(link: String) {
        val db = this.writableDatabase
        db.execSQL("UPDATE link_stats SET qr_scans = qr_scans + 1 WHERE link = ?", arrayOf(link))
        db.close()
    }

    fun incrementReply(link: String) {
        val db = this.writableDatabase
        db.execSQL("UPDATE link_stats SET replies = replies + 1 WHERE link = ?", arrayOf(link))
        db.close()
    }

    fun getStats(): Map<String, Int> {
        val db = this.readableDatabase
        val result = mutableMapOf<String, Int>()
        var totalReviews = 0
        var totalClicks = 0
        var totalQr = 0
        var totalReplies = 0

        val c1 = db.rawQuery("SELECT COUNT(*) FROM reviews", null)
        if (c1.moveToFirst()) totalReviews = c1.getInt(0)
        c1.close()

        val c2 = db.rawQuery("SELECT SUM(clicks), SUM(qr_scans), SUM(replies) FROM link_stats", null)
        if (c2.moveToFirst()) {
            totalClicks = c2.getInt(0)
            totalQr = c2.getInt(1)
            totalReplies = c2.getInt(2)
        }
        c2.close()
        db.close()

        result["reviews"] = totalReviews
        result["clicks"] = totalClicks
        result["qr"] = totalQr
        result["replies"] = totalReplies
        return result
    }

    fun clearAllStats() {
        val db = this.writableDatabase
        db.delete("reviews", null, null)
        db.delete("link_stats", null, null)
        db.close()
    }
}