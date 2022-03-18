package com.clinicalaluz.clinicaapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.Adapters.AdapterMisCitas
import com.clinicalaluz.clinicaapp.Fragment.DialogoFragment.Companion.newInstance
import com.clinicalaluz.clinicaapp.Interface.InterfaceCita
import com.clinicalaluz.clinicaapp.Interface.InterfaceHorario
import com.clinicalaluz.clinicaapp.clases.ClsMisCitas
import com.clinicalaluz.clinicaapp.clases.Horario
import com.clinicalaluz.clinicaapp.databinding.ActivityMisCitasBinding
import com.tapadoo.alerter.Alerter
import java.util.*
import kotlin.collections.ArrayList


class MisCitasActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMisCitasBinding
    val listaMisCitas = ArrayList<ClsMisCitas>()
    private  lateinit var  adapter: AdapterMisCitas
    var interfaceCita: InterfaceCita? = null
    var  DES_AUXILIAR_MI=""
    private  lateinit var alert : AlertDialog
    var tipoEntrada=""

    private  var bottomSheetDialog = newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val linearLayoutManager2 = LinearLayoutManager(this)
        linearLayoutManager2.reverseLayout = true
        linearLayoutManager2.stackFromEnd = true
        binding.recyclermicictas.layoutManager = linearLayoutManager2
        binding.imageViewplaces6.setOnClickListener {
            salir()
        }
        tipoEntrada = intent.getSerializableExtra("tipoEntrada").toString()
    }

    private fun salir() {
        finish()
        if (true){

        }else{

        }
    }
    override fun onStart() {
       super.onStart()
       val preferences = getSharedPreferences("datosuser", Context.MODE_PRIVATE)
       DES_AUXILIAR_MI = preferences.getString("DES_AUXILIAR", null).toString()
       var  COD_AUXILIAR = preferences.getString("COD_AUXILIAR", null).toString()
       var  NUM_DOC_IDENTIDAD = preferences.getString("NUM_DOC_IDENTIDAD", null).toString()
       var  COD_PACIENTE= preferences.getString("COD_PACIENTE", null).toString()
       var  COD_CLIENTE = preferences.getString("COD_CLIENTE", null).toString()
       showMisCitas(NUM_DOC_IDENTIDAD)
    }

    fun showMisCitas(NUM_DOC_IDENTIDAD:String){
        listaMisCitas.clear()
        binding.simpleProgressBar5.visibility=View.VISIBLE
        var peticion = "/Controllers/UsuarioController.php?condicion=citas&nuc_identidad=$NUM_DOC_IDENTIDAD"
        val rq = Volley.newRequestQueue(this)
        val arr = JsonArrayRequest(
            Request.Method.GET, getString(R.string.URL_BASE)+peticion, null,
            { response ->
                for(x in 0..response.length()-1){
                    val COD_ATENCION = response.getJSONObject(x).getString("COD_ATENCION")
                    val COD_MEDICO = response.getJSONObject(x).getString("COD_MEDICO")
                    val COD_EXPEDIENTE = response.getJSONObject(x).getString("COD_EXPEDIENTE")
                    val DES_AUXILIAR = response.getJSONObject(x).getString("DES_AUXILIAR")
                    val COD_ESPECIALIDAD = response.getJSONObject(x).getString("COD_ESPECIALIDAD")
                    val COD_SUCURSAL= response.getJSONObject(x).getString("COD_SUCURSAL")
                    val IDE_HORA = response.getJSONObject(x).getString("IDE_HORA")
                    val DES_HORA = response.getJSONObject(x).getString("DES_HORA")
                    val DES_ESPECIALIDAD = response.getJSONObject(x).getString("DES_ESPECIALIDAD")
                    val FEC_INSERCION = response.getJSONObject(x).getString("FEC_INSERCION")
                    val FEC_ATENCION = response.getJSONObject(x).getString("FEC_ATENCION")
                    val NOM_SUCURSAL = response.getJSONObject(x).getString("NOM_SUCURSAL")
                    val BD = response.getJSONObject(x).getString("BD")
                    val PRECIO_V =response.getJSONObject(x).getString("PRECIO_V")
                    val COD_DOCUMENTO =response.getJSONObject(x).getString("COD_DOCUMENTO")
                    val TIP_ESTADO =response.getJSONObject(x).getString("TIP_ESTADO")
                    val IMP_TOTAL =response.getJSONObject(x).getString("IMP_TOTAL")
                    val COD_BD =response.getJSONObject(x).getString("COD_BD")

                    listaMisCitas.add(ClsMisCitas(COD_ATENCION,COD_MEDICO,COD_EXPEDIENTE,DES_AUXILIAR,COD_ESPECIALIDAD,COD_SUCURSAL,IDE_HORA,DES_HORA,DES_ESPECIALIDAD,FEC_INSERCION,FEC_ATENCION,NOM_SUCURSAL,BD,PRECIO_V,COD_DOCUMENTO,IMP_TOTAL,COD_BD))
                }
                adapter = AdapterMisCitas(this,listaMisCitas)
                adapter.DES_AUXILIAR=DES_AUXILIAR_MI
                binding.recyclermicictas.adapter=adapter
                binding.simpleProgressBar5.visibility=View.GONE
                interfaceCita = object : InterfaceCita {
                    override fun onCallback(value: ClsMisCitas?) {
                        reprogramarCita(value)
                    }
                    override fun onCallbackDos(value: ClsMisCitas?) {
                        pagarCita(value)
                    }
                    override fun onCallbackTres(value: String) {
                        mesagePagado(value)
                    }
                }
                adapter.setOnClickListener { v: View? ->
                    //Toast.makeText(applicationContext, listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).COD_SUCURSAL, Toast.LENGTH_LONG).show()
                    bottomSheetDialog?.interfaceCita=interfaceCita
                    bottomSheetDialog?.desEspecialidad=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).DES_ESPECIALIDAD
                    bottomSheetDialog?.codEspecialidad=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).COD_ESPECIALIDAD
                    bottomSheetDialog?.nombnredoctor=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).DES_AUXILIAR
                    bottomSheetDialog?.nombreespecialidad=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).DES_ESPECIALIDAD
                    bottomSheetDialog?.fechaAtencion=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).FEC_ATENCION
                    bottomSheetDialog?.codAtencion=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).COD_ATENCION
                    bottomSheetDialog?.codSucursal=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).COD_SUCURSAL
                    bottomSheetDialog?.nomSucursal=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).NOM_SUCURSAL
                    bottomSheetDialog?.cod_medico=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).COD_MEDICO
                    bottomSheetDialog?.desHora=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).DES_HORA
                    bottomSheetDialog?.idHora=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).IDE_HORA
                    bottomSheetDialog?.precio_v=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).PRECIO_V
                    bottomSheetDialog?.bd=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).BD
                    bottomSheetDialog?.codDocumento=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).COD_DOCUMENTO
                    bottomSheetDialog?.imptotal=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).IMP_TOTAL
                    bottomSheetDialog?.cod_bd=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).COD_BD
                    bottomSheetDialog?.cod_expediente=listaMisCitas.get(binding.recyclermicictas.getChildAdapterPosition(v!!)).COD_EXPEDIENTE
                    bottomSheetDialog?.show(supportFragmentManager,"bottonshelld")

                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    binding.simpleProgressBar5.visibility=View.GONE
                    Log.e("errror",volleyError.toString() )
                    Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
        rq.add(arr)
    }

    fun pagarCita(obj: ClsMisCitas?){
        val intent = Intent(this, PagarActivity::class.java)
        var COD_COBERTURA="100"
        intent.putExtra("IDE_HORA", obj!!.IDE_HORA)
        intent.putExtra("DES_HORA", obj!!.DES_HORA)
        intent.putExtra("FECHA",obj.FEC_ATENCION)
        intent.putExtra("FECHABD", obj.FEC_ATENCION)
        intent.putExtra("NOM_SUCURSAL", obj.NOM_SUCURSAL)
        intent.putExtra("COD_SUCURSAL", obj.COD_BD)
        intent.putExtra("ESPECIALIDAD", obj.DES_ESPECIALIDAD)
        intent.putExtra("COD_ESPECIALIDAD", obj.COD_ESPECIALIDAD)
        intent.putExtra("DES_AUXILIAR", obj.DES_AUXILIAR)
        intent.putExtra("COD_MEDICO", obj.COD_MEDICO)
        intent.putExtra("COD_COBERTURA", COD_COBERTURA)
        intent.putExtra("COD_ATENCION",  obj.COD_ATENCION)
        intent.putExtra("PRECIO_V",  obj.IMP_TOTAL)
        //finish()

//        ESPECIALIDAD = intent.getSerializableExtra("ESPECIALIDAD").toString()
//        COD_ESPECIALIDAD = intent.getSerializableExtra("COD_ESPECIALIDAD").toString()
//        PRECIO_V = intent.getSerializableExtra("PRECIO_V").toString()
//        FECHA = intent.getSerializableExtra("FECHA").toString()
//        FECHABD = intent.getSerializableExtra("FECHABD").toString()
//        NOM_SUCURSAL = intent.getSerializableExtra("NOM_SUCURSAL").toString()
//        COD_SUCURSAL = intent.getSerializableExtra("COD_SUCURSAL").toString()
//        DES_AUXILIAR = intent.getSerializableExtra("DES_AUXILIAR").toString()
//        COD_MEDICO = intent.getSerializableExtra("COD_MEDICO").toString()
//        IDE_HORA = intent.getSerializableExtra("IDE_HORA").toString()
//        DES_HORA = intent.getSerializableExtra("DES_HORA").toString()
//        COD_COBERTURA = intent.getSerializableExtra("COD_COBERTURA").toString()
//        COD_ATENCION=intent.getSerializableExtra("COD_ATENCION").toString()

        startActivity(intent)
    }

    fun reprogramarCita(value: ClsMisCitas?){

        val intent = Intent(applicationContext, DoctoresActivity::class.java)
        intent.putExtra("especialidad", value!!.DES_ESPECIALIDAD)
        intent.putExtra("COD_ATENCION", value!!.COD_ATENCION)
        intent.putExtra("COD_ESPECIALIDAD", value.COD_ESPECIALIDAD)
        intent.putExtra("PRECIO_V", value.IMP_TOTAL)
        intent.putExtra("NOM_SUCURSAL", value.NOM_SUCURSAL)
        intent.putExtra("COD_SUCURSAL", value.COD_BD)
        intent.putExtra("COD_EXPEDIENTE", value.COD_EXPEDIENTE)
        intent.putExtra("COD_DOCUMENTO", value.COD_DOCUMENTO)//OS  FT BV

        //Log.e("datos", value!!.DES_ESPECIALIDAD+"-"+value.COD_ESPECIALIDAD+"-"+value.NOM_SUCURSAL+"-"+value.COD_BD)
        //Toast.makeText(applicationContext, value!!.DES_ESPECIALIDAD, Toast.LENGTH_LONG).show()

         startActivity(intent)
    }
    fun diaglogoReprogramar(fechacurrent:String){
        val dialogBuilder = AlertDialog.Builder(this)
        val btncerrar: Button
        val btnreprogramar: Button
        val btnnuevafecha:Button
        var tvfechaCita:TextView
        var fechasendpost=""
        val c = Calendar.getInstance()
        val yyyy = c.get(Calendar.YEAR)
        val MM = c.get(Calendar.MONTH)
        val dd = c.get(Calendar.DAY_OF_MONTH)
        var fechaTexto = ""
        var di=dd
        var me=MM+1
        if (di.toString().length==1){
            if (me.toString().length==1){
                fechaTexto= String.format("0$di-0$me-$yyyy")
            }else{
                fechaTexto= String.format("0$di-$me-$yyyy")
            }
        }else{
            if (me.toString().length==1){
                fechaTexto= String.format("$di-0$me-$yyyy")
            }else{
                fechaTexto= String.format("$di-$me-$yyyy")
            }
        }
        val v = LayoutInflater.from(applicationContext)
            .inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_reprogramar, null)
        dialogBuilder.setView(v)
        btncerrar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.btncerrardialogo) as Button
        btnreprogramar=v.findViewById(com.clinicalaluz.clinicaapp.R.id.btnreprogramarcita) as Button
        btnnuevafecha=v.findViewById(com.clinicalaluz.clinicaapp.R.id.btnnuevafecha) as Button
        tvfechaCita=v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvfechacitaActual)
        btnnuevafecha.text=fechaTexto
        tvfechaCita.text=fechacurrent

        btnnuevafecha.setOnClickListener {
            val recogerFecha = DatePickerDialog(this,
                { view, year, month, dayOfMonth ->
                   var dia=dayOfMonth
                   var mes=month+1
                   if (dia.toString().length==1){
                       if (mes.toString().length==1){
                           fechasendpost= String.format("$year-0$mes-0$dia")
                       }else{
                           fechasendpost= String.format("$year-$mes-0$dia")
                       }
                   }else{
                       if (mes.toString().length==1){
                           fechasendpost= String.format("$year-0$mes-$dia")
                       }else{
                           fechasendpost= String.format("$year-$mes-$dia")
                       }
                   }
                   // Toast.makeText(applicationContext, fechasendpost, Toast.LENGTH_LONG).show()
                   var  fechaText = String.format("$dayOfMonth-${month+1}-$year")
                   btnnuevafecha.text=fechaText
                }, yyyy, MM, dd
            )
            recogerFecha.getDatePicker().setMinDate(System.currentTimeMillis())
            recogerFecha.show()
        }

        alert = dialogBuilder.create()
        btncerrar.setOnClickListener {
            alert.dismiss()
        }

        btnreprogramar.setOnClickListener {

        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

    fun mesagePagado(message:String){
        bottomSheetDialog?.dismiss()
        Alerter.create(this@MisCitasActivity)
            .setTitle("Mensaje")
            .setText(message)
            .setIcon(R.drawable.ic_warning)
            .setBackgroundColorRes(R.color.celeste)
            .setIconColorFilter(0) // Optional - Removes white tint
            .show()
    }
    fun  saveReprogramar()
    {
    }
    fun saveSuccess(message:String){
        Alerter.create(this@MisCitasActivity)
            .setTitle("Mensaje")
            .setText(message)
            .setIcon(R.drawable.ic_check)
            .setBackgroundColorRes(R.color.verde)
            .setIconColorFilter(0) // Optional - Removes white tint
            .show()
    }


}