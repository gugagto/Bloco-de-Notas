package com.example.blocodenotas.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blocodenotas.DbHandler
import com.example.blocodenotas.R
import com.example.blocodenotas.model.notepad
import kotlinx.android.synthetic.main.activity_list.view.*
import kotlinx.android.synthetic.main.item_row.view.*

class BnAdapter(private val context: Context, private val list: ArrayList<notepad>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener?=null
    val dbHandler=DbHandler(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_row,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val model= list[position]
        if (holder is MyViewHolder)
        {

            if(model.image.isNotEmpty())
            {
                holder.itemView.image.visibility=View.VISIBLE

            }
            else{
                holder.itemView.image.visibility=View.GONE

            }

            holder.itemView.tv_title_item.text= model.title
           holder.itemView.tv_note.text= model.note
            holder.itemView.tv_date_item.text=model.date

            holder.itemView.setOnClickListener {

                if (onClickListener!=null)
                {
                    onClickListener!!.onClick(position,model)

                }
            }

            holder.itemView.setOnLongClickListener {

                if (onClickListener!=null)
                {
                    onClickListener!!.onDelete(position,model)
                }

                true

            }


        }

    }

    override fun getItemCount(): Int {
        return list.size
    }



    class MyViewHolder(view: View):RecyclerView.ViewHolder(view)
    {

    }

    interface OnClickListener{

        fun onClick(position: Int, np:notepad)

        fun onDelete(position: Int,np: notepad)
    }

    fun setOnClickListener(onClickListener: OnClickListener)
    {
        this.onClickListener=onClickListener
    }

}