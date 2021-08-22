package com.example.blocodenotas

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.blocodenotas.model.notepad
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.save_dialog.*

class MainActivity : AppCompatActivity() {

 val dbHandler=DbHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val sa=SharedAnottation(this)
//        val title= sa.loadTitle()
//        setTitle(title)
//
//        val anotacao = sa.load()
//        et.setText(anotacao)

        fab.setOnClickListener {
            val bn = et.text.toString()
            if (bn.isNotEmpty())
            {


                et.setText("")
            }
            else
            {
                Toast.makeText(this,"Bloco vazio",Toast.LENGTH_SHORT).show()
            }
        }

        nv.setOnNavigationItemSelectedListener {

           when (it.itemId){
               R.id.action_list-> {
                   val intent= Intent(this,ListActivity::class.java)
                  startActivity(intent)
                   return@setOnNavigationItemSelectedListener true
               }

               R.id.action_save->{

                   showSaveDialog()


                   return@setOnNavigationItemSelectedListener true
               }

               else ->  return@setOnNavigationItemSelectedListener false
           }

        }

    }

    private fun showSaveDialog() {

        val dialog=Dialog(this)
        dialog.setContentView(R.layout.save_dialog)
        dialog.btn_save.setOnClickListener {



           val title=dialog.et_nome.text.toString()
            val sa=SharedAnottation(this)
            val bn = et.text.toString()
            val notepad=notepad(0,title,bn)
           // sa.save(bn,title)
            dbHandler.addNote(notepad)


            dialog.dismiss()
            Toast.makeText(this,"Salvo com sucesso",Toast.LENGTH_SHORT).show()
        }
        dialog.btn_cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()



    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.action_erase)
        {
            et.text.clear()
        }

        return super.onOptionsItemSelected(item)
    }



}