package com.clinicalaluz.clinicaapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView.OnDateChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.Adapters.AdapterMedicos
import com.clinicalaluz.clinicaapp.clases.Medic
import com.clinicalaluz.clinicaapp.databinding.ActivityDoctoresBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DoctoresActivity : AppCompatActivity() {


    private lateinit var binding : ActivityDoctoresBinding
    private  var COD_SUCURSAL =""
    private  var especialidad=""
    val listaMedicos = ArrayList<Medic>()
    private  lateinit var  adapter: AdapterMedicos
    var FECHA=""
    var FECHA2=""

    var FechaActual=""
    var FECHABD=""
    var NOM_SUCURSAL=""
    var COD_ESPECIALIDAD=""
    var PRECIO_V=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_doctores)
        binding = ActivityDoctoresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dateTimeDefect()

        COD_SUCURSAL = intent.getSerializableExtra("COD_SUCURSAL").toString()
        NOM_SUCURSAL = intent.getSerializableExtra("NOM_SUCURSAL").toString()
        especialidad = intent.getSerializableExtra("especialidad").toString()
        COD_ESPECIALIDAD = intent.getSerializableExtra("COD_ESPECIALIDAD").toString()
        PRECIO_V = intent.getSerializableExtra("PRECIO_V").toString()


        val linearLayoutManager2 = LinearLayoutManager(this)

        linearLayoutManager2.reverseLayout = true
        linearLayoutManager2.stackFromEnd = true
        binding.recylcerMedicos.layoutManager = linearLayoutManager2
        binding.imageViewplaces3.setOnClickListener {
            salir()
        }
        binding.tvespecialidad1.text = especialidad


        binding.calendarView.setOnDateChangeListener(OnDateChangeListener { view, year, month, day ->
            var mes = month + 1
            FECHA = "$day/$mes/$year"
            FECHABD= String.format("$year-${month+1}-$day")
            FECHA2 = "$year-$mes-$day"

            val inputDateStr = FECHA2
            try {
                val fromServer = SimpleDateFormat("yyyy-MM-dd")
                val date = fromServer.parse(inputDateStr)
                val nombredia = SimpleDateFormat("EEEE")
                val nombremes = SimpleDateFormat("MMMM")
                val diactual = nombredia.format(date)
                val mess = nombremes.format(date)

                FechaActual=diactual+" "+day +" de " +mess
                binding.tvnombredia.text = FechaActual

                showMedics(FECHABD)


            } catch (e: ParseException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
        })
        binding.calendarView.setMinDate(System.currentTimeMillis())

        showMedics(FECHABD)


    }
    private fun dateTimeDefect(){
        val c = Calendar.getInstance()
        val yyyy = c.get(Calendar.YEAR)
        val MM = c.get(Calendar.MONTH)
        val dd = c.get(Calendar.DAY_OF_MONTH)

        var fechacurrent =String.format("$yyyy-${MM+1}-$dd")

        val fromServer = SimpleDateFormat("yyyy-MM-dd")
        val date = fromServer.parse(fechacurrent)
        val nombredia = SimpleDateFormat("EEEE")
        val nombremes = SimpleDateFormat("MMMM")

        val diactual = nombredia.format(date)
        val mess = nombremes.format(date)
        FechaActual =diactual+" "+ dd +" de "+ mess
        binding.tvnombredia.text = FechaActual
        FECHABD=String.format("$yyyy-${MM+1}-$dd")

    }
    private fun salir() {
        finish()
    }

    fun showMedics(fecha:String){
        listaMedicos.clear()
        var peticion = "/pdoSelect.php?especialidad=$especialidad&COD_SUCURSAL=$COD_SUCURSAL&fecha=$fecha"
        binding.simpleProgressBar2.setVisibility(View.VISIBLE);
        val rq = Volley.newRequestQueue(this)
        val arr = JsonArrayRequest(
            Request.Method.GET, getString(R.string.URL_BASE)+peticion, null,
            { response ->
                for(x in 0..response.length()-1){
                    val COD_AUXILIAR = response.getJSONObject(x).getString("COD_AUXILIAR")
                    val name = response.getJSONObject(x).getString("DES_AUXILIAR")
                    val especialidad = response.getJSONObject(x).getString("DES_ESPECIALIDAD")
                    val IMAGEN =response.getJSONObject(x).getString("IMAGEN")
                    val COD_MEDICO =response.getJSONObject(x).getString("COD_MEDICO")
                    val ULTIMA =response.getJSONObject(x).getString("ULTIMA")
                    listaMedicos.add(Medic(COD_AUXILIAR, name, especialidad,IMAGEN,COD_MEDICO,"Proxima Cita $ULTIMA"))
                }
                adapter = AdapterMedicos(this,listaMedicos)
                binding.recylcerMedicos.adapter=adapter
                adapter.setOnClickListener { v: View? ->

                    val intent = Intent(this, DoctorDetalleActivity::class.java)
                    intent.putExtra("especialidad", especialidad)
                    intent.putExtra("COD_ESPECIALIDAD", COD_ESPECIALIDAD)
                    intent.putExtra("PRECIO_V", PRECIO_V)
                    intent.putExtra("COD_AUXILIAR", listaMedicos.get(binding.recylcerMedicos.getChildAdapterPosition(v!!)).codMedic)
                    intent.putExtra("IMAGEN", listaMedicos.get(binding.recylcerMedicos.getChildAdapterPosition(v!!)).IMAGEN)
                    intent.putExtra("FECHA", FechaActual)
                    intent.putExtra("FECHABD", FECHABD)
                    intent.putExtra("NOM_SUCURSAL", NOM_SUCURSAL)
                    intent.putExtra("COD_SUCURSAL", COD_SUCURSAL)
                    intent.putExtra("DES_AUXILIAR", listaMedicos.get(binding.recylcerMedicos.getChildAdapterPosition(v!!)).nameMedic)
                    intent.putExtra("COD_MEDICO", listaMedicos.get(binding.recylcerMedicos.getChildAdapterPosition(v!!)).COD_MEDICO)
                    startActivity(intent)

                }
                binding.simpleProgressBar2.setVisibility(View.GONE)

                if (listaMedicos.size>0){
                   binding.lienarmensaje.setVisibility(View.GONE);
                }else{
                   binding.lienarmensaje.setVisibility(View.VISIBLE);
                }
                ///    binding.listview.adapter = Adapter(this, list)

            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {

                    binding.simpleProgressBar2.setVisibility(View.GONE)
                    Log.e("errror",volleyError.toString() )
                    Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
        rq.add(arr)
    }
}