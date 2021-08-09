package com.clinicalaluz.clinicaapp

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class HorarioAdapter(private val context: Activity,private val listHorario: ArrayList<Horario>) :
    ArrayAdapter<Horario>(context, R.layout.list_horario, listHorario){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context).inflate(R.layout.list_horario, null)

        val horario = listHorario[position]
        inflater.findViewById<TextView>(R.id.day).text = horario.day
        inflater.findViewById<TextView>(R.id.dateComplete).text = horario.date
        inflater.findViewById<TextView>(R.id.start).text = horario.start
        inflater.findViewById<TextView>(R.id.end).text = horario.end
        return inflater
    }
}