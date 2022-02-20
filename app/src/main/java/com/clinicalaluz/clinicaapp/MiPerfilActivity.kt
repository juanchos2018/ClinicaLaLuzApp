package com.clinicalaluz.clinicaapp

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.clinicalaluz.clinicaapp.Fragment.EditFragment
import com.clinicalaluz.clinicaapp.Fragment.PerfilFragment
import com.clinicalaluz.clinicaapp.databinding.ActivityMiPerfilBinding


class MiPerfilActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMiPerfilBinding

    var tipofragment ="perfil"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMiPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(binding.fragmentContent.id, PerfilFragment())
        ft.commit()

        binding.btnabrirgragment.setOnClickListener {
            if (tipofragment=="perfil"){
                tipofragment ="edit"
                binding.tvnombretexto.text="Editar perfil"
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.replace(binding.fragmentContent.id, EditFragment())
                ft.commit()
                binding.btnabrirgragment.setImageResource(com.clinicalaluz.clinicaapp.R.drawable.ic_close)
            }else{
                tipofragment ="perfil"
                binding.tvnombretexto.text=""
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.replace(binding.fragmentContent.id, PerfilFragment())
                ft.commit()
                binding.btnabrirgragment.setImageResource(com.clinicalaluz.clinicaapp.R.drawable.ic_penedit)

            }

        }
        binding.imageViewplaces5.setOnClickListener {
            salir()
        }

    }

    private fun salir() {
        finish()
    }

}