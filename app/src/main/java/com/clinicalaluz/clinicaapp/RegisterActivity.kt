package com.clinicalaluz.clinicaapp

import android.R
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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

        when {
            documento.length in 9..7 -> {
                Toast.makeText(applicationContext ,"El Numero de Documento con 8 digitos" , Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(documento) -> {
                Toast.makeText(applicationContext ,"Escribir Documento" , Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(nombres) -> {
                Toast.makeText(applicationContext ,"Escribir Nombres" , Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(apellidos) -> {
                Toast.makeText(applicationContext ,"Escribir Apellidos" , Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(clave) -> {
                Toast.makeText(applicationContext ,"Escribir Clave" , Toast.LENGTH_SHORT).show()
            }
            else -> {
                val url = "http://161.132.198.52:8080/app_laluz/pdoRegister.php?"
                val stringRequest = object : StringRequest(
                    Request.Method.POST, url, Response.Listener { response ->
                        try {
                            val obj = (response)
                            Log.e("resultadop",response.toString())
                            if (response.toString().trim()== "Enviado"){
                                men("Se ha enviando un mensaje a su correo","Verifica tu bandeja de span")
                            }else if(response.toString().trim()== "Existe"){
                                warning("Dni ya se encuentra registrado")
                            }else{
                                 Toast.makeText(applicationContext ,""+response.toString()+"" , Toast.LENGTH_SHORT).show()
                            }
                           //Toast.makeText(applicationContext ,""+response.toString()+"" , Toast.LENGTH_SHORT).show()

                        } catch (e: JSONException) {
                            Toast.makeText(applicationContext, ""+response.toString()+"", Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                    },
                    object : Response.ErrorListener {
                        override fun onErrorResponse(volleyError: VolleyError) {
                            Toast.makeText(applicationContext, "error:"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
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