package com.example.note_mobile_application

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NoteDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private val DATABASE_NAME = "notesapp.db"
        private val DATABASE_VERSION = 1
        private val TABLE_NAME = "allnotes"
        private val COLUM_ID = "id"
        private val COLUMN_TITEL = "title"
        private val COLUMN_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUM_ID INTEGER PRIMARY KEY , $COLUMN_TITEL TEXT, $COLUMN_CONTENT TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertNote(note: Note){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITEL, note.title)
            put(COLUMN_CONTENT, note.content)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllNotes(): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUM_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITEL))
            val context = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))

            val note = Note(id, title, context)
            notesList.add(note)
        }
        cursor.close()
        db.close()
        return notesList
    }

    fun updateNote(note: Note){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITEL, note.title)
            put(COLUMN_CONTENT, note.content)
        }
        val whereClause = "$COLUM_ID = ?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getNoteById(noteID: Int): Note {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUM_ID = $noteID"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUM_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITEL))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))

        cursor.close()
        db.close()
        return Note (id, title, content)
    }

    fun deleteNote(noteID: Int){
        val db = writableDatabase
        val whereClause = "$COLUM_ID = ?"
        val whereArgs = arrayOf(noteID.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

}