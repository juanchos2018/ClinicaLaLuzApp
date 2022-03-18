package com.clinicalaluz.clinicaapp

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.clases.ClsTest
import com.clinicalaluz.clinicaapp.clases.TemplatePDF
import com.clinicalaluz.clinicaapp.databinding.ActivityPresionBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.tapadoo.alerter.Alerter
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class PresionActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var binding : ActivityPresionBinding
    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0

    private val MIS_PERMISOS = 100
    val c = Calendar.getInstance()
    private val CERO = "0"
    private val BARRA = "-"
    val mes: Int = c.get(Calendar.MONTH)
    val dia: Int = c.get(Calendar.DAY_OF_MONTH)
    val anio: Int = c.get(Calendar.YEAR)
    var id = Locale("in", "ID")
    var simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", id)

    private var fechasendpost=""
    var DniPaciente=""
    lateinit var template: TemplatePDF
    private val header = arrayOf("FECHA", "SISTOLICA", "DIASTOLICA","PULSO")
    private val shortext = "REPORTE DE PULSO"
    var DES_AUXILIAR=""
    var NUM_DOC_IDENTIDAD=""
    var listaTest =   ArrayList<ClsTest>()

    private  lateinit var alert : AlertDialog
    private  lateinit var alert2 : AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //CAPTURA INICIAL DE LA FECHA Y HORA
        dateTimeDefect()
        //SELECCION DE LA FECHA Y HORA
        pickDate()
        //PINTAR GRAFICO

        binding.savePresion.setOnClickListener {
           sendPost()
            //createMyPdf()
            ///genetatepdf()
        }
        //TERMINAR LA ACCION
        binding.preReturnMenu.setOnClickListener { finish() }

        val singleToneClass: SingleToneClass = SingleToneClass.instance
        val preferences = getSharedPreferences("datosuser", MODE_PRIVATE)
        DES_AUXILIAR = preferences.getString("DES_AUXILIAR", null).toString()
        var  COD_AUXILIAR = preferences.getString("COD_AUXILIAR", null).toString()
        NUM_DOC_IDENTIDAD = preferences.getString("NUM_DOC_IDENTIDAD", null).toString()
        var  COD_PACIENTE= preferences.getString("COD_PACIENTE", null).toString()
        var  COD_CLIENTE = preferences.getString("COD_CLIENTE", null).toString()


       DniPaciente =NUM_DOC_IDENTIDAD
       binding.newSistolica.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (binding.newSistolica.length()==3){
                binding.newDiastolica.requestFocus()
              }
        }
        override fun afterTextChanged(s: Editable) {
         }
        })
        binding.newDiastolica.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if ( binding.newDiastolica.length()==3){
                     binding.newPulso.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
        consultData()

        binding.preDate.setOnClickListener {
            fechados()
        }
        binding.btndialogo.setOnClickListener {
            abriridialogo()
        }
        if (solicitaPermisosVersionesSuperiores()){
        }


       // Toast.makeText(this,  System.currentTimeMillis().toString(), Toast.LENGTH_SHORT).show()
//        binding.lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener
//        {
//            override fun onValueSelected(e: Entry, h: Highlight?) {
//                val x = e.x.toString()
//                val y = e.y
//                val selectedXAxisCount = x.substringBefore(".")
//                val nonFloat=binding.lineChart.getXAxis().getValueFormatter().getFormattedValue(e.x)
//            }
//
//            override fun onNothingSelected() {}
//        })
//

       // binding.lineChart.setOnChartValueSelectedListener(this)

    }

