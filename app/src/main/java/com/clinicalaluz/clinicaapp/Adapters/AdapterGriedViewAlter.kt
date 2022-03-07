package com.clinicalaluz.clinicaapp.Adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.clinicalaluz.clinicaapp.DoctoresActivity
import com.clinicalaluz.clinicaapp.clases.ClsEspecialidad
import com.squareup.picasso.Picasso


//class AdapterGriedViewAlter {
//}
class AdapterGriedViewAlter(private val context: Activity, private var listaMedic: ArrayList<ClsEspecialidad>):
    RecyclerView.Adapter<AdapterGriedViewAlter.CustomVeiwHolder>(), View.OnClickListener{

    override fun onClick(v: View) {
        if (listener != null) {
            listener!!.onClick(v)
        }
    }

    private var listener: View.OnClickListener? = null
    var NOM_SUCURSAL =""
    var COD_SUCURSAL=""


    fun setOnClickListener(listener: View.OnClickListener?) {
        this.listener = listener
    }
    // @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomVeiwHolder {
        val view = LayoutInflater.from(parent.context).inflate(com.clinicalaluz.clinicaapp.R.layout.item_especialiades,parent,false)
        view.setOnClickListener(this)
        return CustomVeiwHolder(view)
    }


    override fun getItemCount(): Int {
        return   listaMedic.size
    }
    public fun filtrar(filtro: ArrayList<ClsEspecialidad>) {
        this.listaMedic = filtro
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: CustomVeiwHolder, position: Int) {
        val itemList=listaMedic.get(position)
        holder.tvespecialidad.setText(itemList.DES_ESPECIALIDAD)
        holder.COD_ESPECIALIDAD=itemList.COD_ESPECIALIDAD
        holder.PRECIO_V= itemList.PRECIO_V
        if (itemList.IMAGEN=="default"){
            holder!!.img.setImageResource(com.clinicalaluz.clinicaapp.R.drawable.default_profile_image)
        }else{
            Picasso.with(context)
                .load( itemList.IMAGEN)
                .into(holder!!.img)
        }
    }

    inner class CustomVeiwHolder(view: View): RecyclerView.ViewHolder(view){
        lateinit var tvespecialidad: TextView
        lateinit var img: ImageView
        var  COD_ESPECIALIDAD =""
        var  PRECIO_V =""
        init {
            tvespecialidad=view.findViewById(com.clinicalaluz.clinicaapp.R.id.tvespecialidad)
            img=view.findViewById(com.clinicalaluz.clinicaapp.R.id.imageView2)
            itemView.setOnClickListener { v ->
                val intent = Intent(context, DoctoresActivity::class.java)
                intent.putExtra("especialidad", tvespecialidad.text)
                intent.putExtra("COD_ESPECIALIDAD", COD_ESPECIALIDAD)
                intent.putExtra("PRECIO_V", PRECIO_V)
                intent.putExtra("NOM_SUCURSAL", NOM_SUCURSAL)
                intent.putExtra("COD_SUCURSAL", COD_SUCURSAL)
                context.startActivity(intent)
            }
        }
    }
}