package com.clinicalaluz.clinicaapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.clinicalaluz.clinicaapp.clases.WebAppInterface


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



        val myWebView: WebView = findViewById(R.id.webview)
//        myWebView.setWebViewClient(object : WebViewClient() {
//            override fun onPageFinished(view: WebView, url: String) {//
//            }
//        })
        //myWebView.settings.javaScriptEnabled = true
        var url="http://161.132.198.52:8080/app_laluz/pasarela/popin.php?total=$total&id_pedido=$id_pedido&tipo_comprobante=$TIP_COMPROBANTE&cod_cliente=$COD_CLIENTE&cod_auxilar=$COD_AUXILIAR&cod_sucursal=$COD_SUCURSAL"

        val webSettings = myWebView.settings
        webSettings.javaScriptEnabled = true
        myWebView.addJavascriptInterface(WebAppInterface(this), "Android")
        myWebView.webViewClient =MyWebViewClient()
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
                }
            }
        })

        var imgbutton:ImageView=findViewById(R.id.imageViewplaces6)
        imgbutton.setOnClickListener{
            salir()
        }
      //  myWebView.loadUrl("http://161.132.198.52:8080/app_laluz/pasarela/popin.php?total=$total&id_pedido=$id_pedido&tipo_comprobante=$TIP_COMPROBANTE&cod_cliente=$COD_CLIENTE&cod_auxilar=$COD_AUXILIAR")
    }

    private fun salir() {
        finish()
        val intent = Intent(this, MisCitasActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or  Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    private class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            webView: WebView,
            webResourceRequest: WebResourceRequest
        ): Boolean {
            return super.shouldOverrideUrlLoading(webView, webResourceRequest)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        //codigo adicional
         salir()
    }

}