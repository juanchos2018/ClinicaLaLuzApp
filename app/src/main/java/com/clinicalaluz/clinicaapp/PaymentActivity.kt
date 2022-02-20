package com.clinicalaluz.clinicaapp

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity


class PaymentActivity : AppCompatActivity() {

    var total=""
    var id_pedido=""
    var TIP_COMPROBANTE=""
    var COD_CLIENTE=""
    var COD_AUXILIAR=""
    var COD_SUCURSAL=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        total = intent.getSerializableExtra("total").toString()
        id_pedido = intent.getSerializableExtra("id_pedido").toString()
        TIP_COMPROBANTE= intent.getSerializableExtra("TIP_COMPROBANTE").toString()
        COD_CLIENTE= intent.getSerializableExtra("COD_CLIENTE").toString()
        COD_AUXILIAR= intent.getSerializableExtra("COD_AUXILIAR").toString()
        COD_SUCURSAL= intent.getSerializableExtra("COD_SUCURSAL").toString()

        //IP_COMPROBANTE="boleta"


        val myWebView: WebView = findViewById(R.id.webview)
//        myWebView.setWebViewClient(object : WebViewClient() {
//            override fun onPageFinished(view: WebView, url: String) {//
//            }
//        })
        myWebView.settings.javaScriptEnabled = true
        var url="http://161.132.198.52:8080/app_laluz/pasarela/popin.php?total=$total&id_pedido=$id_pedido&tipo_comprobante=$TIP_COMPROBANTE&cod_cliente=$COD_CLIENTE&cod_auxilar=$COD_AUXILIAR&cod_sucursal=$COD_SUCURSAL"

        myWebView.loadUrl(url)
        myWebView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url != null) view.loadUrl(url)
                return true
            }
        })
        myWebView.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                var  progressBar: ProgressBar =findViewById(R.id.simpleProgressBar3)
                if (newProgress == 100) {
                     progressBar.setVisibility(View.GONE)
                } else {
                     progressBar.setVisibility(View.VISIBLE)
                    // progressBar.setProgress(newProgress)
                }
            }
        })
      //  myWebView.loadUrl("http://161.132.198.52:8080/app_laluz/pasarela/popin.php?total=$total&id_pedido=$id_pedido&tipo_comprobante=$TIP_COMPROBANTE&cod_cliente=$COD_CLIENTE&cod_auxilar=$COD_AUXILIAR")
    }
}