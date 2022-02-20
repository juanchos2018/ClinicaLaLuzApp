package com.clinicalaluz.clinicaapp.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.clinicalaluz.clinicaapp.R
import com.clinicalaluz.clinicaapp.clases.Horario

class HorarioAdapter(private val context: Activity,private val listHorario: ArrayList<Horario>) :
    ArrayAdapter<Horario>(context, R.layout.list_horario, listHorario){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context).inflate(R.layout.list_horario, null)

        val horario = listHorario[position]
        inflater.findViewById<TextView>(R.id.day).text = horario.COD_DOCUMENTO
        inflater.findViewById<TextView>(R.id.dateComplete).text = horario.DES_HORA
        inflater.findViewById<TextView>(R.id.start).text = horario.IDE_HORA
        inflater.findViewById<TextView>(R.id.end).text = horario.DES_HORA
        return inflater
    }
}