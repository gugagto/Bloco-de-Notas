package com.example.blocodenotas

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.blocodenotas.model.notepad

class SharedAnottation(private val context: Context) {

    private val  mSharedPreferences:SharedPreferences

    companion object{
        val list:ArrayList<notepad> = ArrayList()
    }

init {
     mSharedPreferences= context.getSharedPreferences("NotePad",Context.MODE_PRIVATE)


}

    fun save(bn:String,title:String){


        val editor =mSharedPreferences.edit()
        editor.putString("bn",bn)
        editor.putString("tt",title)
        editor.apply()
        Log.e("bn",bn)
       // val model=notepad(title,bn)

      //  list.add(model)
        Log.e("list", list.toString())

    }

    fun load(): String?{


        val text = mSharedPreferences.getString("bn", "")
         return text

    }

    fun loadTitle():String?
    {
        val title= mSharedPreferences.getString("tt","Bloco de Notas")

        return title
    }




}