//    override fun onNothingSelected() {
//        Log.i("Entry selected", "Nothing selected.")
//    }
//
//    override fun onValueSelected(e: Entry?, h: Highlight?) {
//        Log.i("Entry selected", e.toString())
//        val x:Float =e!!.x
//        val y:Float =e!!.y
//
//    }
    private fun abriridialogo() {

        val dialogBuilder = AlertDialog.Builder(this)
        val btngenerar: Button
        val tvmensaje: TextView
        val radiuouno : RadioButton
        val radiogrupoi: RadioGroup
        val layaout  :LinearLayout
        val btnfecha1:Button
        val btnfecha2:Button

        val c = Calendar.getInstance()
        val yyyy = c.get(Calendar.YEAR)
        val MM = c.get(Calendar.MONTH)
        val dd = c.get(Calendar.DAY_OF_MONTH)

        var fechasendpostDialogo= String.format("$yyyy-${MM+1}-$dd")
        var fechasendpostDialogo2= String.format("$yyyy-${MM+1}-$dd")
        var fechaText = String.format("$dd/0${MM+1}/$yyyy")
        var fechaText2 = String.format("$dd/0${MM+1}/$yyyy")

        val v = LayoutInflater.from(applicationContext)
            .inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_reporte, null)
        var tiporeporte=false
        dialogBuilder.setView(v)
        btngenerar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.btngenrarpdf) as Button
        radiogrupoi=v.findViewById(com.clinicalaluz.clinicaapp.R.id.grupocomprobante)
        layaout=v.findViewById(com.clinicalaluz.clinicaapp.R.id.layoutfechas)
        btnfecha1=v.findViewById(com.clinicalaluz.clinicaapp.R.id.btnfechainicio)
        btnfecha2=v.findViewById(com.clinicalaluz.clinicaapp.R.id.btnfechafin)

        radiuouno=v.findViewById(com.clinicalaluz.clinicaapp.R.id.rbdtodo)
        radiuouno.isChecked=true
        tiporeporte=false
        btnfecha1.text=fechaText
        btnfecha2.text=fechaText

        btnfecha1.setOnClickListener {
            val recogerFecha = DatePickerDialog(this,
                { view, year, month, dayOfMonth ->
                    c.set(year, month, dayOfMonth)
                    val c = Calendar.getInstance()
                    val yyyy = c.get(Calendar.YEAR)
                    val MM = c.get(Calendar.MONTH)
                    val dd = c.get(Calendar.DAY_OF_MONTH)
                    fechasendpostDialogo= String.format("$year-${month+1}-$dayOfMonth")
                    fechaText = String.format("$dayOfMonth/${month+1}/$year")
                    btnfecha1.text=fechaText
                }, yyyy, MM, dd
            )
          //  recogerFecha.getDatePicker().setMinDate(System.currentTimeMillis())
            recogerFecha.show()
        }
        btnfecha2.setOnClickListener {
            val recogerFecha = DatePickerDialog(this,
                { view, year, month, dayOfMonth ->
                    c.set(year, month, dayOfMonth)
                    val c = Calendar.getInstance()
                    val yyyy = c.get(Calendar.YEAR)
                    val MM = c.get(Calendar.MONTH)
                    val dd = c.get(Calendar.DAY_OF_MONTH)
                    fechasendpostDialogo2= String.format("$year-${month+1}-$dayOfMonth")
                    fechaText2 = String.format("$dayOfMonth/${month+1}/$year")
                    btnfecha2.text=fechaText2
                }, yyyy, MM, dd
            )
            //  recogerFecha.getDatePicker().setMinDate(System.currentTimeMillis())
            recogerFecha.show()
        }
        layaout.isVisible=false
        radiogrupoi.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId==R.id.rbdtodo){
                layaout.isVisible=false
                tiporeporte=false
            }else if (checkedId==R.id.rbdfechas){
                layaout.isVisible=true
                tiporeporte=true
            }
        }
        val alert = dialogBuilder.create()
        btngenerar.setOnClickListener {
            if (tiporeporte){
                if (true){
                }
            }else{
            }
            alert.dismiss()
            consultaReportes(tiporeporte,fechasendpostDialogo,fechasendpostDialogo2)
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

    private  fun consultaReportes(tipo:Boolean, fecha1:String,fecha2:String )
    {
        var tipos =""
        if (tipo){
            tipos ="fechasp"
        }else{
            tipos ="todop"
        }
        var result =when(tipo){
            true->"fechas"
            false->"todo"
        }
        val progress = ProgressBar(this)
        val progres = ProgressDialog(this)
        progres.setMessage("Cargando...")
        progres.show()
        val peticion = "/Controllers/UsuarioController.php?condicion=$tipos&documento=$DniPaciente&fecha1=$fecha1&fecha2=$fecha2"
        val rq = Volley.newRequestQueue(this)
        val arr = JsonArrayRequest(Request.Method.GET, getString(R.string.URL_BASE)+peticion, null,
            { response ->
                for(x in 0..response.length()-1){
                    val fecha = response.getJSONObject(x).getString("FECHA")
                    val REG_SISTOLICA = response.getJSONObject(x).getString("SISTOLICA")
                    val DIASTOLICA = response.getJSONObject(x).getString("DIASTOLICA")
                    val PULSO = response.getJSONObject(x).getString("PULSO")
                    var test =  ClsTest(fecha,REG_SISTOLICA,DIASTOLICA,PULSO)
                    listaTest.add(test)
                }
                if (response.length()==0){

                }else{

                 }
               // alert.dismiss()
                progres.dismiss()
                genetatepdf()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "ERROR: %s".format(error.toString()), Toast.LENGTH_SHORT).show()
                progres.dismiss()
            })
        var socketTimeout =30000
        var policy = DefaultRetryPolicy(socketTimeout,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        arr.setRetryPolicy(policy);
        rq.add(arr)
    }

    override fun onStart() {
        super.onStart()
       // dateTimeDefect()
    }
    fun sendPost() {
        if (TextUtils.isEmpty(binding.newPulso.text)){
            Toast.makeText(applicationContext, "LLenar el pulso", Toast.LENGTH_LONG).show()
        }else if (TextUtils.isEmpty(binding.newSistolica.text)){
            Toast.makeText(applicationContext, "LLenar el Sistolic", Toast.LENGTH_LONG).show()
        }
        else if (TextUtils.isEmpty(binding.newDiastolica.text)){
            Toast.makeText(applicationContext, "LLenar el Diastolica", Toast.LENGTH_LONG).show()
        }else{
            val preferences = getSharedPreferences("datosuser", Context.MODE_PRIVATE)
            var   dni = preferences.getString("NUM_DOC_IDENTIDAD", null)
            var   TipoSE = preferences.getString("logueo", null)
            if (dni==null){
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
            }else{
                //Toast.makeText(applicationContext, "entra al else", Toast.LENGTH_LONG).show()
                val newSistolica = (binding.newSistolica.text.toString()).toInt()
                val newDiastolica = (binding.newDiastolica.text.toString()).toInt()
                var newPulso =(binding.newPulso.text.toString()).toInt()
                val peticion = "/Controllers/UsuarioController.php"
                val stringRequest = object : StringRequest(Request.Method.POST, getString(R.string.URL_BASE)+peticion,
                    Response.Listener { response ->
                        try {
                            val jsonObject = JSONTokener(response).nextValue() as JSONObject
                            val message = jsonObject.getString("message")
                            if (message=="Success"){
                                consultData()
                                binding.newPulso.text.clear()
                                binding.newSistolica.text.clear()
                                binding.newDiastolica.text.clear()
                                simpleAlert("Mensaje","Registrado con exito")
                            }else{
                                Toast.makeText(applicationContext, "No Registrado", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(applicationContext, ""+response.toString()+"", Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                    },
                    object : Response.ErrorListener {
                        override fun onErrorResponse(volleyError: VolleyError) {
                            Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                        }
                    })
                   {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params.put("tipo","presion")
                        params.put("doc",DniPaciente)
                        params.put("fecha",fechasendpost)
                        params.put("sistolica",newSistolica.toString())
                        params.put("diastolica",newDiastolica.toString())
                        params.put("pulso",newPulso.toString())
                        return params
                    }
                }
                stringRequest.setRetryPolicy(
                    DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
                )
                stringRequest.setShouldCache(false)
                VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
            }
        }
    }
    private fun consultData(){
        val xvalues = ArrayList<String>()
        val listaPulso = ArrayList<Entry>()
        val listaSistolica = ArrayList<Entry>()
        val listaDiastolica = ArrayList<Entry>()
        var listaTest2 =   ArrayList<ClsTest>()
        val lineDataSeta = ArrayList<ILineDataSet>()
        val peticion = "/Controllers/UsuarioController.php?condicion=presion&doc=$DniPaciente"
        val rq = Volley.newRequestQueue(this)
        val arr = JsonArrayRequest(Request.Method.GET, getString(R.string.URL_BASE)+peticion, null,
            { response ->
                for(x in 0..response.length()-1){
                    val fecha = response.getJSONObject(x).getString("FECHA")
                    xvalues.add(fecha)

                    val REG_SISTOLICA = response.getJSONObject(x).getString("REG_PULSO_SISTOLICA")
                    val DIASTOLICA = response.getJSONObject(x).getString("REG_PULSO_DIASTOLICA")
                    val PULSO = response.getJSONObject(x).getString("REG_PULSO")

                    listaSistolica.add(Entry(REG_SISTOLICA.toFloat(), x))
                    listaDiastolica.add(Entry(DIASTOLICA.toFloat(), x))
                    listaPulso.add(Entry(PULSO.toFloat(), x))

                    var test =  ClsTest(fecha,REG_SISTOLICA,DIASTOLICA,PULSO)
                    listaTest2.add(test)
                }

                val linedataset2 = LineDataSet(listaSistolica, "SISTOLICA")
                val linedataset3 = LineDataSet(listaDiastolica, "DIASTOLICA")
                val linedataset = LineDataSet(listaPulso, "PULSO")

                linedataset2.color = resources.getColor(R.color.red)
                linedataset3.color = resources.getColor(R.color.purple_700)
                linedataset.color = resources.getColor(R.color.purple_200)

                lineDataSeta.add(linedataset2)
                lineDataSeta.add(linedataset3)
                lineDataSeta.add(linedataset)

                val data = LineData(xvalues, lineDataSeta)

                binding.lineChart.data = data
                binding.lineChart.setBackgroundColor(resources.getColor(R.color.white))
                binding.lineChart.animateXY(3000,3000)
                binding.lineChart.setOnChartValueSelectedListener(object :OnChartValueSelectedListener{
                    override fun onValueSelected(e: Entry, dataSetIndex: Int, h: Highlight?) =
                        try {
                            var valor1 =listaTest2.get(e.xIndex).SISTOLICA
                            var valor2 =listaTest2.get(e.xIndex).DIASTOLICA
                            var valor3 =listaTest2.get(e.xIndex).PULSO
                            var fechapulso =listaTest2.get(e.xIndex).FECHA
                            detallePulso("SISTOLICA " +valor1,"DIASTOLICA "+valor2,"PULSO "+valor3,fechapulso)

                        }catch (ex:java.lang.Exception){
                        }

                    override fun onNothingSelected() {
                    }

                })
//                binding.lineChart.isDrawingCacheEnabled=true
//                var bitmap: Bitmap
//                bitmap = Bitmap.createBitmap( binding.lineChart.getDrawingCache());
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "ERROR: %s".format(error.toString()), Toast.LENGTH_SHORT).show()
            })
        var socketTimeout =30000
        var policy = DefaultRetryPolicy(socketTimeout,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        arr.setRetryPolicy(policy);
        rq.add(arr)
    }



    private fun dateTimeDefect(){

        val c = Calendar.getInstance()
        val yyyy = c.get(Calendar.YEAR)
        val MM = c.get(Calendar.MONTH)
        val dd = c.get(Calendar.DAY_OF_MONTH)
        val hh = c.get(Calendar.HOUR_OF_DAY)
        val mm = c.get(Calendar.MINUTE)
        val xx:String

        val diaFormateado =
            if (dd < 10) CERO + dd.toString() else dd.toString()
        val mesFormateado =
            if (MM < 10) CERO + MM.toString() else MM.toString()

        if(c.get(Calendar.AM_PM) == Calendar.AM){
            xx = "am"
            //binding.preDate.text = String.format("$dd-${MM+1}-$yyyy $hh:$mm $xx")
            fechasendpost= String.format("$yyyy-${MM+1}-$dd $hh:$mm $xx")
            //binding.preDate.text =   diaFormateado + BARRA + mesFormateado + BARRA + yyyy+" "+ hh+":"+mm+" "+xx
          //  binding.preDate.text =   String.format("$yyyy -${MM+1}-$dd $hh:$mm $xx")

            var len  =MM.toString().length
            if (len==1){
                binding.preDate.text = String.format("$dd/0${MM+1}/$yyyy $hh:$mm $xx")
                fechasendpost= String.format("$yyyy-0${MM+1}-$dd $hh:$mm")
            }else{
                binding.preDate.text = String.format("$dd/${MM+1}/$yyyy $hh:$mm $xx")
                fechasendpost= String.format("$yyyy-${MM+1}-$dd $hh:$mm ")
            }
            //Toast.makeText(this, "AM", Toast.LENGTH_SHORT).show()


        }else if(c.get(Calendar.AM_PM) == Calendar.PM){
            xx = "pm"
           //  binding.preDate.text = String.format("$dd-${MM+1}-$yyyy $hh:$mm $xx")
           // binding.preDate.text =   diaFormateado + BARRA + mesFormateado + BARRA + yyyy+" " +hh+":"+mm+" "+xx
           //    binding.preDate.text =   String.format("$yyyy -${MM+1}-$dd $hh:$mm $xx")

            var len  =MM.toString().length
            if (len==1){
                binding.preDate.text = String.format("$dd/0${MM+1}/$yyyy $hh:$mm $xx")
                fechasendpost= String.format("$yyyy-0${MM+1}-$dd $hh:$mm")
            }else{

                binding.preDate.text = String.format("$dd/${MM+1}/$yyyy $hh:$mm $xx")
                fechasendpost= String.format("$yyyy-${MM+1}-$dd $hh:$mm")
            }
           // Toast.makeText(this, "pm", Toast.LENGTH_SHORT).show()
        }
    }
    private fun createMyPdf(){
        var  myPdfDodument =PdfDocument()

        var myPageInfo = PdfDocument.PageInfo.Builder(300,600,1).create()
        var myPage =myPdfDodument.startPage(myPageInfo)
        var myPaint = Paint()
        var myString ="mensaje de pdf"
        var x =10
        var y =20
        myPage.canvas.drawText(myString,x.toFloat(),y.toFloat(),myPaint)
        myPdfDodument.finishPage(myPage)

        var myFilePath2=this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        var myFilePath3=Environment.getExternalStorageState()  +"/Download/mypdf4.pdf"
        var myFilePath4=Environment.getExternalStorageState()  +"/Download/mypdf8.pdf"
        val mFilePath = File(myFilePath4, "mypdf2.pdf")

        val file = File(Environment.getExternalStorageDirectory(), "GFG.pdf")
        var myFile=File(myFilePath4)
        try {
                myPdfDodument.writeTo(FileOutputStream(myFile))

        }catch (ex:Exception){
            Toast.makeText(applicationContext, ex.toString(), Toast.LENGTH_LONG).show()
        }
        myPdfDodument.close()
       // Toast.makeText(applicationContext, "creado="+myFilePath4, Toast.LENGTH_LONG).show()

    }

    private  fun genetatepdf(){
        template =TemplatePDF(this)
        template.openDocument()
        template.imgeset()
        template.addMetaData("clientes","ventas","pepe")
        template.addTitles("tinasdfsd","clisntes","fecha")
        template.addParagraph(shortext)
        template.addParagraph(DES_AUXILIAR)
        template.createTable(header,listaTest)
        template.closeDocument()
        simpleAlert("Mensaje","Reporte Creado")
    }
    private fun getDateTimeCalendar(){
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
        //cal.setMinDate(System.currentTimeMillis())
    }
    private fun pickDate(){
        binding.preDate.setOnClickListener {
           // getDateTimeCalendar()
            //DatePickerDialog(this, this, year, month, day).show()
        }
    }
    fun simpleAlert(title:String,subtile:String) {
        Alerter.create(this@PresionActivity)
            .setTitle(title)
            .setText(subtile)
            .setIcon(R.drawable.ic_check)
            .setBackgroundColorRes(R.color.verde)
            .setIconColorFilter(0) // Optional - Removes white tint
            .show()
    }


    private  fun detallePulso(sistolica:String,diastolica:String,puslo:String,fechapulso:String){

        val dialogBuilder = AlertDialog.Builder(this)
        val btncerrar: Button
        val tvsistolica: TextView
        val tvdiastolica : TextView
        val tvpuslso: TextView
        val tvfecha  :TextView
        val tvnombreuser  :TextView

        val v = LayoutInflater.from(applicationContext)
            .inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_detalle, null)

        dialogBuilder.setView(v)
        btncerrar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.idbtncerrardialogo3) as Button
        tvsistolica=v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvsistolica)
        tvdiastolica=v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvdiastolica)
        tvpuslso=v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvpulso)
        tvfecha=v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvfecha)
        tvnombreuser=v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvnombreuser)

        tvsistolica.text=sistolica
        tvdiastolica.text=diastolica
        tvpuslso.text=puslo
        tvfecha.text=fechapulso
        tvnombreuser.text=DES_AUXILIAR
        alert2 = dialogBuilder.create()
        btncerrar.setOnClickListener {
            alert2.dismiss()
        }
        alert2.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert2.show()
    }

    private fun fechados(){
        val recogerFecha = DatePickerDialog(this,
            { view, year, month, dayOfMonth ->
                val mesActual = month + 1
                c.set(year, month, dayOfMonth)
                savedDay = dayOfMonth
                savedMonth = month
                savedYear = year
                hour = c.get(Calendar.HOUR)
                minute = c.get(Calendar.MINUTE)
                // String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                val diaFormateado =
                    if (dayOfMonth < 10) CERO + dayOfMonth.toString() else dayOfMonth.toString()
                val mesFormateado =
                    if (mesActual < 10) CERO + mesActual.toString() else mesActual.toString()
                fechasendpost= String.format("$savedYear -${savedMonth+1}-$savedDay $savedHour:$savedMinute ")
              //  Toast.makeText(applicationContext, binding.preDate.text, Toast.LENGTH_LONG).show()
              //  binding.preDate.text = String.format("$savedDay/${savedMonth+1}/$savedYear $savedHour:$savedMinute")
                TimePickerDialog(this,this, hour, minute, true).show()
               //  binding.preDate.text =        diaFormateado + BARRA + mesFormateado + BARRA + year
                //Toast.makeText(applicationContext, binding.preDate.text, Toast.LENGTH_LONG).show()

            }, year, month, day
        )
        recogerFecha.getDatePicker().setMinDate(System.currentTimeMillis())
        recogerFecha.show()
    }



    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year
        getDateTimeCalendar()
        TimePickerDialog(this,this, hour, minute, true).show()
    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        val xx:String
        val diaFormateado =
            if (savedDay < 10) CERO + savedDay.toString() else savedDay.toString()
        val mesFormateado =
            if (savedDay < 10) CERO + savedMonth.toString() else savedMonth.toString()
        if (savedHour>12){
            xx = "am"
        }
        else{
            xx = "pm"
        }
        var len  =savedMonth.toString().length
        if (len==1){
            binding.preDate.text = String.format("$savedDay/0${savedMonth+1}/$savedYear $savedHour:$savedMinute")
            fechasendpost= String.format("$savedYear-0${savedMonth+1}-$savedDay $savedHour:$savedMinute")
        }else{
          //  savedMonth="0"+(savedMonth+1)
            binding.preDate.text = String.format("$savedDay/${savedMonth+1}/$savedYear $savedHour:$savedMinute")
            fechasendpost= String.format("$savedYear-${savedMonth+1}-$savedDay $savedHour:$savedMinute")
        }
    }


    private fun solicitaPermisosVersionesSuperiores(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { //validamos si estamos en android menor a 6 para no buscar los permisos
            return true
        }
        //validamos si los permisos ya fueron aceptados
        if (applicationContext.checkSelfPermission(WRITE_EXTERNAL_STORAGE) === PackageManager.PERMISSION_GRANTED && applicationContext.checkSelfPermission(
                CAMERA
            ) === PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(
                CAMERA
            )
        ) {
            cargarDialogoRecomendacion()
        } else {
            requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA), MIS_PERMISOS)
        }
        return false //implementamos el que procesa el evento dependiendo de lo que se defina aqui
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun cargarDialogoRecomendacion() {
        val dialogo: AlertDialog.Builder = AlertDialog.Builder(applicationContext)
        dialogo.setTitle("Permisos Desactivados")
        dialogo.setMessage("Debe conceder los permisos para el correcto funcionamiento de la App")
        dialogo.setPositiveButton("Aceptar",
            DialogInterface.OnClickListener { dialogInterface, i ->
                requestPermissions(
                    arrayOf(
                        WRITE_EXTERNAL_STORAGE, CAMERA
                    ), 100
                )
            })
        dialogo.show()
    }
}