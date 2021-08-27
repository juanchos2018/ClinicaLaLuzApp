package com.clinicalaluz.clinicaapp

import android.R
import android.R.string
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
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.clinicalaluz.clinicaapp.databinding.ActivityRegisterGmailBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.json.JSONException
import java.util.*


class RegisterGmailActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var day = 0
    private var month = 0
    private var year = 0

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var fechasendpost=""
    //private lateinit var binding : ActivityRegisterBinding

    private  lateinit var binding: ActivityRegisterGmailBinding
    lateinit  var mGoogleSignInClient : GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_register_gmail)
        binding = ActivityRegisterGmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pickDate()

        binding.buttonRegCreate.setOnClickListener {
            sendPost()
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            binding.regEmail.setText(personEmail)
            val arrayNombres = personName!!.split(" ").toTypedArray()
            if (arrayNombres.size==4){
               var primerN = arrayNombres[0]
               var seconN  = arrayNombres[1]
               var apelliP = arrayNombres[2]
               var apelliM = arrayNombres[3]
                binding.regName.setText(primerN+" " +seconN)
                binding.regLastname.setText(apelliP+" " +apelliM)
            }
            else if (arrayNombres.size==3){
                var primerN = arrayNombres[0]
                var seconN  = arrayNombres[1]
                var apelliP = arrayNombres[2]
                binding.regName.setText(primerN+" " +seconN)
                binding.regLastname.setText(apelliP)
            }
            else if (arrayNombres.size==2){
                var primerN = arrayNombres[0]
                var seconN  = arrayNombres[1]
                binding.regName.setText(primerN+" " +seconN)
            }else {
                binding.regName.setText(personName+"")
            }
          //  Toast.makeText(applicationContext, arrayNombres.size.toString(), Toast.LENGTH_LONG).show()
            //for (item in arrayNombres)
               // println(item)
        }

        binding.returnRegister.setOnClickListener { finish() }

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


    fun sendPost() {
        var documento  =  binding.regDocIdentity.text.toString()
        var nombres  =  binding.regName.text.toString()
        var apellidos  =  binding.regLastname.text.toString()
        var fecha  =  binding.regDate.text.toString()
        var celular  =  binding.regPhone.text.toString()
        var email  =  binding.regEmail.text.toString()

        if (documento.length<=7 || documento.length>=9){
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
      else{
            val url = "http://161.132.198.52:8080/app_laluz/pdoRegister.php?"
            val stringRequest = object : StringRequest(
                Request.Method.POST, url, Response.Listener { response ->
                    try {
                        if (response.toString().trim() == "Enviado"){
                            val preferences: SharedPreferences = this@RegisterGmailActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
                            val editor = preferences.edit()
                            editor.putString("nombres", nombres)
                            editor.putString("dni", documento)
                            editor.putString("correo", email)
                            editor.putString("logueo", "gmail")
                            editor.commit()
                            men("Registrado","Comuniquese con la clinica para activar su Cuenta")
                            val singleToneClass: SingleToneClass = SingleToneClass.instance
                            singleToneClass.data=documento

                        }else if(response.toString().trim() == "Existe"){
                           // warning("Dni ya se encuentra registrado")
                            dialogpdate("Su Dni ya existe","Desea actualizar su Correo "+ email,email,documento,nombres)

                        }else{
                            Toast.makeText(applicationContext ,""+response.toString()+"" , Toast.LENGTH_SHORT).show()
                        }
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
                    params.put("clave","")
                    params.put("logueo","gmail")
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

    private fun dialogpdate(mensaje: String,descrpcion:String,correo:String,cod:String,nombre:String){
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
            UpodateInfo(nombre,cod,correo )
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

    private  fun  UpodateInfo(nombres:String, documento:String, email:String){
        val url = "http://161.132.198.52:8080/app_laluz/pdoActualizarDatos.php?"
        var progres =ProgressDialog(this)
        progres.setMessage("Cargando")
        progres.show()

        val stringRequest = object : StringRequest(
            Request.Method.POST, url, Response.Listener { response ->
                try {
                    if (response.toString().trim() == "Enviado"){
                        val preferences: SharedPreferences = this@RegisterGmailActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
                        val editor = preferences.edit()
                        editor.putString("nombres", nombres)
                        editor.putString("dni", documento)
                        editor.putString("correo", email)
                        editor.commit()
                     //   men("Actualizado","Se ha Actualizado su correo")
                        men("Actualizado","Comuniquese con la clinica para activar su Cuenta")
                        val singleToneClass: SingleToneClass = SingleToneClass.instance
                        singleToneClass.data=documento

                    }else if(response.toString().trim() == "Existe"){
                        warning("Dni ya se encuentra registrado")
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
                params.put("nombres",nombres)
                params.put("fecha",fechasendpost)
                params.put("email",email)
                params.put("clave","")
                params.put("logueo","gmail")
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

    private fun warning(mensaje: String){
        val dialogBuilder = AlertDialog.Builder(this)
        val btcerrrar: Button
        val tvmensaje: TextView
        val  v = LayoutInflater.from(applicationContext).inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_warning, null)
        val animationView: LottieAnimationView = v.findViewById(com.clinicalaluz.clinicaapp.R.id.animation_viewcheck)
        animationView.playAnimation()
        dialogBuilder.setView(v)
        btcerrrar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.idbtncerrardialogo) as Button
        tvmensaje = v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvmensaje)
        tvmensaje.text = mensaje
        val alert = dialogBuilder.create()
        btcerrrar.setOnClickListener {
            alert.dismiss()
            finish()
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

    private fun pickDate(){
        binding.regDate.setOnClickListener {
            getDateCalendar()
            DatePickerDialog(this, this, year, month, day).show()
            binding.regDate.setTextColor(resources.getColor(R.color.black))
        }
    }
    private fun getDateCalendar(){
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
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