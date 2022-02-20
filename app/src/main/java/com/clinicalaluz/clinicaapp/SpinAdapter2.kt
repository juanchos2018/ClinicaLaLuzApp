package com.clinicalaluz.clinicaapp

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.clinicalaluz.clinicaapp.clases.ClsSedes
import java.util.ArrayList

class SpinAdapter2 (   private val context: Activity, resource: Int,   private var values: ArrayList<ClsSedes>
) : ArrayAdapter<ClsSedes>(context, resource,values) {
    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): ClsSedes {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val label = super.getView(position, convertView, parent) as TextView
        label.setTextColor(Color.BLACK)
        label.text = values.toTypedArray<Any>()[position]
            .toString()
        return label
    }
//
//    override fun getDropDownView(
//        position: Int, convertView: View,
//        parent: ViewGroup
//    ): View {
//        val label = super.getDropDownView(position, convertView, parent) as TextView
//        label.setTextColor(Color.BLACK)
//        label.text = values.toTypedArray<Any>()[position]
//            .toString()
//        return label
//    }

}