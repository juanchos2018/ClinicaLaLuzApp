package com.clinicalaluz.clinicaapp

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.squareup.picasso.Picasso
import java.util.*


class PerfilActivity : AppCompatActivity() {

    lateinit  var mGoogleSignInClient :GoogleSignInClient

    lateinit var TipoSesion:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        TipoSesion = intent.getSerializableExtra("TipoSesion").toString()

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
                val nombres: TextView = findViewById(R.id.txtnombres)
                nombres.setText(personName+"")

                Picasso.with(this)
                    .load(personPhoto.toString())
                    .into(imgperfil)
            }

        }


        val salir :Button =findViewById(R.id.sigOut)
        salir.setOnClickListener {
            mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, OnCompleteListener<Void?> {
                    Toast.makeText(applicationContext, "se ha salido de sesion", Toast.LENGTH_LONG).show()
                    finish()
                })
        }


    }
}