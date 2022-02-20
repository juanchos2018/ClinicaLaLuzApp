package com.clinicalaluz.clinicaapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.Adapters.AdapterMisCitas
import com.clinicalaluz.clinicaapp.Fragment.DialogoFragment.Companion.newInstance
import com.clinicalaluz.clinicaapp.clases.ClsMisCitas
import com.clinicalaluz.clinicaapp.databinding.ActivityMisCitasBinding


class MisCitasActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMisCitasBinding
    val listaMisCitas = ArrayList<ClsMisCitas>()
    private  lateinit var  adapter: AdapterMisCitas


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ///etContentView(R.layout.activity_mis_citas)
        binding = ActivityMisCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val linearLayoutManager2 = LinearLayoutManager(this)

        linearLayoutManager2.reverseLayout = true
        linearLayoutManager2.stackFromEnd = true
        binding.recyclermicictas.layoutManager = linearLayoutManager2

        binding.imageViewplaces6.setOnClickListener {
            salir()
        }


    }

    private fun salir() {
        finish()
    }

    override fun onStart() {
        super.onStart()
       val preferences = getSharedPreferences("datosuser", Context.MODE_PRIVATE)
       var  DES_AUXILIAR = preferences.getString("DES_AUXILIAR", null).toString()
       var  COD_AUXILIAR = preferences.getString("COD_AUXILIAR", null).toString()
       var  NUM_DOC_IDENTIDAD = preferences.getString("NUM_DOC_IDENTIDAD", null).toString()
       var  COD_PACIENTE= preferences.getString("COD_PACIENTE", null).toString()
       var  COD_CLIENTE = preferences.getString("COD_CLIENTE", null).toString()
        showMisCitas(COD_PACIENTE)
    }


    fun showMisCitas(cod_paciente:String){
        listaMisCitas.clear()
        var peticion = "/pdoMisCitas.php?cod_paciente=$cod_paciente"

        val rq = Volley.newRequestQueue(this)
        val arr = JsonArrayRequest(
            Request.Method.GET, getString(R.string.URL_BASE)+peticion, null,
            { response ->
                for(x in 0..response.length()-1){
                    val COD_ATENCION = response.getJSONObject(x).getString("COD_ATENCION")
                    val COD_MEDICO = response.getJSONObject(x).getString("COD_MEDICO")
                    val DES_AUXILIAR = response.getJSONObject(x).getString("DES_AUXILIAR")
                    val COD_ESPECIALIDAD = response.getJSONObject(x).getString("COD_ESPECIALIDAD")

                    val DES_ESPECIALIDAD = response.getJSONObject(x).getString("DES_ESPECIALIDAD")
                    val FEC_INSERCION = response.getJSONObject(x).getString("FEC_INSERCION")
                    val FEC_ATENCION = response.getJSONObject(x).getString("FEC_ATENCION")

                    listaMisCitas.add(ClsMisCitas(COD_ATENCION,COD_MEDICO,DES_AUXILIAR,COD_ESPECIALIDAD,DES_ESPECIALIDAD,FEC_INSERCION,FEC_ATENCION ))
                }
                adapter = AdapterMisCitas(this,listaMisCitas)
                binding.recyclermicictas.adapter=adapter
                adapter.setOnClickListener { v: View? ->

                    val bottomSheetDialog = newInstance()
                    bottomSheetDialog?.show(supportFragmentManager,"dfdfd")
                   // bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");

                }


                ///    binding.listview.adapter = Adapter(this, list)

            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {

                   // binding.simpleProgressBar2.setVisibility(View.GONE)
                    Log.e("errror",volleyError.toString() )
                    Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
        rq.add(arr)
    }
}