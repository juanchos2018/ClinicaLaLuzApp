package com.clinicalaluz.clinicaapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.clinicalaluz.clinicaapp.Adapters.Adapter.MyViewHolder
import com.clinicalaluz.clinicaapp.R
import com.clinicalaluz.clinicaapp.clases.ClsSedes

class Adapter(private val context: Context, private val petsList: List<ClsSedes>) :  RecyclerView.Adapter<MyViewHolder>() {


    private var selectedItem = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_3, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.tag = petsList[position]
        holder.name.text = petsList[position].direccion
        holder.cardView!!.setCardBackgroundColor(context.resources.getColor(R.color.com_facebook_blue))
        if (selectedItem == position) {
            holder.cardView!!.setCardBackgroundColor(context.resources.getColor(R.color.com_facebook_blue))
        }
        holder.itemView.setOnClickListener {
            val previousItem = selectedItem
            selectedItem = position
            notifyItemChanged(previousItem)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return petsList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var cardView: CardView? = null

        init {
            name = view.findViewById<View>(R.id.tvdireccion) as TextView
        }
    }

    companion object {
        private const val lastClickedPosition = -1
    }
}