package com.example.blocodenotas

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import com.example.blocodenotas.model.notepad

class DbHandler(private val context: Context) :SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {



    companion object{

        private const val DATABASE_VERSION = 3 // Database version
        private const val DATABASE_NAME = "Bloco_De_Notas.db" // Database name
        private const val TABLE_NAME = "bn" // Table Name

        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_NOTE = "note"
        private const val KEY_DATE = "date"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE bn(_id INTEGER PRIMARY KEY,title TEXT,image TEXT,note TEXT,date TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS   $TABLE_NAME")
        onCreate(db)
    }

    fun getAll():ArrayList<notepad>
    {
        val list: ArrayList<notepad> = ArrayList()
        val db=this.readableDatabase

        try {
        val cursor:Cursor=db.rawQuery("SELECT * FROM $TABLE_NAME",null)
            if (cursor.moveToFirst())
            {
                do {
                      val model = notepad(cursor.getInt(cursor.getColumnIndex(KEY_ID)),cursor.getString(cursor.getColumnIndex(
                          KEY_TITLE)),cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),cursor.getString(cursor.getColumnIndex(KEY_NOTE)),cursor.getString(cursor.getColumnIndex(
                          KEY_DATE)))
                        list.add(model)


                }while (cursor.moveToNext())


            }
            cursor.close()
        }catch (e:SQLException)
        {
            db.execSQL("SELECT * FROM $TABLE_NAME")
            return ArrayList()
        }


        return list
    }


    fun addNote(bn:notepad):Long
    {
    val db= this.writableDatabase
    val contentValues=ContentValues()
    contentValues.put(KEY_TITLE,bn.title)
        contentValues.put(KEY_IMAGE,bn.image)
        contentValues.put(KEY_NOTE,bn.note)
        contentValues.put(KEY_DATE,bn.date)

       val result= db.insert(TABLE_NAME,null,contentValues)
        db.close()
    return result

    }


    fun delete(np:notepad)
    {
        val db= this.writableDatabase
        db.delete(TABLE_NAME, KEY_ID + "=" + np.id,null)

        db.close()
    }

    fun editNote(np:notepad, text: String, image: String)
    {
        val db=this.writableDatabase
        val contentValues=ContentValues()
        contentValues.put(KEY_NOTE,text)
        contentValues.put(KEY_IMAGE,image)
        db.update(TABLE_NAME,contentValues, KEY_ID + "=" + np.id,null)
        db.close()


    }


}