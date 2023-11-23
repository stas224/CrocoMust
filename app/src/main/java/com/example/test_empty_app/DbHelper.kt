package com.example.test_empty_app

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException

class DbHelper(private val context: Context) :
    SQLiteOpenHelper(context, "userWords", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val queryFirst =
            "CREATE TABLE IF NOT EXISTS userWords (id INT PRIMARY KEY, word VARCHAR(100), mode INT, idFirst INT)"
        db?.execSQL(queryFirst)
        val querySecond =
            "CREATE TABLE IF NOT EXISTS words (id INT PRIMARY KEY, wordRu VARCHAR(100), wordEng VARCHAR(100), mode INT)"
        db?.execSQL(querySecond)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val query = "DROP TABLE IF EXISTS userWords"
        db?.execSQL(query)
        val query2 = "DROP TABLE IF EXISTS words"
        db?.execSQL(query2)
        onCreate(db)
    }

    fun copyDatabase() {
        try {
            val inputStream = context.assets.open("db.db")
            val outputStream = FileOutputStream(context.filesDir.path + "db.db")
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
       moveDB()
    }

    @SuppressLint("Range")
    private fun moveDB(){
        val externalDb = SQLiteDatabase.openDatabase(context.filesDir.path + "db.db", null, SQLiteDatabase.OPEN_READONLY)
        val query = "SELECT * FROM words"
        val cursor = externalDb.rawQuery(query , null)
        cursor.use{
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndex("_id"))
                    val wordRu = it.getString(it.getColumnIndex("word_ru"))
                    val wordEng = it.getString(it.getColumnIndex("word_eng"))
                    val mode = it.getInt(it.getColumnIndex("mode"))

                    val wordDb = WordDb(id, wordRu, wordEng, mode)
                    addWordDb(wordDb)

                } while (it.moveToNext())
            }
        }
        externalDb.close()
    }

    @SuppressLint("Range")
    fun getWord(curLang: String, mode: Int): Pair<String, Int> {
        val db = this.readableDatabase
        val query = "SELECT $curLang, id FROM words WHERE id NOT IN (SELECT idFirst FROM userWords) AND mode=$mode ORDER BY RANDOM()"
        val cursor = db.rawQuery(query, null)
        var word = ""
        var id = 0

        cursor.use {
            if (it.moveToFirst()) {
                word = it.getString(it.getColumnIndex(curLang))
                id = it.getInt(it.getColumnIndex("id"))
            }
        }
        db.close()

        return Pair(word, id)
    }

    fun addWord(word: Word) {
        val values = ContentValues().apply {
            put("word", word.value)
            put("mode", word.mode)
            put("idFirst", word.idFirst)
        }

        val db = this.writableDatabase
        db.use { it.insert("userWords", null, values) }
    }

    private fun addWordDb(word: WordDb) {
        val values = ContentValues().apply {
            put("id", word.id)
            put("wordRu", word.wordRu)
            put("wordEng", word.wordEng)
            put("mode", word.mode)
        }

        val db = this.writableDatabase
        db.use { it.insert("words", null, values) }
    }

    @SuppressLint("Range")
    fun getHistory(): MutableList<String> {
        val history = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT * FROM userWords ORDER BY id DESC"
        val cursor = db.rawQuery(query, null)

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val word = it.getString(it.getColumnIndex("word"))
                    history.add(word)
                } while (it.moveToNext())
            }
        }
        db.close()

        return history
    }

    fun cleanHistory(){
        val db = this.writableDatabase
        val query = "DELETE from userWords"
        db.use{ it.execSQL(query) }
    }
}
