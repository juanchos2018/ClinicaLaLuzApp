package com.clinicalaluz.clinicaapp.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.clinicalaluz.clinicaapp.R
import com.clinicalaluz.clinicaapp.clases.ClsChequeo


class AdapterChequeo (private var context: Activity, al_menu: ArrayList<ClsChequeo>) :
    ArrayAdapter<ClsChequeo?>(context, R.layout.item_chequeo, al_menu as List<ClsChequeo?>) {

    var viewHolder: ViewHolder? = null
    var al_menu = ArrayList<ClsChequeo>()
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
            convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_chequeo, parent, false)
            viewHolder!!.tv_DES_ESPECIALIDAD =
                convertView.findViewById<View>(R.id.tvnombrechequeo) as TextView
            viewHolder!!.img = convertView.findViewById<View>(R.id.imageView2) as ImageView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        viewHolder!!.tv_DES_ESPECIALIDAD!!.text = al_menu[position].NOMBRE
        return convertView!!
    }


    class ViewHolder {
        var img: ImageView? = null
        var tv_DES_ESPECIALIDAD: TextView? = null
    }

    init {
        this.al_menu = al_menu
    }
}