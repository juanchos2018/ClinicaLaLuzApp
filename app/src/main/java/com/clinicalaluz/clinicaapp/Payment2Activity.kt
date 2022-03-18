package com.clinicalaluz.clinicaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Payment2Activity : AppCompatActivity() {

    var total=""
    var id_pedido=""
    var TIP_COMPROBANTE=""
    var COD_CLIENTE=""
    var COD_AUXILIAR=""
    var COD_SUCURSAL=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment2)
    }
}