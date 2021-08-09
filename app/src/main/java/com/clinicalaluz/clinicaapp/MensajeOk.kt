package com.clinicalaluz.clinicaapp

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView

class MensajeOk {

    //var builder1: AlertDialog.Builder? = null
    //var alert1: AlertDialog? = null
    private val frames = 9
    private var currentAnimationFrame = 0
    private var animationView: LottieAnimationView? = null


    private fun mensaje(estado: String,context: Context) {
        var builder1: AlertDialog.Builder? = null
        var alert1: AlertDialog? = null
        builder1 = AlertDialog.Builder(context)
        val btcerrrar: Button
        val tvestado: TextView
        val  v = LayoutInflater.from(context).inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_correo, null)
        animationView = v.findViewById(com.clinicalaluz.clinicaapp.R.id.animation_viewcheck)
       //resetAnimationView()
       // animationView.playAnimation()
        builder1!!.setView(v)

        btcerrrar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.idbtncerrardialogo) as Button
        tvestado = v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvmensaje)
        tvestado.text = estado
        btcerrrar.setOnClickListener {
            Toast.makeText(context, "Cerrado", Toast.LENGTH_LONG).show()

        }
        alert1 = builder1!!.create()
        alert1.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert1.show()
    }





}