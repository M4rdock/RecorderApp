package com.rkoma.recorderapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date

class Helper(var recorderApp: ArrayList<RecorderApp>, var listener: ClickListener) : RecyclerView.Adapter<Helper.ViewHolder>() {

    private var editMode = false

    fun isEditMode(): Boolean { return editMode }
    fun setEditMode(mode : Boolean){
        if(editMode != mode){
            editMode = mode
            notifyDataSetChanged()

        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        var recordingname : TextView = itemView.findViewById(R.id.recordingName)
        var metadati : TextView = itemView.findViewById(R.id.metaDati)
        var checkBox : CheckBox= itemView.findViewById(R.id.checkBox)


        init{
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION)
                listener.clicklistener(position)
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION)
                listener.longclicklistener(position)
            return true
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item_activity, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recorderApp.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       if(position != RecyclerView.NO_POSITION){
           val recorderApp  =recorderApp[position]

           val formato = SimpleDateFormat("dd/MM/yyyy")
           val data = Date(recorderApp.timestamp)
           val datatext = formato.format(data)

           holder.recordingname.text = recorderApp.filename
           holder.metadati.text = "${recorderApp.duration} $datatext"

           if(editMode){
               holder.checkBox.visibility = View.VISIBLE
               holder.checkBox.isChecked = recorderApp.isChecked
           }else{
               holder.checkBox.visibility = View.GONE
               holder.checkBox.isChecked = false
           }

       }
    }



}