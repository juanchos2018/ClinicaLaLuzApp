package com.clinicalaluz.clinicaapp

import android.app.Activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class AdapterGridView (private var context: Activity, al_menu: ArrayList<Medic>) :
    ArrayAdapter<Medic?>(context, R.layout.item_medicos, al_menu as List<Medic?>) {

    var viewHolder: ViewHolder? = null
    var al_menu = ArrayList<Medic>()
    override fun getCount(): Int {
        return al_menu.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getViewTypeCount(): Int {
        return if (al_menu.size > 0) {
            al_menu.size
        } else {
            1
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView =      LayoutInflater.from(getContext()).inflate(R.layout.item_medicos, parent, false)
            viewHolder!!.tv_name = convertView.findViewById<View>(R.id.textView2) as TextView
            viewHolder!!.tvespecialidad = convertView.findViewById<View>(R.id.tvespecilidada) as TextView
            viewHolder!!.img = convertView.findViewById<View>(R.id.imageView2) as ImageView //

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        Picasso.with(context)
            .load(al_menu[position].imgMedic)
            .into(viewHolder!!.img)

        viewHolder!!.tv_name!!.text = al_menu[position].nameMedic
        viewHolder!!.tvespecialidad!!.text = al_menu[position].nameSpecialty
        return convertView!!
    }


    public fun filtrar(filtro: ArrayList<Medic>) {
        this.al_menu = filtro
        notifyDataSetChanged()
    }


    class ViewHolder {
        var img: ImageView? = null
        var tv_name: TextView? = null
        var tvespecialidad:TextView?=null
    }

    init {
        this.al_menu = al_menu
        //  this.context = context
    }

}