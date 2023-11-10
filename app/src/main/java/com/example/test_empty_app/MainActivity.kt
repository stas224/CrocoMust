package com.example.test_empty_app

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonNewGame: Button = findViewById(R.id.buttonNewGame)
        val buttonContinue: Button = findViewById(R.id.buttonContinue)
        val buttonHistory: Button = findViewById(R.id.buttonHistory)

        buttonNewGame.setOnClickListener {
            val db = DbHelper(this, null)
            db.cleanHistory()
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        buttonContinue.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        buttonHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isDarkThemeEnabled(context: Context): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}


