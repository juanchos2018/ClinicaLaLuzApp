package com.clinicalaluz.clinicaapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.clinicalaluz.clinicaapp.databinding.ActivityRegisterFacebookBinding
import com.facebook.AccessToken
import com.facebook.Profile
import org.json.JSONException
import java.util.*

class RegisterFacebookActivity : AppCompatActivity() , DatePickerDialog.OnDateSetListener{

    private var day = 0
    private var month = 0
    private var year = 0

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var fechasendpost=""


    private  lateinit var binding:ActivityRegisterFacebookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_register_facebook)
        binding = ActivityRegisterFacebookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pickDate()
        // val preferences = getSharedPreferences("datosuser", Context.MODE_PRIVATE)
        if (AccessToken.getCurrentAccessToken() == null) {
            goLoginScreen()
        } else {
            //var profile: Profile = Profile.getCurrentProfile()
            val handler = Handler()
            handler.postDelayed({
                var perfil = Profile.getCurrentProfile()
                if (perfil != null) {
                    displayProfileInfo(perfil)
                } else {
                    Profile.fetchProfileForCurrentAccessToken()
                }
            }, 2000)

        }
        binding.buttonRegCreate2.setOnClickListener {
            sendPost()
        }
        binding.returnRegister.setOnClickListener { finish() }
    }


    private fun displayProfileInfo(profile: Profile) {
        val id = profile.id
        val name = profile.name
        val arrayNombres = name!!.split(" ").toTypedArray()
        if (arrayNombres.size==4){
            var primerN = arrayNombres[0]
            var seconN  = arrayNombres[1]
            var apelliP = arrayNombres[2]
            var apelliM = arrayNombres[3]
            binding.regName2.setText(primerN+" " +seconN)
            binding.regLastname2.setText(apelliP+" " +apelliM)
        }
        else if (arrayNombres.size==3){
            var primerN = arrayNombres[0]
            var seconN  = arrayNombres[1]
            var apelliP = arrayNombres[2]
            binding.regName2.setText(primerN+" " +seconN)
            binding.regLastname2.setText(apelliP)
        }
        else if (arrayNombres.size==2){
            var primerN = arrayNombres[0]
            var seconN  = arrayNombres[1]
            binding.regName2.setText(primerN+" " +seconN)
        }else {
            binding.regName2.setText(name+"")
        }
    }
    fun sendPost() {
        var documento  =  binding.regDocIdentity2.text.toString()
        var nombres  =  binding.regName2.text.toString()
        var apellidos  =  binding.regLastname2.text.toString()
        var fecha  =  binding.regDate2.text.toString()
        var celular  =  binding.regPhone2.text.toString()
        var email  =  binding.regEmail2.text.toString()

        if (documento.length<=7 || documento.length>=9){
            binding.regDocIdentity2.setError("El Numero de Documento con 8 digitos")
        }
        else if ( TextUtils.isEmpty(documento))
        {
            binding.regDocIdentity2.setError("Escribir Documento")
        }else if (TextUtils.isEmpty(nombres)){
            binding.regName2.setError("Escribir Nombres")
        }
        else if (TextUtils.isEmpty(apellidos)){
            binding.regLastname2.setError("Escribir Apellidos")
        }
        else if (TextUtils.isEmpty(email)){
            binding.regEmail2.setError("Escribir Correo")
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.regEmail2.setError("Coreo no valido")
        }
        else{
            val url = "http://161.132.198.52:8080/app_laluz/pdoRegister.php?"
            val stringRequest = object : StringRequest(
                Request.Method.POST, url, Response.Listener { response ->
                    try {
                        if (response.toString().trim() == "Enviado"){
                            val preferences: SharedPreferences = this@RegisterFacebookActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
                            val editor = preferences.edit()
                            editor.putString("nombres", nombres)
                            editor.putString("dni", documento)
                            editor.putString("correo", email)
                            editor.putString("logueo", "facebook")
                            editor.commit()

                            val singleToneClass: SingleToneClass = SingleToneClass.instance
                            singleToneClass.data=documento
                            men("Registrado","Comuniquese con la clinica para activar su Cuenta")

                        }else if(response.toString().trim() == "Existe"){
                            warning("Dni ya se encuentra registrado")
                            //dialogpdate("Su Dni ya existe","Desea actualizar su Correo "+ email,email,documento,nombres)
                        }else if(response.toString().trim()=="ExisteCorreo")    {
                            Toast.makeText(applicationContext ,"Su Correo ya Existe" , Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(applicationContext ,"E: "+response.toString()+"" , Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(applicationContext, "C: "+response.toString()+"", Toast.LENGTH_LONG).show()
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
                    params.put("logueo","facebook")
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
    private fun men(mensaje: String,descrpcion:String){
        val dialogBuilder = AlertDialog.Builder(this)
        val btcerrrar: Button
        val tvmensaje: TextView
        var tvedsderpcion : TextView
        val  v = LayoutInflater.from(applicationContext).inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_correo, null)
        val animationView: LottieAnimationView = v.findViewById(com.clinicalaluz.clinicaapp.R.id.animation_viewcheck)
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

    private fun goLoginScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    private fun pickDate(){
        binding.regDate2.setOnClickListener {
            getDateCalendar()
            DatePickerDialog(this, this, year, month, day).show()
            binding.regDate2.setTextColor(resources.getColor(R.color.black))
        }
    }
    private fun getDateCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year
        getDateCalendar()
        binding.regDate2.text = "$savedDay/${savedMonth+1}/$savedYear"
        fechasendpost= String.format("$savedYear-${savedMonth+1}-$savedDay")
    }

}