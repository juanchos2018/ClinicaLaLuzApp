package com.clinicalaluz.clinicaapp

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.databinding.ActivityInicioSesionBinding
import org.json.JSONException
import org.json.JSONObject

class InicioSesionActivity : AppCompatActivity() {


    private lateinit var binding: ActivityInicioSesionBinding

    //val dialogBuilder2 = AlertDialog.Builder(this)
    private  lateinit var alert : AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_inicio_sesion)
        binding = ActivityInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = getSharedPreferences("datosuser", MODE_PRIVATE)
        var NUM_DOC_IDENTIDAD = preferences.getString("NUM_DOC_IDENTIDAD", null)

        if (NUM_DOC_IDENTIDAD!=null){
            var tipodoc = preferences.getString("logueo", null)
            val intent = Intent(this, PrincipalActivity::class.java)
            intent.putExtra("TipoSesion", tipodoc)
            startActivity(intent)
            finish()
        }

        var list = ArrayList<String>()
        list.add("Dni")
        list.add("Pasaporte")
        list.add("Cedula")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
        binding.spinnerTipoDocumento.adapter = adapter

        binding.btningresar.setOnClickListener {
            var tipo = binding.spinnerTipoDocumento.selectedItem.toString()
            var documento = binding.etNumero.text.toString()
            iniciarsesion(tipo, documento)
        }
        binding.etNumero.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (  binding.etNumero.length()>6){
                   // binding.newPulso.requestFocus()
                    binding.btningresar.setBackgroundResource(R.drawable.btn_borde1)
                    binding.btningresar.setTextColor(resources.getColor(R.color.white))
                }else {
                    binding.btningresar.setBackgroundResource(R.drawable.btn_borde3)
                    binding.btningresar.setTextColor(resources.getColor(R.color.black))
                }
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun iniciarsesion(tipo: String, documento: String) {

        if (TextUtils.isEmpty(tipo)) {
            // binding.spinnerTipoDocumento.setError("El documento no puede star vacio")

        } else if (TextUtils.isEmpty(documento)) {
            binding.etNumero.setError("Debe completar este campo")
        } else {

        //           var progres = ProgressDialog(this)
        //            progres.setMessage("Cargando...")
        //            progres.show()
             loanding("","")
            var tipodoc = ""
            if (tipo == "Dni") {
                tipodoc = "DN"
            } else if (tipo == "Pasaporte") {
                tipodoc = "PS"
            } else if (tipo == "Cedula") {
                tipodoc = "CE"
            }

            var peticion="/Controllers/UsuarioController.php?condicion=login&tipo=$tipodoc&doc=$documento"
            val rq = Volley.newRequestQueue(this)
            val jst = JsonObjectRequest(
                Request.Method.GET, getString(R.string.URL_BASE)+peticion, null,
                { response: JSONObject ->
                    val json = response.optJSONArray("usuario")
                        alert.dismiss()
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = json.getJSONObject(0)
                        var existe = jsonObject.optString("existe")

                        if (existe.equals("existe")) {

                            var DES_AUXILIAR = jsonObject.optString("DES_AUXILIAR")
                            var COD_AUXILIAR = jsonObject.optString("COD_AUXILIAR")
                            var NUM_DOC_IDENTIDAD = jsonObject.optString("NUM_DOC_IDENTIDAD")
                            var COD_PACIENTE = jsonObject.optString("COD_PACIENTE")
                            var COD_CLIENTE = jsonObject.optString("COD_CLIENTE")

                            val singleToneClass: SingleToneClass = SingleToneClass.instance
                            singleToneClass.data = documento

                            val preferences: SharedPreferences =
                                this.getSharedPreferences("datosuser", MODE_PRIVATE)
                            val editor = preferences.edit()
                            editor.putString("DES_AUXILIAR", DES_AUXILIAR)
                            editor.putString("COD_AUXILIAR", COD_AUXILIAR)
                            editor.putString("NUM_DOC_IDENTIDAD", NUM_DOC_IDENTIDAD)
                            editor.putString("COD_PACIENTE", COD_PACIENTE)
                            editor.putString("COD_CLIENTE", COD_CLIENTE)
                            editor.putString("logueo", "dni")
                            editor.commit()
                            //   Toast.makeText(applicationContext, COD_PACIENTE, Toast.LENGTH_LONG).show()

                            val intent = Intent(this, PrincipalActivity::class.java)
                            intent.putExtra("TipoSesion", "dni")
                            startActivity(intent)
                            finish()

                        } else {
                            fail("No Existe Usuario")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                      //  progres.dismiss()
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
    }
    private fun fail(mensaje: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val btcerrrar: Button
        val tvmensaje: TextView
        val v = LayoutInflater.from(applicationContext)
            .inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_fail, null)
        val animationView: LottieAnimationView =
            v.findViewById(com.clinicalaluz.clinicaapp.R.id.animation_fail)
        animationView.playAnimation()
        dialogBuilder.setView(v)
        btcerrrar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.idbtncerrardialogo2) as Button
        tvmensaje = v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvmensaje2)
        tvmensaje.text = mensaje
        val alert = dialogBuilder.create()
        btcerrrar.setOnClickListener {
            alert.dismiss()
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }


    private fun loanding(mensaje: String, descrpcion: String) {
        val dialogBuilder2 = AlertDialog.Builder(this)
        val btcerrrar: Button
        val tvmensaje: TextView
        var tvedsderpcion: TextView
        val v = LayoutInflater.from(applicationContext)
            .inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_loanding, null)
        val animationView: LottieAnimationView =
            v.findViewById(com.clinicalaluz.clinicaapp.R.id.loandingone)
        animationView.playAnimation()
        dialogBuilder2.setView(v)
//        btcerrrar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.idbtncerrardialogo) as Button
//        tvmensaje = v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvmensaje)
//        tvedsderpcion=v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvdecripcion)
//        tvedsderpcion.text=descrpcion
//        tvmensaje.text = mensaje
        //val alert = dialogBuilder2.create()

        alert=dialogBuilder2.create()
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }
}

