package com.clinicalaluz.clinicaapp

import android.R.attr.password
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.view.View
import android.view.animation.ScaleAnimation
import android.widget.RadioButton
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.Adapters.AdapterHorario
import com.clinicalaluz.clinicaapp.Adapters.AdapterTimeline
import com.clinicalaluz.clinicaapp.Interface.InterfaceHorario
import com.clinicalaluz.clinicaapp.clases.Horario
import com.clinicalaluz.clinicaapp.clases.Horario2
import com.clinicalaluz.clinicaapp.databinding.ActivityDoctorDetalleBinding
import com.squareup.picasso.Picasso
import com.tapadoo.alerter.Alerter
import org.json.JSONException
import org.json.JSONObject


class DoctorDetalleActivity : AppCompatActivity() {

    private lateinit var  binding : ActivityDoctorDetalleBinding

    var numBotones = 20
    var shrinkAnimation: ScaleAnimation? = null
    private var layoutManager: StaggeredGridLayoutManager? = null
    lateinit var adapterhoraio: AdapterHorario
    lateinit var adaptertimeline: AdapterTimeline
    private var ESPECIALIDAD=""
    var IMAGEN=""
    var FECHA=""
    var FECHABD=""
    var NOM_SUCURSAL=""
    var DES_AUXILIAR=""
    var COD_SUCURSAL=""
    var COD_DOCUMENTO=""
    var COD_EXPEDIENTE=""
    var COD_ATENCION=""
    var COD_MEDICO=""

    var IDE_HORA=""
    var DES_HORA=""
    var COD_ESPECIALIDAD=""
    var PRECIO_V=""
    var COD_COBERTURA="100"
    var interfaceHorario: InterfaceHorario? = null
    var reprogramado=""
    lateinit var interfaceHora: InterfaceHorario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDoctorDetalleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        IMAGEN = intent.getSerializableExtra("IMAGEN").toString()
        ESPECIALIDAD = intent.getSerializableExtra("especialidad").toString()
        COD_ESPECIALIDAD = intent.getSerializableExtra("COD_ESPECIALIDAD").toString()
        PRECIO_V = intent.getSerializableExtra("PRECIO_V").toString()
        FECHA = intent.getSerializableExtra("FECHA").toString()
        FECHABD = intent.getSerializableExtra("FECHABD").toString()
        NOM_SUCURSAL = intent.getSerializableExtra("NOM_SUCURSAL").toString()
        COD_SUCURSAL = intent.getSerializableExtra("COD_SUCURSAL").toString()
        DES_AUXILIAR = intent.getSerializableExtra("DES_AUXILIAR").toString()
        COD_MEDICO = intent.getSerializableExtra("COD_MEDICO").toString()
        COD_DOCUMENTO = intent.getSerializableExtra("COD_DOCUMENTO").toString()
        COD_EXPEDIENTE = intent.getSerializableExtra("COD_EXPEDIENTE").toString()
        COD_ATENCION = intent.getSerializableExtra("COD_ATENCION").toString()
        //Toast.makeText(applicationContext,"hora :"+COD_ESPECIALIDAD +"- "+COD_MEDICO+" "+FECHABD, Toast.LENGTH_LONG).show()
        layoutManager = StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL)

        val linearLayoutManager2 = LinearLayoutManager(this)
        linearLayoutManager2.reverseLayout = true
        linearLayoutManager2.stackFromEnd = true
        binding.recyclerorario.layoutManager =layoutManager
        binding.recyclertimeline.layoutManager=  LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        if (binding.recyclerorario != null) {
            binding.recyclerorario.setHasFixedSize(true)
        }
        verificar()
       binding.btnreservar.isEnabled=false
       binding.btnreservar.setOnClickListener {
           goactivity()
        }
        binding.imageViewplaces4.setOnClickListener {
            salir()
        }
        binding.tvespecialidad3.text=ESPECIALIDAD
        binding.tvfechacita2.text=FECHA
        binding.tvenombreespecialidad.text=ESPECIALIDAD
        binding.tvnombredoctor2.text=DES_AUXILIAR
        binding.tvsede3.text=NOM_SUCURSAL

        if (IMAGEN=="default"){
            binding.imgdoctor3.setImageResource(R.drawable.default_profile_image)
        }else{
            Picasso.with(applicationContext)
                .load(getString(R.string.URL_PAGINA)+ IMAGEN)
                .into(binding.imgdoctor3)
        }
//       binding.gruporadios.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
//            radioButton = findViewById<View>(checkedId) as RadioButton
//            Toast.makeText(baseContext, radioButton.getText(), Toast.LENGTH_SHORT).show()
//        }
//        )
        ListarHorario()

        //esto es para el timelinea
        //ListarHorario2()
        binding.rbdconsulta.isChecked=true
    }

    private fun verificar() {
        if (COD_DOCUMENTO==""){
            binding.btnreservar.text="RESERVAR"
        }else{
            binding.btnreservar.text="REPROGRAMAR"
            reprogramado="no"
        }
    }

    private fun salir() {
        finish()
    }
    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.rbdconsulta ->
                    if (checked) {
                        COD_COBERTURA="100"
                    }
