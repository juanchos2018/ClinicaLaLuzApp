package com.clinicalaluz.clinicaapp.Adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.clinicalaluz.clinicaapp.Interface.InterfaceHorario
import com.clinicalaluz.clinicaapp.R
import com.clinicalaluz.clinicaapp.clases.Horario


class AdapterHorario(private val context: Activity, private var listaHorario: ArrayList<Horario>,
                     var intter: InterfaceHorario?
):
    RecyclerView.Adapter<AdapterHorario.CustomVeiwHolder>(), View.OnClickListener  {
    var row_index=0

//var itemClickListener: ItemClickListener
   // private var lastClickedPosition = -1
    private var selectedItem = 0
    var selected_position = 0
    var selectedPos = RecyclerView.NO_POSITION
   // InterfaceHorario
   lateinit var interfaceHorario: InterfaceHorario
   // var interfaceHorario: InterfaceHorario? = null


    var selectedItemPos = -1
    var lastItemSelectedPos = -1


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
        val view =    LayoutInflater.from(parent.context).inflate(R.layout.item_horarios, parent, false)
        view.setOnClickListener(this)
        return CustomVeiwHolder(view)
    }


    override fun getItemCount(): Int {
        return listaHorario.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: CustomVeiwHolder, position: Int) {
       // val itemhorario = listaHorario.get(position)
        holder.itemView.tag=listaHorario[position]
        holder.texto.text= listaHorario[position].DES_HORA

      //  if (listaHorario[position].COD_DOCUMENTO!="L"){
        ///    holder.cardView!!.setBackgroundDrawable(context.resources.getDrawable(R.drawable.btn_borde3))
          //  holder.cardView!!.isEnabled=false
     //   }
//        else{
           holder.cardView!!.setBackgroundDrawable(context.resources.getDrawable(R.drawable.btn_borde1))
//        }
        if (selectedItem == position) {
          //  holder.cardView!!.setBackgroundDrawable(context.resources.getDrawable(R.drawable.btn_borde2))
        }
        holder.itemView.setOnClickListener {
            if (listaHorario[position].COD_DOCUMENTO=="L"){
               val previousItem = selectedItem
               selectedItem = position
               notifyItemChanged(previousItem)
              //  notifyItemChanged(position)
               holder.cardView!!.setBackgroundDrawable(context.resources.getDrawable(R.drawable.btn_borde2))

              var horario =   Horario(listaHorario[position].COD_DOCUMENTO,listaHorario[position].IDE_HORA,listaHorario[position].DES_HORA,"","")
              intter?.onCallback(horario)

            }
        }

    }



    interface ItemClickListener {
        fun itemClick()
    }



    companion object {
        private const val lastClickedPosition = -1
    }
    inner class CustomVeiwHolder(view: View) : RecyclerView.ViewHolder(view) {

          var texto : TextView
          var cardView: CardView? = null
         init {
            texto=view.findViewById(R.id.tvtexto)
            cardView = view.findViewById<View>(R.id.cardhorario) as CardView
        }
    }





}