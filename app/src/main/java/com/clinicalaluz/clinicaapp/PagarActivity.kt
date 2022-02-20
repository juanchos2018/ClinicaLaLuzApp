package com.clinicalaluz.clinicaapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.databinding.ActivityPagarBinding
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.util.*


class PagarActivity : AppCompatActivity() {

    private lateinit var  binding : ActivityPagarBinding

    private var ESPECIALIDAD=""
    var FECHA=""
    var FECHABD=""
    var NOM_SUCURSAL=""
    var DES_AUXILIAR=""
    var COD_SUCURSAL=""
    var COD_MEDICO=""
    var IDE_HORA=""
    var DES_HORA=""
    var COD_ESPECIALIDAD=""
    var PRECIO_V=""
    var NUM_DOC_IDENTIDAD=""
    var COD_PACIENTE=""
    var COD_AUXILIAR=""
    var COD_CLIENTE=""
    var COD_COBERTURA=""
    var TIP_PACIENTE="PAR"
    var TIP_PARENTESCO="1"
    var TIP_COMPROBANTE="BV"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPagarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ESPECIALIDAD = intent.getSerializableExtra("ESPECIALIDAD").toString()
        COD_ESPECIALIDAD = intent.getSerializableExtra("COD_ESPECIALIDAD").toString()
        PRECIO_V = intent.getSerializableExtra("PRECIO_V").toString()
        FECHA = intent.getSerializableExtra("FECHA").toString()
        FECHABD = intent.getSerializableExtra("FECHABD").toString()
        NOM_SUCURSAL = intent.getSerializableExtra("NOM_SUCURSAL").toString()
        COD_SUCURSAL = intent.getSerializableExtra("COD_SUCURSAL").toString()
        DES_AUXILIAR = intent.getSerializableExtra("DES_AUXILIAR").toString()
        COD_MEDICO = intent.getSerializableExtra("COD_MEDICO").toString()
        IDE_HORA = intent.getSerializableExtra("IDE_HORA").toString()
        DES_HORA = intent.getSerializableExtra("DES_HORA").toString()
        COD_COBERTURA = intent.getSerializableExtra("COD_COBERTURA").toString()

        binding.tvhoracita.text=DES_HORA
        binding.tvfechacita3.text=FECHA
        binding.tvnombredoctor3.text=DES_AUXILIAR
        binding.tvsede4.text=NOM_SUCURSAL
        binding.tvespecialidad4.text=ESPECIALIDAD

        binding.imageViewplaces.setOnClickListener {
            salir()
        }

        binding.btnpagar.setOnClickListener {
           // Toast.makeText(applicationContext, "se va a pagar", Toast.LENGTH_LONG).show()
            payment()
        }
        binding.btnpagar.isEnabled=false
        binding.rbdboleta.isChecked=true

        binding.datosfactura.isVisible=false
        NameCliente()


        binding.numeroruc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.numeroruc.length()==11){
                    consultarruc(binding.numeroruc.text)
                }
            }
        })

//
//        binding.newSistolica.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                if (binding.newSistolica.length()==3){
//                    binding.newDiastolica.requestFocus()
//                }
//            }
//            override fun afterTextChanged(s: Editable) {
//            }
//        })