//                R.id.rbdprocedimiento ->
//                    if (checked) {
//                        COD_COBERTURA="987"
//                    }
            }
        }
    }

    private fun goactivity() {
        if (IDE_HORA=="" || DES_HORA =="" ){
            simpleAlertFail("Vuelva a seleccionar la hora ","seleccione")
        }else{
            if (COD_DOCUMENTO==""){
                val intent = Intent(this, PagarActivity::class.java)
                intent.putExtra("IDE_HORA", IDE_HORA)
                intent.putExtra("DES_HORA", DES_HORA)
                intent.putExtra("FECHA", FECHA)
                intent.putExtra("FECHABD", FECHABD)
                intent.putExtra("NOM_SUCURSAL", NOM_SUCURSAL)
                intent.putExtra("COD_SUCURSAL", COD_SUCURSAL)
                intent.putExtra("ESPECIALIDAD", ESPECIALIDAD)
                intent.putExtra("COD_ESPECIALIDAD", COD_ESPECIALIDAD)
                intent.putExtra("PRECIO_V", PRECIO_V)
                intent.putExtra("DES_AUXILIAR", DES_AUXILIAR)
                intent.putExtra("COD_MEDICO", COD_MEDICO)
                intent.putExtra("COD_COBERTURA", COD_COBERTURA)
                intent.putExtra("COD_ATENCION", "")
                finish()
                startActivity(intent)

            }else{
                if (reprogramado=="si"){
                    irMiscitas()
                }else{
                    Reprogramar()
                }
            }
        }
    }

    private  fun Reprogramar(){
        binding.progressBar2.visibility=View.VISIBLE
        val `objecto` = JSONObject()
        try {
            `objecto`.put("fecha", FECHABD)
            `objecto`.put("COD_EXPEDIENTE", COD_EXPEDIENTE)
            `objecto`.put("cod_medico", COD_MEDICO)
            `objecto`.put("cod_especialidad", COD_ESPECIALIDAD)
            `objecto`.put("COD_ATENCION", COD_ATENCION)
            `objecto`.put("id_hora", IDE_HORA)
            `objecto`.put("cod_sucrusal", COD_SUCURSAL)
            Log.e("json ", objecto.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val peticion = "/Controllers/PacienteController.php"
        val rq = Volley.newRequestQueue(this)
        val jst = JsonObjectRequest(
            Request.Method.PUT, getString(R.string.URL_BASE)+peticion,objecto,
            { response: JSONObject ->
                binding.progressBar2.visibility=View.GONE
                //Toast.makeText(applicationContext, response.toString(), Toast.LENGTH_LONG).show()
                Log.e("respsonse ", response.toString())
                simpleAlert2("Se ha reprogramado su cita")
                binding.btnreservar.text="VER MI CITAS"
                reprogramado="si"

            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Log.e("respsonse ",volleyError.toString())
                    binding.progressBar2.visibility=View.GONE
                    reprogramado="no"
                    Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
        rq.add(jst)



//        Toast.makeText(applicationContext, "rerprogramar", Toast.LENGTH_LONG).show()
//
//        val peticion = "/Controllers/PacienteController.php"
//        val stringRequest = object : StringRequest(Request.Method.PUT, getString(R.string.URL_BASE)+peticion,
//            Response.Listener { response ->
//                Toast.makeText(applicationContext, response.toString(), Toast.LENGTH_LONG).show()
//
//                Log.e("responde",response.toString())
//            },
//            object : Response.ErrorListener {
//                override fun onErrorResponse(volleyError: VolleyError) {
//                    Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
//                }
//            })
//        {
//            @Throws(AuthFailureError::class)
//            override fun getParams(): Map<String, String> {
//                val params = HashMap<String, String>()
//                params.put("cod_atencion","222222")
//                params.put("COD_AUXILIAR","65656565")
//                Log.e("params" , params.toString())
//                return params
//            }
//        }
//        stringRequest.setRetryPolicy(
//            DefaultRetryPolicy(
//                10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//            )
//        )
//        stringRequest.setShouldCache(false)
//        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
    private  fun  irMiscitas(){
        finish()
        val intent = Intent(this, MisCitasActivity::class.java)
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  or  Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP  or  Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
  //  @RequiresApi(Build.VERSION_CODES.N)
    fun  ListarHorario(){
        binding.simpleProgressBar2.visibility= View.VISIBLE
        var listHorario =   ArrayList<Horario>()
        var listHorario2 =   ArrayList<Horario2>()

      var peticion  = "/Controllers/DoctoresController.php?tipo=horario&cod_medico=$COD_MEDICO&fecha=$FECHABD&CODBD=$COD_SUCURSAL"
     // Log.e("epeticion",peticion )
        val rqSp = Volley.newRequestQueue(this)
        val js = JsonArrayRequest(
            Request.Method.GET, getString(R.string.URL_BASE)+peticion, null,
            { response ->
                for(x in 0..response.length()-1) {
                    var COD_DOCUMENTO = response.getJSONObject(x).getString("COD_DOCUMENTO")
                    if (COD_DOCUMENTO=="L"){
                        var IDE_HORA = response.getJSONObject(x).getString("IDE_HORA")
                        var DES_HORA = response.getJSONObject(x).getString("DES_HORA")
                        //  var code = response.getJSONObject(x).getString("COD_SUCURSAL")
                        var horario =  Horario(COD_DOCUMENTO,IDE_HORA,DES_HORA,"","")
                        listHorario.add(horario)
                    }
                }
                interfaceHorario = object : InterfaceHorario {
                    override fun onCallback(value: Horario?) {
                        IDE_HORA= value?.IDE_HORA.toString()
                        DES_HORA= value?.DES_HORA.toString()
                        binding.btnreservar.isEnabled=true
                        binding.btnreservar.setBackgroundResource(R.drawable.btn_borde1)
                        binding.btnreservar.setTextColor(resources.getColor(R.color.white))
                        val scrollview = binding.scrolview
                        scrollview.post { scrollview.fullScroll(ScrollView.FOCUS_DOWN) }
                        simpleAlert(FECHA+" "+DES_HORA )
                    }
                }

                adapterhoraio = AdapterHorario(this, listHorario,interfaceHorario)
                binding.simpleProgressBar2.visibility=View.GONE
                if (listHorario.size>0){
                  //  binding.lienarmensaje.setVisibility(View.GONE);
                }else{
                  //  binding.lienarmensaje.setVisibility(View.VISIBLE);
                }
//                var studlistGrouped: Map<String, List<Horario>> =
//                listHorario.stream().collect(Collectors.groupingBy { w -> w.DES_HORA.substring(0,2) })
//                Log.e ("lista", studlistGrouped.toString())

                binding.recyclerorario.adapter=adapterhoraio

             //   adaptertimeline= AdapterTimeline(this,listHorario2)
               // binding.recyclertimeline.adapter=adaptertimeline

            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    binding.simpleProgressBar2.visibility=View.GONE
                    Log.e("errror de ",volleyError.toString() )
                    Toast.makeText(applicationContext, "error de ,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
        rqSp.add(js)
    }
    fun simpleAlert(fecha:String) {
        Alerter.create(this@DoctorDetalleActivity)
            .setTitle("Hora Seleccionada")
            .setText(fecha)
            .setIcon(R.drawable.ic_check)
            .setBackgroundColorRes(R.color.verde)
            .setIconColorFilter(0) // Optional - Removes white tint
            .show()
    }
    fun simpleAlert2(message: String) {
        Alerter.create(this@DoctorDetalleActivity)
            .setTitle("Mensaje")
            .setText(message)
            .setIcon(R.drawable.ic_check)
            .setBackgroundColorRes(R.color.verde)
            .setIconColorFilter(0)
            .show()
    }
    fun simpleAlertFail(message:String,title:String) {
        Alerter.create(this@DoctorDetalleActivity)
            .setTitle(title)
            .setText(message)
            .setIcon(R.drawable.ic_warning)
            .setBackgroundColorRes(R.color.gris)
            .setIconColorFilter(0)
            .show()
    }
    fun  ListarHorario2(){

        var listHorario2 =   ArrayList<Horario2>()
        var peticion  = "/pdoHorarioMedico2.php?cod_sucursal=001&cod_medico=$COD_MEDICO&fecha=$FECHABD&CODBD=$COD_SUCURSAL"
        val rqSp = Volley.newRequestQueue(this)
        val js = JsonArrayRequest(
            Request.Method.GET, getString(R.string.URL_BASE)+peticion, null,
            { response ->
                for(x in 0..response.length()-1) {
                    var INDEX = response.getJSONObject(x).getString("INDEX")
                    var DES_HORA = response.getJSONObject(x).getString("DES_HORA")
                    if (DES_HORA.toInt() % 2 != 0) {
                        var horario =  Horario2(INDEX,DES_HORA+":00")
                        listHorario2.add(horario)
                    }
                }
                adaptertimeline= AdapterTimeline(this,listHorario2)
                binding.recyclertimeline.adapter=adaptertimeline
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Log.e("errror de ",volleyError.toString() )
                    Toast.makeText(applicationContext, "error de ,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
        rqSp.add(js)
    }

}