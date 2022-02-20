package com.clinicalaluz.clinicaapp.Adapters


import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clinicalaluz.clinicaapp.clases.Medic
import com.clinicalaluz.clinicaapp.R
import com.squareup.picasso.Picasso


class AdapterMedicos(private val context: Activity, private var listaMedic: ArrayList<Medic>):RecyclerView.Adapter<AdapterMedicos.CustomVeiwHolder>(), View.OnClickListener{

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medicos2,parent,false)
        view.setOnClickListener(this)
        return CustomVeiwHolder(view)
    }


    override fun getItemCount(): Int {
        return   listaMedic.size
    }

    override fun onBindViewHolder(holder: CustomVeiwHolder, position: Int) {
        val itemList=listaMedic.get(position)
        holder.tvnombreMedico.setText(itemList.nameMedic)
        holder.tvnombreEspecialidad.setText(itemList.nameSpecialty)
        holder.proxima.text=itemList.ULTIMA
        if (itemList.IMAGEN=="default"){
            holder!!.IMG.setImageResource(R.drawable.default_profile_image)
        }else{
          Picasso.with(context)
            .load(context.getString(R.string.URL_PAGINA)+ itemList.IMAGEN)
            .into(holder!!.IMG)
        }
    }

    inner class CustomVeiwHolder(view: View): RecyclerView.ViewHolder(view){
        lateinit var tvnombreMedico: TextView
        lateinit var tvnombreEspecialidad: TextView
        lateinit var IMG: ImageView
        var proxima :TextView

        init {
            tvnombreMedico=view.findViewById(R.id.tvnombredoctor2)
            tvnombreEspecialidad=view.findViewById(R.id.tvenombreespecialidad)
            IMG=view.findViewById(R.id.imgdoctor2)
            proxima=view.findViewById(R.id.tvproxima)

        }
        // inflater.findViewById<TextView>(R.id.nameMedic).text = medico.nameMedic
        //        inflater.findViewById<TextView>(R.id.nameSpecialty).text = medico.nameSpecialty

    }


}