package com.example.test_empty_app

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_history)

        val backArrow: ImageView = findViewById(R.id.backArrow)
        val listViewHistory: ListView = findViewById(R.id.ListViewHistory)

        val db = DbHelper(this)
        val wordsHistory = db.getHistory()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, wordsHistory)
        listViewHistory.adapter = adapter

        backArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
