package com.example.test_empty_app

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException

class DbHelper(private val context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "userWords", factory, 1) {

    @SuppressLint("Range")
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
    fun moveDB(){
        val externalDb = SQLiteDatabase.openDatabase(context.filesDir.path + "db.db", null, SQLiteDatabase.OPEN_READONLY)
        val query2 = "SELECT * FROM words"
        val cursor = externalDb.rawQuery(query2, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("_id"))
                val wordRu = cursor.getString(cursor.getColumnIndex("word_ru"))
                val wordEng = cursor.getString(cursor.getColumnIndex("word_eng"))
                val mode = cursor.getInt(cursor.getColumnIndex("mode"))

                val wordDb = WordDb(id, wordRu, wordEng, mode)
                addWordDb(wordDb)

            } while (cursor.moveToNext())
        }
        cursor.close()
        externalDb.close()
    }

    @SuppressLint("Range", "Recycle")
    fun getWord(curLang: String, mode: Int): Pair<String, Int> {
        val db = this.readableDatabase
        val query = "SELECT $curLang, id FROM words WHERE id NOT IN (SELECT idFirst FROM userWords) AND mode=$mode ORDER BY RANDOM()"
        val cursor = db.rawQuery(query, null)
        var word = ""
        var id = 0

        if (cursor.moveToFirst()) {
            word = cursor.getString(cursor.getColumnIndex(curLang))
            id = cursor.getInt(cursor.getColumnIndex("id"))
        }

        cursor.close()
        db.close()

        return Pair(word, id)
    }

    fun addWord(word: Word) {
        val values = ContentValues()
        values.put("word", word.value)
        values.put("mode", word.mode)
        values.put("idFirst", word.idFirst)

        val db = this.writableDatabase
        with(db) {
            insert("userWords", null, values)
            close()
        }
    }

    private fun addWordDb(word: WordDb) {
        val values = ContentValues()
        values.put("id", word.id)
        values.put("wordRu", word.wordRu)
        values.put("wordEng", word.wordEng)
        values.put("mode", word.mode)

        val db = this.writableDatabase
        with(db) {
            insert("words", null, values)
            close()
        }
    }

    fun cleanHistory(){
        val db = this.writableDatabase
        val query = "DELETE from userWords"
        db?.execSQL(query)
    }

    @SuppressLint("Range","Recycle")
    fun getHistory(): MutableList<String> {
        val db = this.readableDatabase
        val query = "SELECT * FROM userWords"
        val cursor = db.rawQuery(query, null)
        val history = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                val word = cursor.getString(cursor.getColumnIndex("word"))
                history.add(word)

            } while (cursor.moveToNext())
        }
        return history

    }

    @SuppressLint("Range", "Recycle")
    fun getLastWord(): Pair<String, Int>{
        val db = this.readableDatabase
        val query = "SELECT word, mode FROM userWords ORDER BY id DESC LIMIT 1"
        val cursor = db.rawQuery(query, null)
        var lastWord = ""
        var lastMode = 1

        if (cursor.moveToFirst()){
            lastWord = cursor.getString(cursor.getColumnIndex("word"))
            lastMode = cursor.getInt(cursor.getColumnIndex("mode"))
        }

        return Pair(lastWord, lastMode)
    }

}


