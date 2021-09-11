package com.clinicalaluz.clinicaapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
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
import com.clinicalaluz.clinicaapp.databinding.ActivityMenuNuevoBinding
import com.facebook.AccessToken
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class MenuNuevoActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMenuNuevoBinding
    var DNI_PACIENTE=""
    var TipoSesion=""
    lateinit  var mGoogleSignInClient : GoogleSignInClient
    lateinit var token:String
    lateinit var  TipoSE:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_menu_nuevo)
        binding = ActivityMenuNuevoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val preferences = getSharedPreferences("datosuser", Context.MODE_PRIVATE)
        TipoSE = preferences.getString("logueo", null).toString()
        TipoSesion = intent.getSerializableExtra("TipoSesion").toString()
        token=intent.getSerializableExtra("token").toString()


        if (TipoSesion == "gmail"){
            val dniuser = preferences.getString("dni", null)
            if (dniuser==null){
                val intent = Intent(this, RegisterGmailActivity::class.java)
                startActivity(intent)
            }else
            {
                DNI_PACIENTE=dniuser
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
                if (acct.photoUrl!=null){
                    val personPhoto: Uri = acct.photoUrl
                    Picasso.with(this)
                        .load(personPhoto.toString())
                        .into(binding.imgperfil)
                }
                else{
                    binding.imgperfil.setImageResource(com.clinicalaluz.clinicaapp.R.drawable.default_profile_image)
                }
                binding.tvnombreuser.text=personName
                binding.tvcorreo.text=personEmail
            }
        }
        else if (TipoSesion == "dni"){
            val dniuser = preferences.getString("dni", null)
            if (dniuser!=null){

                var nombres =preferences.getString("nombres",null)
                var apellidos =preferences.getString("apellidos",null)
                DNI_PACIENTE=dniuser
                val singleToneClass: SingleToneClass = SingleToneClass.instance
                singleToneClass.data=dniuser
                binding.tvnombreuser.text = nombres+ " "+apellidos
                binding.imgperfil.setImageResource(com.clinicalaluz.clinicaapp.R.drawable.default_profile_image)
            }
        }

        else if (TipoSesion == "facebook"){
            if (AccessToken.getCurrentAccessToken() == null) {
                goLoginScreen()
            } else {
                //var profile: Profile = Profile.getCurrentProfile()
                var dniuser = preferences.getString("dni", null)
                if (dniuser==null){
                    val intent = Intent(this, RegisterFacebookActivity::class.java)
                    startActivity(intent)
                    // Toast.makeText(applicationContext, "facebook nulo", Toast.LENGTH_LONG).show()
                }else{
                    DNI_PACIENTE=dniuser
                }

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
        }

        //botones  de cerrar sesion

        binding.btncerrarsesion.setOnClickListener {
            if (TipoSesion=="gmail"){
                mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, OnCompleteListener<Void?> {
                        val preferences = getSharedPreferences("datosuser", MODE_PRIVATE)
                        preferences.edit().remove("dni").commit()
                       // Toast.makeText(applicationContext, "se ha salido de sesion", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    })
            }else if(TipoSesion=="facebook"){
                LoginManager.getInstance().logOut()
                //closeAndClearTokenInformation()
                //goLoginScreen()
                finish()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            else {
                val preferences = getSharedPreferences("datosuser", MODE_PRIVATE)
                preferences.edit().remove("dni").commit()
                finish()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        binding.buttonHeart.setOnClickListener {
            var actividad="PresionActivity"
            val dniuserpac = preferences.getString("dni", null)
            if (TextUtils.isEmpty(dniuserpac)){
                if (TipoSE=="gmail"){
                    warningDatos("Registre sus Datos","RegisterGmailActivity")
                }else if(TipoSE=="dni"){
                    warningDatos("Registre sus Datos","RegisterGmailActivity")
                }else if (TipoSE=="facebook"){
                    warningDatos("Registre sus Datos","RegisterFacebookActivity")
                }
            }else{
                veririqueCheck(dniuserpac.toString(),actividad)
            }
            //Toast.makeText(this, TipoSesion, Toast.LENGTH_SHORT).show()
        }
        binding.buttonSugar.setOnClickListener {

            val dniuserpac = preferences.getString("dni", null)
            if (TextUtils.isEmpty(dniuserpac)){
                if (TipoSE=="gmail"){
                    warningDatos("Registre sus Datos","RegisterGmailActivity")
                }else if(TipoSE=="dni"){
                    warningDatos("Registre sus Datos","RegisterGmailActivity")
                }else if (TipoSE=="facebook"){
                    warningDatos("Registre sus Datos","RegisterFacebookActivity")
                }

            }else{
                var actividad="GlucosaActivity"
                veririqueCheck(dniuserpac.toString(),actividad)
            }
          //  Toast.makeText(this, TipoSesion, Toast.LENGTH_SHORT).show()
        }
        binding.buttonConsult.setOnClickListener {
            val intent = Intent(this@MenuNuevoActivity, SearchActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        //codigo adicional
        finish()
    }
    private fun goLoginScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun displayProfileInfo(profile: Profile) {
        val id = profile.id
        val name = profile.name
        val preferences = getSharedPreferences("datosuser", Context.MODE_PRIVATE)
        var correo = preferences.getString("correo", null).toString()

        try {
            val photoUrl = profile.getProfilePictureUri(100, 100).toString()
            if (photoUrl!=null){
                Picasso.with(this)
                    .load(photoUrl)
                    .into(binding.imgperfil)
            }
            else{
                binding.imgperfil.setImageResource(com.clinicalaluz.clinicaapp.R.drawable.default_profile_image)
            }
        } catch (e: ArithmeticException) {
            //handler
        } finally {
            //optional finally block
        }
        binding.tvnombreuser.setText(name+"")
        binding.tvcorreo.text=correo

    }

    private  fun veririqueCheck(doc:String,actividad:String){
        val url = "http://161.132.198.52:8080/app_laluz/consultarEstado.php?doc=$doc"
        val rq = Volley.newRequestQueue(this)
        val jst = JsonObjectRequest(
            Request.Method.GET, url,null,
            { response: JSONObject ->
                val json = response.optJSONArray("usuario")
                //  progres.dismiss()
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = json.getJSONObject(0)
                    var existe =jsonObject.optString("existe")
                    if (existe.equals("existe")){
                        var estado =jsonObject.optString("ESTADO")
                        if (estado.toString().trim()=="1"){
                            if(actividad=="PresionActivity"){
                                val intent = Intent(this, PresionActivity::class.java)
                                startActivity(intent)
                            }else if(actividad=="GlucosaActivity"){
                                val intent = Intent(this, GlucosaActivity::class.java)
                                startActivity(intent)
                            }
                        }else{
                            warning("Comuniquese con la clinica para activar su cuenta")
                        }
                    }else{
                        Toast.makeText(this, "No existe su Dni", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // progres.dismiss()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    // progres.dismiss()
                    Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
        rq.add(jst)
    }
    private fun warningDatos(mensaje: String,actividad: String){
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
        btcerrrar.text="Registrar"
        val alert = dialogBuilder.create()
        btcerrrar.setOnClickListener {

            if (TipoSE=="gmail"){
                val intent = Intent(this, RegisterGmailActivity::class.java)
                startActivity(intent)
            }else if(TipoSE=="dni"){
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }else if (TipoSE=="facebook"){
                val intent = Intent(this, RegisterFacebookActivity::class.java)
                startActivity(intent)
            }
            alert.dismiss()
            //  val intent = Intent(this, RegisterFacebookActivity::class.java)
            // startActivity(intent)
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
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
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }
}