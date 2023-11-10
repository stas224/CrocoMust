package com.example.test_empty_app

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private var wordValue: String? = null
    private var mode: Int = 1
    private var click: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)

        val card: TextView = findViewById(R.id.textViewCardWord)
        val buttonNewWord: Button = findViewById(R.id.buttonNewWord)
        val buttonMode : Button = findViewById(R.id.buttonMode)
        val buttonRules: Button = findViewById(R.id.buttonRules)
        val backArrow: ImageView = findViewById(R.id.backArrow)

        val db = DbHelper(this, null)

        savedInstanceState?.also {
            wordValue = it.getString("Key")
            mode = it.getInt("Mode")
            click = it.getBoolean("Click")
        }

        if (wordValue != null) card.text = wordValue
        else{
            with(db.getLastWord()) {
                if (first != "") {
                    card.text = first
                    mode = second
                }
            }
        }

        if (mode == 2) buttonMode.text = getString(R.string.hardMode)

        backArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        buttonNewWord.setOnClickListener {
            val curLang =  if(resources.configuration.locales[0].language == "ru") "wordRu" else "wordEng"
            var pairWordMode = db.getWord(curLang, mode)
            wordValue = pairWordMode.first

            if (wordValue == "") {
                db.copyDatabase()
                pairWordMode = db.getWord(curLang, mode)
                wordValue = pairWordMode.first
                }

            val word = Word(wordValue!!, mode, pairWordMode.second)
            db.addWord(word)
            card.text = wordValue
        }

        buttonMode.setOnClickListener {
            val modeEasy = getString(R.string.easyMode)
            val modeHard = getString(R.string.hardMode)
            buttonMode.text = if (buttonMode.text == modeEasy) {
                mode = 2
                modeHard
            } else{
                mode = 1
                modeEasy
            }
        }

        buttonRules.setOnClickListener{
            click = true

            val dialogBinding = layoutInflater.inflate(R.layout.dialog_rules, null)
            val butOk : Button = dialogBinding.findViewById(R.id.ButtonOk)
            val dialog = Dialog(this)

            dialog.setContentView(dialogBinding)
            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            dialog.setOnCancelListener { click = false }

            butOk.setOnClickListener{
                click = false
                dialog.dismiss()
            }
        }

        if (click){
            buttonRules.performClick()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("Word", wordValue)
        outState.putInt("Mode", mode)
        outState.putBoolean("Click", click)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        wordValue = savedInstanceState.getString("Word")
        mode = savedInstanceState.getInt("Mode")
        click = savedInstanceState.getBoolean("Click")
    }


}

