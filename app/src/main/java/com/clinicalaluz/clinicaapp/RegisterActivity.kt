package com.clinicalaluz.clinicaapp

import android.R
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.clinicalaluz.clinicaapp.databinding.ActivityRegisterBinding
import org.json.JSONException
import java.util.*


class RegisterActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener{
    private lateinit var binding : ActivityRegisterBinding
    private var day = 0
    private var month = 0
    private var year = 0

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var fechasendpost=""

   // var builder1: AlertDialog.Builder? = null
   // var alert1: AlertDialog? = null
    private val frames = 9
    private var currentAnimationFrame = 0
    private var animationView: LottieAnimationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pickDate()

        binding.buttonRegCreate.setOnClickListener {
           sendPost()
        }

        binding.returnRegister.setOnClickListener { finish() }
        binding.buttonRegCancel.setOnClickListener { finish()  }
    }


    fun sendPost() {
        var documento  =  binding.regDocIdentity.text.toString()
        var nombres  =  binding.regName.text.toString()
        var apellidos  =  binding.regLastname.text.toString()
        var fecha  =  binding.regDate.text.toString()
        var celular  =  binding.regPhone.text.toString()
        var email  =  binding.regEmail.text.toString()
        var clave  =  binding.regPassword.text.toString()

        if (documento.length<=7 || documento.length>=9){
          //  Toast.makeText(applicationContext ,"El Numero de Documento con 8 digitos" , Toast.LENGTH_SHORT).show()
            binding.regDocIdentity.setError("El Numero de Documento con 8 digitos")
        }
        else if ( TextUtils.isEmpty(documento))
        {
            binding.regDocIdentity.setError("Escribir Documento")

        }else if (TextUtils.isEmpty(nombres)){
            binding.regName.setError("Escribir Nombres")
        }
        else if (TextUtils.isEmpty(apellidos)){
            binding.regLastname.setError("Escribir Apellidos")
        }
        else if (TextUtils.isEmpty(email)){
            binding.regEmail.setError("Escribir Correo")
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.regEmail.setError("Coreo no valido")
        }
        else if (TextUtils.isEmpty(clave)){
            binding.regPassword.setError("Escribir Clave")
        }
        else if (clave.length<6){

            binding.regPassword.setError("Clave de 6 digitos")
        }
        else {

            var progres = ProgressDialog(this)
            progres.setMessage("Cargando...")
            progres.show()
                val url = "http://161.132.198.52:8080/app_laluz/pdoRegister.php?"
                val stringRequest = object : StringRequest(
                    Request.Method.POST, url, Response.Listener { response ->
                        try {
                            progres.dismiss()
                           // Log.e("resultadop",response.toString())
                            if (response.toString().trim()== "Enviado"){
                                val preferences: SharedPreferences = this@RegisterActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
                                val editor = preferences.edit()
                                editor.putString("nombres", nombres)
                                editor.putString("dni", documento)
                                editor.putString("correo", email)
                                editor.putString("logueo", "dni")
                                editor.commit()
                                val singleToneClass: SingleToneClass = SingleToneClass.instance
                                singleToneClass.data=documento
                                men("Registrado","Comuniquese con la clinica para activar su Cuenta")

                                //men("Se ha enviando un mensaje a su correo","Verifica tu bandeja de span")
                               // men("Registrado","Comuniquese con la clinica para activar su Cuenta")
                            }else if(response.toString().trim()== "Modificado") {
                                val preferences: SharedPreferences =
                                    this@RegisterActivity.getSharedPreferences(
                                        "datosuser",
                                        MODE_PRIVATE
                                    )
                                val editor = preferences.edit()
                                editor.putString("nombres", nombres)
                                editor.putString("dni", documento)
                                editor.putString("correo", email)
                                editor.putString("logueo", "dni")
                                editor.commit()
                                val singleToneClass: SingleToneClass = SingleToneClass.instance
                                singleToneClass.data = documento
                                men(
                                    "Registrado",
                                    "Comuniquese con la clinica para activar su Cuenta"
                                )

                            }
                            else if (response.toString().trim()=="Usado"){
                                    warning("Este Correo Ya esta En Uso por otro Usuario")

                            }else if(response.toString().trim()=="ExisteCorreo"){
                                warning("Este Correo Ya esta En Uso por otro Usuario")
                            }
                            else if (response.toString().trim()=="Active"){
                                val preferences: SharedPreferences = this@RegisterActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
                                val editor = preferences.edit()
                                editor.putString("nombres", nombres)
                                editor.putString("dni", documento)
                                editor.putString("correo", email)
                                editor.putString("logueo", "facebook")
                                editor.commit()
                                val singleToneClass: SingleToneClass = SingleToneClass.instance
                                singleToneClass.data=documento
                                men("Sus Datos ya existen","Comuniquese con la clinica para activar su Cuenta")

                            }
                            else{
                                Toast.makeText(applicationContext ,"E: "+response.toString()+"" , Toast.LENGTH_SHORT).show()
                            }

                        } catch (e: JSONException) {
                            Toast.makeText(applicationContext, "ex :"+e.message.toString()+"", Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                            progres.dismiss()
                        }
                    },
                    object : Response.ErrorListener {
                        override fun onErrorResponse(volleyError: VolleyError) {
                            Toast.makeText(applicationContext, "error:"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                            progres.dismiss()
                        }
                    })
                {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params.put("doc",documento)
                        params.put("nombres",nombres)
                        params.put("apellidos",apellidos)
                        params.put("fecha",fechasendpost)
                        params.put("celular",celular)
                        params.put("email",email)
                        params.put("clave",clave)
                        params.put("logueo","dni")
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


    private fun dialogpdate(mensaje: String,descrpcion:String,correo:String,cod:String,nombre:String,clave:String){
        val dialogBuilder = AlertDialog.Builder(this)
        val btcerrrar: Button
        val btnupdate: Button
        val tvmensaje: TextView
        var tvedsderpcion :TextView
        val  v = LayoutInflater.from(applicationContext).inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_upate, null)
        val animationView:LottieAnimationView = v.findViewById(com.clinicalaluz.clinicaapp.R.id.animation_viewcheck)
        animationView.playAnimation()
        dialogBuilder.setView(v)
        btcerrrar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.btncerrar) as Button
        btnupdate = v.findViewById(com.clinicalaluz.clinicaapp.R.id.btnactualizar) as Button
        tvmensaje = v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvmensajeudpate)
        tvedsderpcion=v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvdecripcion2)
        tvedsderpcion.text=descrpcion
        tvmensaje.text = mensaje
        val alert = dialogBuilder.create()
        btcerrrar.setOnClickListener {
            alert.dismiss()
        }
        btnupdate.setOnClickListener {
            alert.dismiss()
            UpodateInfo(nombre,cod,correo,clave )
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

    private  fun  UpodateInfo(nombres:String, documento:String, email:String,clave:String){
        val url = "http://161.132.198.52:8080/app_laluz/pdoActualizarDatos.php?"
        var progres = ProgressDialog(this)
        progres.setMessage("Cargando")
        progres.show()
        val stringRequest = object : StringRequest(
            Request.Method.POST, url, Response.Listener { response ->
                try {
                    if (response.toString().trim() == "Enviado"){
//                        val preferences: SharedPreferences = this@RegisterActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
//                        val editor = preferences.edit()
//                        editor.putString("nombres", nombres)
//                        editor.putString("dni", documento)
//                        editor.putString("correo", email)
//                        editor.commit()
                       //  men("Actualizado","Se ha Actualizado su correo")
                        men("Actualizado","Comuniquese con la clinica para activar su Cuenta")
                        val singleToneClass: SingleToneClass = SingleToneClass.instance
                        singleToneClass.data=documento

                    }else if (response.toString().trim()=="ExisteCorreo")   {
                        warning("Este Correo ya esta en uso")
                    }else{
                        Toast.makeText(applicationContext ,""+response.toString()+"" , Toast.LENGTH_SHORT).show()
                    }
                    progres.dismiss()
                } catch (e: JSONException) {
                    Toast.makeText(applicationContext, ""+response.toString()+"", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                    progres.dismiss()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Toast.makeText(applicationContext, "error:"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                    progres.dismiss()
                }
            })
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("doc",documento)
                params.put("email",email)
                params.put("clave",clave)
                params.put("logueo","dni")
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
    private fun men(mensaje: String,descrpcion:String){
        val dialogBuilder = AlertDialog.Builder(this)
        val btcerrrar: Button
        val tvmensaje: TextView
        var tvedsderpcion :TextView
        val  v = LayoutInflater.from(applicationContext).inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_correo, null)
        val animationView:LottieAnimationView = v.findViewById(com.clinicalaluz.clinicaapp.R.id.animation_viewcheck)
        animationView.playAnimation()
        dialogBuilder.setView(v)
        btcerrrar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.idbtncerrardialogo) as Button
        tvmensaje = v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvmensaje)
        tvedsderpcion=v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvdecripcion)
        tvedsderpcion.text=descrpcion
        tvmensaje.text = mensaje
        val alert = dialogBuilder.create()
        btcerrrar.setOnClickListener {
            alert.dismiss()
            finish()
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

    private fun warning(mensaje: String){
        val dialogBuilder = AlertDialog.Builder(this)
        val btcerrrar: Button
        val tvmensaje: TextView
        val  v = LayoutInflater.from(applicationContext).inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_warning, null)
        val animationView:LottieAnimationView = v.findViewById(com.clinicalaluz.clinicaapp.R.id.animation_viewcheck)
        animationView.playAnimation()
        dialogBuilder.setView(v)
        btcerrrar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.idbtncerrardialogo) as Button
        tvmensaje = v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvmensaje)
        tvmensaje.text = mensaje
        val alert = dialogBuilder.create()
        btcerrrar.setOnClickListener {
            alert.dismiss()
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

     //   private  fun logo(){
     //       val animationView:LottieAnimationView = findViewById(com.example.clinicaapp.R.id.animation_viewcheck)
      //      animationView.setAnimation("hello-world.json")
      //      animationView.loop(true)
      //      animationView.playAnimation()
       // }
    private fun resetAnimationView() {
        currentAnimationFrame = 0
        animationView!!.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER,
            { null }
        )
    }

    private fun getDateCalendar(){
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }
    private fun pickDate(){
        binding.regDate.setOnClickListener {
            getDateCalendar()
            DatePickerDialog(this, this, year, month, day).show()
            binding.regDate.setTextColor(resources.getColor(R.color.black))
        }
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateCalendar()
        binding.regDate.text = "$savedDay/${savedMonth+1}/$savedYear"
        fechasendpost= String.format("$savedYear-${savedMonth+1}-$savedDay")
    }
}