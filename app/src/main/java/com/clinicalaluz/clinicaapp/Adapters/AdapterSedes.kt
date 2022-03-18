package com.clinicalaluz.clinicaapp.Adapters

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.clinicalaluz.clinicaapp.EspecialidadActivity
import com.clinicalaluz.clinicaapp.R
import com.clinicalaluz.clinicaapp.UbicacionActivity
import com.clinicalaluz.clinicaapp.clases.ClsSedes
import com.squareup.picasso.Picasso

class AdapterSedes (private val context: Activity, private var itemList: ArrayList<ClsSedes>):RecyclerView.Adapter<AdapterSedes.CustomVeiwHolder>(), View.OnClickListener{



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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_3,parent,false)
        view.setOnClickListener(this)
        return CustomVeiwHolder(view)
    }


    override fun getItemCount(): Int {
        return   itemList.size

    }

    override fun onBindViewHolder(holder: CustomVeiwHolder, position: Int) {
        val itemList=itemList.get(position)
        holder.tvnombreSede.setText(itemList.NOM_SUCURSAL)
        holder.tvdireccion.setText(itemList.direccion)
        holder.tvtelefono.setText(itemList.telefono)

        Picasso.with(context).load(itemList.img).placeholder(R.drawable.preview).into(holder.img)

        holder.tvnombreSede.setOnClickListener {
            redirect(itemList.COD_SUCURSAL,itemList.telefono,itemList.NOM_SUCURSAL)
        }
        holder.tvdireccion.setOnClickListener {
            redirect(itemList.COD_SUCURSAL,itemList.telefono,itemList.NOM_SUCURSAL)
        }
        holder.img.setOnClickListener {
            redirect(itemList.COD_SUCURSAL,itemList.telefono,itemList.NOM_SUCURSAL)
        }
        holder.imglocation.setOnClickListener {
            redirectMap(itemList.COD_SUCURSAL,itemList.telefono,itemList.DIRECMAP)
        }

       // val picasso = Picasso.get()
//        holder.itemView.setOnClickListener {
//            val intent = Intent(context, DetailActivity::class.java)
//            intent.putExtra("nameMedic", itemList)
//            context.startActivity(intent)
//        }
    }

    private  fun redirect(COD_SUCURSAL:String,phone:String,NOM_SUCURSAL:String){

//        Toast.makeText(context, COD_SUCURSAL, Toast.LENGTH_SHORT)
//            .show()

       val intent = Intent(context, EspecialidadActivity::class.java)
       intent.putExtra("COD_SUCURSAL", COD_SUCURSAL)
       intent.putExtra("phone",phone )
       intent.putExtra("NOM_SUCURSAL", NOM_SUCURSAL)
        var datos=COD_SUCURSAL+"-"+NOM_SUCURSAL
        Log.e("datos",datos)
       context.startActivity(intent)

    }
    private  fun redirectMap(COD_SUCURSAL:String,phone:String,direcMapa:String){
//        val intent = Intent(context, UbicacionActivity::class.java)
//        context.startActivity(intent)
//
//        val gmmIntentUri = Uri.parse("google.streetview:cbll=-18.0226834,-70.2612516")
//        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//        mapIntent.setPackage("com.google.android.apps.maps")
//        context. startActivity(mapIntent)

        val gmmIntentUri = Uri.parse("geo:-18.0226834,-70.2612516?q=$direcMapa")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
    }

    public fun filtrar(filtro: ArrayList<ClsSedes>) {
        this.itemList = filtro
        notifyDataSetChanged()
    }

    inner class CustomVeiwHolder(view: View): RecyclerView.ViewHolder(view){
        var tvnombreSede: TextView
        lateinit var tvdireccion: TextView
        lateinit var tvtelefono: TextView
        lateinit var img: ImageView
         var imglocation: ImageView
        init {
            tvnombreSede=view.findViewById(R.id.tvnombresede)
            tvdireccion=view.findViewById(R.id.tvdireccion)
            tvtelefono=view.findViewById(R.id.tvtelefonoclinica)
            img=view.findViewById(R.id.imgsede)
            imglocation=view.findViewById(R.id.imglocation)
        }
    }
}