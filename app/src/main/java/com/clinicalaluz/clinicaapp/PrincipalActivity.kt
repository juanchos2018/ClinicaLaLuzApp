package com.clinicalaluz.clinicaapp

import android.R
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.ui.AppBarConfiguration
import com.clinicalaluz.clinicaapp.databinding.ActivityPrincipalBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*


class PrincipalActivity : AppCompatActivity() {

    private lateinit var  binding : ActivityPrincipalBinding
    private  lateinit var alert : AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_principal)

        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardconsulta.setOnClickListener {
            val intent = Intent(this, ServiciosActivity::class.java)
            startActivity(intent)
        }

        binding.cardpruepresion.setOnClickListener {
            val intent = Intent(this, AutochequoActivity::class.java)
            startActivity(intent)
        }
        binding.imageViewplaces.setOnClickListener {
            salir()
        }

        binding.cardmiscitas.setOnClickListener {
            val intent = Intent(this, MisCitasActivity::class.java)
            intent.putExtra("tipoEntrada", "miscitas")
            startActivity(intent)
        }


    //    val bottomNavigationView = binding.nav_view
// //     findViewById<View>(R.id.bottom_navigation) as BottomNavigationView


    //    val navView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
     //   val navView: BottomNavigationView = binding.navView
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home
//            )
//        )
//        binding.navView.setOnNavigationItemSelectedListener { item ->
//            when (item.getItemId()) {
//                binding.navView.indexOfChild(  R.id.navigation_home) -> {
//
//                }
//            }
//            false
//        }

        val navView = findViewById<BottomNavigationView>(com.clinicalaluz.clinicaapp.R.id.nav_view)
       binding.navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


//        val appBarConfiguration = AppBarConfiguration.Builder(
//            com.clinicalaluz.clinicaapp.R.id.navigation_home, com.clinicalaluz.clinicaapp.R.id.navigation_dashboard, com.clinicalaluz.clinicaapp.R.id.navigation_notifications
//        )


    }
    private fun salir() {

        val dialogBuilder = AlertDialog.Builder(this)
        val btnno: Button
        val btnsi: Button
        val v = LayoutInflater.from(applicationContext)
            .inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_salir, null)

        dialogBuilder.setView(v)
        btnno = v.findViewById(com.clinicalaluz.clinicaapp.R.id.btnno) as Button
        btnsi = v.findViewById(com.clinicalaluz.clinicaapp.R.id.btncerrarsesion) as Button

        alert= dialogBuilder.create()
        btnno.setOnClickListener {
            alert.dismiss()
        }
        btnsi.setOnClickListener {
            alert.dismiss()
            val preferences = getSharedPreferences("datosuser", MODE_PRIVATE)
            preferences.edit().remove("NUM_DOC_IDENTIDAD").commit()
            val intent = Intent(this, InicioSesionActivity::class.java)
            startActivity(intent)
            finish()
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            com.clinicalaluz.clinicaapp.R.id.reserva -> {
                val intent = Intent(this, ServiciosActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            com.clinicalaluz.clinicaapp.R.id.laboratorio -> {
                Toast.makeText(applicationContext ,"laboratorio" , Toast.LENGTH_SHORT).show()

                return@OnNavigationItemSelectedListener true
            }
            com.clinicalaluz.clinicaapp.R.id.perfil -> {

                val intent = Intent(this, MiPerfilActivity::class.java)
          //      intent.putExtra("TipoSesion","dni")
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }




}