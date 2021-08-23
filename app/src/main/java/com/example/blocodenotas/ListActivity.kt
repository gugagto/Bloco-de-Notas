package com.example.blocodenotas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.blocodenotas.adapter.BnAdapter
import com.example.blocodenotas.model.notepad
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    val dbHandler=DbHandler(this)
    var undoModel:notepad ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

      

        loadAll()

        fab_add.setOnClickListener {

            val intent=Intent(this,NewNoteActivity::class.java)
            startActivity(intent)


        }


    }

    override fun onResume() {
        super.onResume()

        loadAll()


    }


    fun loadAll() {

        val dbHandler = DbHandler(this)
        val list: ArrayList<notepad> = dbHandler.getAll()

        rv.layoutManager = StaggeredGridLayoutManager( 2,StaggeredGridLayoutManager.VERTICAL)
            rv.setHasFixedSize(true)
        val adapter = BnAdapter(this, list)
        rv.adapter = adapter
        adapter.setOnClickListener(object : BnAdapter.OnClickListener {
            override fun onClick(position: Int, model: notepad) {
                val intent = Intent(this@ListActivity, DetalhesActivity::class.java)
                intent.putExtra("np", model)
                startActivity(intent)
            }

            override fun onDelete(position: Int, np: notepad) {

                    undoModel=np
                AlertDialog.Builder(this@ListActivity)
                    .setMessage("Deseja Remover a nota?")
                    .setPositiveButton("Sim"){dialog,wich->

                        dbHandler.delete(np)
                        Snackbar.make(cv_list,"Anotação Removida com sucesso",Snackbar.LENGTH_SHORT)
                            .setAction("DESFAZER",View.OnClickListener {
                                dbHandler.addNote(undoModel!!)
                                loadAll()
                            })

                            .show()
                        loadAll()

                    }
                    .setNegativeButton("Cancelar",null)

                    .show()
            }
        })


        rv.addOnScrollListener(object: RecyclerView.OnScrollListener() {


            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                fab_add.hide()
                btn_top.visibility=View.INVISIBLE

                if (!rv.canScrollVertically(1)&& dy>0)
                {
                    btn_top.visibility=View.VISIBLE
                }
                else
                {
                    btn_top.visibility=View.INVISIBLE
                }


            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
              fab_add.show()

            }

            override fun equals(other: Any?): Boolean {
                return super.equals(other)



            }


        })



        btn_top.setOnClickListener {
            rv.scrollToPosition(0)
            btn_top.visibility=View.INVISIBLE
        }


    }






}