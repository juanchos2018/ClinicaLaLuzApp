package com.clinicalaluz.clinicaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.clinicalaluz.clinicaapp.Adapters.AdapterChequeo
import com.clinicalaluz.clinicaapp.Adapters.AdapterGridEspecialidad
import com.clinicalaluz.clinicaapp.clases.ClsChequeo
import com.clinicalaluz.clinicaapp.clases.ClsEspecialidad
import com.clinicalaluz.clinicaapp.databinding.ActivityAutochequoBinding
import com.clinicalaluz.clinicaapp.databinding.ActivityDetailBinding

class AutochequoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAutochequoBinding
    val listChequeo= ArrayList<ClsChequeo>()
    private  lateinit var adapterchequeo : AdapterChequeo



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_autochequo)
        binding = ActivityAutochequoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageViewplaces3.setOnClickListener {
            salir()
        }
    }

    private fun salir() {
        finish()
    }
    override fun onStart() {
        super.onStart()
        listChequeo.clear()
        listChequeo.add(ClsChequeo("1","Presion"))
        listChequeo.add(ClsChequeo("2","Azucar"))
        adapterchequeo= AdapterChequeo(this,listChequeo)
        binding.grideschequeos.adapter =adapterchequeo

        binding.grideschequeos.setOnItemClickListener { parent, view, position, id ->
            ///    Toast.makeText(applicationContext, "id : ,"+idbd, Toast.LENGTH_LONG).show()
            if (listChequeo[position].NOMBRE=="Presion"){
                val intent = Intent(this, PresionActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this, GlucosaActivity::class.java)
                startActivity(intent)
            }

        }



    }
}