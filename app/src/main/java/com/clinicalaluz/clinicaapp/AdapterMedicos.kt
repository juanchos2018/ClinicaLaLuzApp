package com.clinicalaluz.clinicaapp


import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class AdapterMedicos(private val context: Activity, private var itemList: ArrayList<Medic>):RecyclerView.Adapter<AdapterMedicos.CustomVeiwHolder>() {


    interface OnMedicClickListener{
        fun onitemClick()
    }

   // @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomVeiwHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)

        return CustomVeiwHolder(view)
    }


    override fun getItemCount(): Int {
      return   itemList.size

    }

    override fun onBindViewHolder(holder: CustomVeiwHolder, position: Int) {
    val itemList=itemList.get(position)
        holder.tvnombre.setText(itemList.nameMedic)
  ///      holder.tvnombre.setText(itemList.get(position).nameMedic)
        holder.tvespecialidad.setText(itemList.nameSpecialty)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("nameMedic", itemList)
            context.startActivity(intent)
        }
        // holder.tvespecialidad.setText(itemList.get(position).nameSpecialty)
    }


    inner class CustomVeiwHolder(view: View):RecyclerView.ViewHolder(view){
         lateinit var tvnombre:TextView
         lateinit var tvespecialidad:TextView

         init {
             tvnombre=view.findViewById(R.id.nameMedic)
             tvespecialidad=view.findViewById(R.id.nameSpecialty)

         }
        // inflater.findViewById<TextView>(R.id.nameMedic).text = medico.nameMedic
        //        inflater.findViewById<TextView>(R.id.nameSpecialty).text = medico.nameSpecialty

    }

    public fun filtrar(filtro: ArrayList<Medic>) {
        this.itemList = filtro
        notifyDataSetChanged()
    }
}