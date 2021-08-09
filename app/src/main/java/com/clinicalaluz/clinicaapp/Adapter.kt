package com.clinicalaluz.clinicaapp

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class Adapter(private val context: Activity,private var listAdapter: ArrayList<Medic>) :


    ArrayAdapter<Medic>(context, R.layout.list_item, listAdapter){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context).inflate(R.layout.list_item, null)

        val medico = listAdapter[position]
        inflater.findViewById<TextView>(R.id.nameMedic).text = medico.nameMedic
        inflater.findViewById<TextView>(R.id.nameSpecialty).text = medico.nameSpecialty
        return inflater
    }

    public fun filtrar(filtro: ArrayList<Medic>) {
        this.listAdapter = filtro
        notifyDataSetChanged()
    }

}