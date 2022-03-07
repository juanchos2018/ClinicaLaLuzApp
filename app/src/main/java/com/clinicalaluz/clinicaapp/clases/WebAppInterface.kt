package com.clinicalaluz.clinicaapp.clases

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.webkit.JavascriptInterface
import com.clinicalaluz.clinicaapp.MisCitasActivity

class WebAppInterface internal constructor(var context: Context) {
    @JavascriptInterface
    fun showAndroidToast(message: String?) {
       // Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        val intent = Intent(context, MisCitasActivity::class.java)
        intent.putExtra("TipoSesion","dni")
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK or  Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

    }
}