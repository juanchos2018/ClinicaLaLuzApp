package com.clinicalaluz.clinicaapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.databinding.ActivityMenuNuevoBinding
import com.clinicalaluz.clinicaapp.databinding.ActivityPerfilBinding
import com.facebook.AccessToken
import com.facebook.Profile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class PerfilActivity : AppCompatActivity() {

    lateinit  var mGoogleSignInClient :GoogleSignInClient

    lateinit var TipoSesion:String
    var DNI_PACIENTE=""

    private lateinit var binding: ActivityPerfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         //setContentView(R.layout.activity_perfil)
          binding = ActivityPerfilBinding.inflate(layoutInflater)
          setContentView(binding.root)

        TipoSesion = intent.getSerializableExtra("TipoSesion").toString()

        val preferences = getSharedPreferences("datosuser", Context.MODE_PRIVATE)
        var NUM_DOC_IDENTIDAD = preferences.getString("NUM_DOC_IDENTIDAD", null).toString()
        DatosUser(NUM_DOC_IDENTIDAD)

        if (TipoSesion.equals("gmail")){
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
                val personPhoto: Uri = acct.photoUrl

                val imgperfil: ImageView = findViewById(R.id.imgperfil)
               // val nombres: TextView = findViewById(R.id.tvnombres)
              //  nombres.setText(personName+"")
                binding.tvnombreuser2.text = personName
                binding.tvcorreo2.text = personEmail

                Picasso.with(this)
                    .load(personPhoto.toString())
                    .into(imgperfil)
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
        }else{
            val dniuser = preferences.getString("NUM_DOC_IDENTIDAD", null)
            if (dniuser!=null){

                var nombres =preferences.getString("nombres",null)
                var apellidos =preferences.getString("apellidos",null)
                DNI_PACIENTE=dniuser
                val singleToneClass: SingleToneClass = SingleToneClass.instance
                singleToneClass.data=dniuser
                binding.tvnombreuser2.text = nombres
                binding.imgperfil.setImageResource(com.clinicalaluz.clinicaapp.R.drawable.default_profile_image)
            }
        }

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
      //  binding.tvnombreuser.setText(name+"")
      ///  binding.tvcorreo.text=correo
    }

    private fun goLoginScreen() {
        TODO("Not yet implemented")
    }

    private  fun DatosUser(dni:String){
        val url = "http://161.132.198.52:8080/app_laluz/pdoSelectDni.php?doc=$dni"
        val rq = Volley.newRequestQueue(this)
        val jst = JsonObjectRequest(
            Request.Method.GET, url,null,
            { response: JSONObject ->
                val json = response.optJSONArray("usuario")
                Log.e("respsonse ", json.toString())
          //      progres.dismiss()
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = json.getJSONObject(0)
                    var existe =jsonObject.optString("existe")

                    if (existe.equals("existe")){

                        var  nombres =jsonObject.optString("NOM_PACIENTE")
                        var  apellidoPa =jsonObject.optString("APE_PATERNO")
                        var  apellidoMa =jsonObject.optString("APE_MATERNO")
                        var  doc =jsonObject.optString("NUM_HC")

                        var  genero =jsonObject.optString("DES_GENERO")

                        if (genero=="FE"){
                            binding.imgsexo.setImageResource(R.drawable.famale)
                        }else if (genero=="MA"){
                            binding.imgsexo.setImageResource(R.drawable.malepn)
                        }else{
                            binding.imgsexo.setImageResource(R.drawable.ic_null)
                        }
                      //imgsexo
                        binding.tvnombres.setText(nombres)
                        binding.tvapellidoPa.setText(apellidoPa)
                        binding.tvapellidoMa.setText(apellidoMa)
                        binding.tvdocumento.setText(doc)

                       // var  estado =jsonObject.optString("ESTADO")
                      //  var  correo =jsonObject.optString("CORREO")

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                  //  progres.dismiss()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                  //  Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
        rq.add(jst)
    }
}