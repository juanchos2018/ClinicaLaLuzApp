package com.clinicalaluz.clinicaapp

import android.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clinicalaluz.clinicaapp.databinding.ActivityMenuBinding
import com.facebook.AccessToken
import com.facebook.Profile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.squareup.picasso.Picasso


class MenuActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMenuBinding

    @JvmField
    var DNI_PACIENTE ="a"
    lateinit var TipoSesion:String
    lateinit  var mGoogleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = getSharedPreferences("datosuser", Context.MODE_PRIVATE)
        TipoSesion = intent.getSerializableExtra("TipoSesion").toString()

        if (TipoSesion == "gmail"){

            val dniuser = preferences.getString("dni", null)
            if (dniuser==null){
                val intent = Intent(this, RegisterGmailActivity::class.java)
                startActivity(intent)
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
                DNI_PACIENTE=dniuser
                val singleToneClass: SingleToneClass = SingleToneClass.instance
                singleToneClass.data=dniuser
                binding.tvnombreuser.text = nombres+""
                binding.imgperfil.setImageResource(com.clinicalaluz.clinicaapp.R.drawable.default_profile_image)
            }
        }
        else if (TipoSesion == "facebook"){
            if (AccessToken.getCurrentAccessToken() == null) {
                goLoginScreen()
            } else {
                val profile: Profile = Profile.getCurrentProfile()
                if (profile != null) {
                    displayProfileInfo(profile)
                } else {
                    Profile.fetchProfileForCurrentAccessToken()
                }
            }
        }
        binding.buttonHeart.setOnClickListener {
            val intent = Intent(this, PresionActivity::class.java)
            startActivity(intent)
        }
        binding.buttonSugar.setOnClickListener {
            val intent = Intent(this, GlucosaActivity::class.java)
            startActivity(intent)
        }
        binding.returnLogin.setOnClickListener {
            finish()
        }
        binding.btncerrarsesion.setOnClickListener {

            if (TipoSesion=="gmail"){
                mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, OnCompleteListener<Void?> {
                        Toast.makeText(applicationContext, "se ha salido de sesion", Toast.LENGTH_LONG).show()
                        finish()
                    })
            }else{

                finish()
            }

        }

        binding.buttonConsult.setOnClickListener {
            val intent = Intent(this@MenuActivity, SearchActivity::class.java)
            startActivity(intent)
        }

    }
    private fun displayProfileInfo(profile: Profile) {
        val id = profile.id
        val name = profile.name
        val photoUrl = profile.getProfilePictureUri(100, 100).toString()

        Picasso.with(this)
            .load(photoUrl)
            .into(binding.imgperfil)
        binding.tvnombreuser.setText(name+"")

    }

    private fun goLoginScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}

class SingleToneClass public constructor() {
    var data: String? = null
    companion object {
        val instance = SingleToneClass()
    }
}