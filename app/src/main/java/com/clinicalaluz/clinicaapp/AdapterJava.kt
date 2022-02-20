package com.clinicalaluz.clinicaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clinicalaluz.clinicaapp.AdapterJava.ViewHolderDatos
import com.clinicalaluz.clinicaapp.clases.ClsSedes
import java.util.*

class AdapterJava(var listaPersonaje: ArrayList<ClsSedes>) :
    RecyclerView.Adapter<ViewHolderDatos>(), View.OnClickListener {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatos {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_3, parent, false)
        vista.setOnClickListener(this)
        return ViewHolderDatos(vista)
    }

    override fun onBindViewHolder(holder: ViewHolderDatos, position: Int) {
        if (holder is ViewHolderDatos) {
            //  final Dato dato=
            holder.fecha.text = listaPersonaje[position].COD_SUCURSAL
        }
    }

    override fun getItemCount(): Int {
        return listaPersonaje.size
    }

    override fun onClick(v: View) {
        if (listener != null) {
            listener!!.onClick(v)
        }
    }

    private var listener: View.OnClickListener? = null
    fun setOnClickListener(listener: View.OnClickListener?) {
        this.listener = listener
    }

    inner class ViewHolderDatos(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fecha: TextView

        init {
            fecha = itemView.findViewById<View>(R.id.nameMedic) as TextView
        }
    }
}