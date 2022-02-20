package com.clinicalaluz.clinicaapp.Adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clinicalaluz.clinicaapp.clases.Horario
import com.clinicalaluz.clinicaapp.R
import com.clinicalaluz.clinicaapp.clases.Horario2

class AdapterTimeline(private val context: Activity, private var listaHorario: ArrayList<Horario2>):
    RecyclerView.Adapter<AdapterTimeline.CustomVeiwHolder>(), View.OnClickListener {

    override fun onClick(v: View) {
        if (listener != null) {
            listener!!.onClick(v)
        }
    }

    private var listener: View.OnClickListener? = null

    fun setOnClickListener(listener: View.OnClickListener?) {
        this.listener = listener
    }

    // @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomVeiwHolder {
        val view =    LayoutInflater.from(parent.context).inflate(R.layout.item_timeline, parent, false)
        view.setOnClickListener(this)
        return CustomVeiwHolder(view)
    }


    override fun getItemCount(): Int {
        return listaHorario.size

    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: CustomVeiwHolder, position: Int) {
        //  holder.itemView.tag=listaHorario[position]
       //  val itemList = listaHorario.get(position)
        holder.tvhora.setText(listaHorario[position].HORA)
        if (position==0 ){
            holder.imgcircle.visibility=View.VISIBLE
        }

        if (position==listaHorario.count()-1){
         //   holder.imgcircle2.visibility=View.VISIBLE
        }

    }
    inner class CustomVeiwHolder(view: View) : RecyclerView.ViewHolder(view) {
         var tvhora: TextView
         var imgcircle:ImageView
        // var imgcircle2:ImageView
       //  var view =View
        init {
            tvhora=view.findViewById(R.id.tvhora2)
            imgcircle =view.findViewById(R.id.imgcircle)
          //  imgcircle2 =view.findViewById(R.id.imgcircle2)
        //    view =
        }


    }
}