package com.clinicalaluz.clinicaapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clinicalaluz.clinicaapp.databinding.ActivityMainBinding
import com.facebook.login.LoginManager
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
        if (sharedPreferences.getInt("INTRO", 0) == 0) {
            LoginManager.getInstance().logOut()
         // Toast.makeText(this, "cerro session face", Toast.LENGTH_SHORT).show()
        }
        launchHomeScreen()
// generate key hash
//        try {
//            val info = packageManager.getPackageInfo(
//                "com.clinicalaluz.clinicaapp",  //Insert your own package name.
//                PackageManager.GET_SIGNATURES
//            )
//            for (signature in info.signatures) {
//                val md: MessageDigest = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                Log.d("KeyHash-one:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
//            }
//        } catch (e: PackageManager.NameNotFoundException) {
//        } catch (e: NoSuchAlgorithmException) {
//        }
//
        binding.buttonConsult.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.buttonGo.setOnClickListener {
            val intentLogin = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intentLogin)
        }
    }

    private fun launchHomeScreen() {

        val sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
        val editor: SharedPreferences.Editor
        editor = sharedPreferences.edit()
        editor.putInt("INTRO", 1)
        editor.apply()

    }
}