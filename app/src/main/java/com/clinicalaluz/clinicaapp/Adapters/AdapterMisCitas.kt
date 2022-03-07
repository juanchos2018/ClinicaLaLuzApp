package com.clinicalaluz.clinicaapp.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clinicalaluz.clinicaapp.R
import com.clinicalaluz.clinicaapp.clases.ClsMisCitas
import com.clinicalaluz.clinicaapp.clases.Medic
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class AdapterMisCitas (private val context: Activity, private var listaMisCitas: ArrayList<ClsMisCitas>):
    RecyclerView.Adapter<AdapterMisCitas.CustomVeiwHolder>(), View.OnClickListener{

    override fun onClick(v: View) {
        if (listener != null) {
            listener!!.onClick(v)
        }
    }

    var DES_AUXILIAR=""
    private var listener: View.OnClickListener? = null

    fun setOnClickListener(listener: View.OnClickListener?) {
        this.listener = listener
    }
    // @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomVeiwHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_miscitas,parent,false)
        view.setOnClickListener(this)
        return CustomVeiwHolder(view)
    }

    override fun getItemCount(): Int {
        return   listaMisCitas.size
    }

    override fun onBindViewHolder(holder: CustomVeiwHolder, position: Int) {
        val itemList=listaMisCitas.get(position)
        var sucurlsal=""
        holder.tvespecialidad.setText(itemList.DES_ESPECIALIDAD)
        holder.tvmedico.setText(itemList.DES_AUXILIAR)
        holder.tvminonbre.text=DES_AUXILIAR
        var dia = itemList.FEC_ATENCION.split('-')
        val fromServer = SimpleDateFormat("yyyy-MM-dd")
        val date = fromServer.parse(itemList.FEC_ATENCION)

        val nombremes = SimpleDateFormat("MMMM")
        val mess = nombremes.format(date)
        holder.tvfechascita3.text=dia[2]
        holder.tvnombremes.text=mess.substring(0,3)
        holder.tvhora.text=itemList.DES_HORA
        holder.tvtsede.text=itemList.NOM_SUCURSAL
    }

    inner class CustomVeiwHolder(view: View): RecyclerView.ViewHolder(view) {
        lateinit var tvespecialidad: TextView
        lateinit var tvmedico: TextView
        var tvfechascita3: TextView
        var tvnombremes: TextView
        var tvminonbre:TextView
        var tvhora :TextView
        var tvtsede:TextView

        init {
            tvespecialidad = view.findViewById(R.id.tvespecialidad)
            tvmedico = view.findViewById(R.id.tvmedico)
            tvfechascita3 = view.findViewById(R.id.tvfechascita3)
            tvnombremes = view.findViewById(R.id.tvnombremes)
            tvminonbre =view.findViewById(R.id.tvminombre)
            tvhora=view.findViewById(R.id.tvhoracita2)
            tvtsede=view.findViewById(R.id.tvtsede)
        }

    }
}