//        binding.btnconsutlar.setOnClickListener {
//            var ruc =binding.numeroruc.text
//            consultarruc(ruc)
//        }
    }

    private fun consultarruc(ruc: Editable) {
        Toast.makeText(applicationContext, ruc, Toast.LENGTH_LONG).show()
            val url = "http://161.132.198.52:8080/app_laluz/pdoConsultaRuc.php?ruc=$ruc"
            val rq = Volley.newRequestQueue(this)
            val jst = JsonObjectRequest(
                Request.Method.GET, url, null,
                { response: JSONObject ->
                    Log.e("responmde ",response.toString())

                    try {
                        var razon_social =response.optString("razon_social")
                        binding.razonsocial.setText(razon_social)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(volleyError: VolleyError) {
                        // progres.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "error," + volleyError.toString() + "",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            rq.add(jst)
    }

    private fun payment() {

        val intentLogin = Intent(this@PagarActivity, PaymentActivity::class.java)
        intentLogin.putExtra("total",PRECIO_V)
        intentLogin.putExtra("id_pedido",binding.tvcodigoatencion.text)
        intentLogin.putExtra("TIP_COMPROBANTE",TIP_COMPROBANTE)
        intentLogin.putExtra("COD_CLIENTE",COD_CLIENTE)
        intentLogin.putExtra("COD_AUXILIAR",COD_AUXILIAR)
        intentLogin.putExtra("COD_SUCURSAL",COD_SUCURSAL)

        //COD_CLIENTE
        Toast.makeText(applicationContext, binding.tvcodigoatencion.text, Toast.LENGTH_SHORT).show()
        startActivity(intentLogin)
    }


    private  fun getPrecio(){
        val url = "http://161.132.198.52:8080/app_laluz/pdoprecioespecialidad.php?cod_especialidad=$COD_ESPECIALIDAD&sucursal=$COD_SUCURSAL"
        val rq = Volley.newRequestQueue(this)
        val jst = JsonObjectRequest(
            Request.Method.GET, url,null,
            { response: JSONObject ->
                val json = response.optJSONArray("especialidad")
                Log.e("respsonse ", json.toString())
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = json.getJSONObject(0)
                    var existe =jsonObject.optString("existe")

                    if (existe.equals("si")){
                        var precio =jsonObject.optString("PRECIO_P")
                       // binding.btnpagar.text="PAGAR  $precio"
                    }else{
                        Log.e("no", "no existe")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
        rq.add(jst)
    }


    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.rbdboleta ->
                    if (checked) {
                        TIP_COMPROBANTE="BV"
                        binding.datosfactura.isVisible=false
                        Toast.makeText(applicationContext, TIP_COMPROBANTE, Toast.LENGTH_LONG).show()
                    }
                R.id.rbdfactura ->
                    if (checked) {
                        TIP_COMPROBANTE="FT"
                        binding.datosfactura.isVisible=true
                        Toast.makeText(applicationContext, TIP_COMPROBANTE, Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
    fun  NameCliente(){
        val preferences = getSharedPreferences("datosuser", Context.MODE_PRIVATE)
        DES_AUXILIAR = preferences.getString("DES_AUXILIAR", null).toString()
        COD_AUXILIAR = preferences.getString("COD_AUXILIAR", null).toString()
        NUM_DOC_IDENTIDAD = preferences.getString("NUM_DOC_IDENTIDAD", null).toString()
        COD_PACIENTE= preferences.getString("COD_PACIENTE", null).toString()
        COD_CLIENTE = preferences.getString("COD_CLIENTE", null).toString()
        binding.tvnombrecliente.text=DES_AUXILIAR
        GenerarCodigoAtencion(IDE_HORA,COD_MEDICO,NUM_DOC_IDENTIDAD,DES_AUXILIAR,FECHABD,COD_SUCURSAL,COD_ESPECIALIDAD,"AMB",COD_COBERTURA,COD_PACIENTE,COD_AUXILIAR,COD_CLIENTE,TIP_PACIENTE,TIP_PARENTESCO,"100","100","VA","PO","8","2","","")
    }

    private fun salir() {
        finish()
    }

    private fun obtenerdatosruc(){

    }
    private  fun GenerarCodigoAtencion(id_hora:String,cod_medico:String,num_documento:String,nombres:String,fecha:String,sucursal:String,cod_especialidad:String,tip_cobertura:String,cod_cobertura:String,cod_paciente:String,cod_auxiliar:String,cod_cliente:String,tip_paciente:String,tip_parentesco:String,imp_valor_poliza:String,imp_valor_real:String,tip_copago:String,tip_valor:String,nuevo_convenio:String,nueva_empresa:String,cod_autorizacion:String,cod_asegurado:String){

        binding.simpleProgressBar4.setVisibility(View.VISIBLE);

        val peticion = "/pdoCodigoAtencion.php?"
        val stringRequest = object : StringRequest(Request.Method.POST, getString(R.string.URL_BASE)+peticion,
            Response.Listener { response ->
                try {

//                    var jsonObject: JSONObject? = null
//                    var codAtencion =jsonObject!!.getString("codAtencion")

                    var jsonObject=JSONTokener(response).nextValue() as JSONObject
                    var codAtencion =jsonObject.optString("codAtencion")
                     binding.tvcodigoatencion.text=   codAtencion
                    if (codAtencion!="0"){
                        binding.btnpagar.isEnabled=true
                        binding.btnpagar.setBackgroundResource(R.drawable.btn_borde1)
                        binding.btnpagar.text="PAGAR  $PRECIO_V"
                    }

                    binding.simpleProgressBar4.setVisibility(View.GONE)
                } catch (e: JSONException) {
                    binding.simpleProgressBar4.setVisibility(View.GONE);
                    Toast.makeText(applicationContext, ""+e.message+"", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    binding.simpleProgressBar4.setVisibility(View.GONE);
                    Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
           {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("nuevo_ide_hora",id_hora)
                params.put("nuevo_cod_medico",cod_medico)
                params.put("nuevo_doc_paciente",num_documento)
                params.put("nuevo_nom_paciente",nombres)
                params.put("nueva_fecha",fecha)
                params.put("sucursal",sucursal)
                params.put("especialidad",cod_especialidad)
                params.put("nuevo_tipo_cobertura",tip_cobertura)
                params.put("nueva_cobertura",cod_cobertura)
                params.put("nuevo_cod_paciente",cod_paciente)
                params.put("nuevo_cod_auxiliar",cod_auxiliar)
                params.put("nuevo_cod_cliente",cod_cliente)
                params.put("nuevo_tipo_paciente",tip_paciente)
                params.put("nuevo_parentesco",tip_parentesco)
                params.put("imp_valor_poliza",imp_valor_poliza)
                params.put("imp_valor_real",imp_valor_real)
                params.put("tip_copago",tip_copago)
                params.put("tip_valor",tip_valor)
                params.put("nuevo_convenio",nuevo_convenio)
                params.put("nueva_empresa",nueva_empresa)
                params.put("nuevo_cod_autorizacion",cod_autorizacion)
                params.put("nuevo_cod_asegurado",cod_asegurado)
                Log.e("params" , params.toString())
                return params
            }
        }
        stringRequest.setRetryPolicy(
            DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        stringRequest.setShouldCache(false)
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }

}