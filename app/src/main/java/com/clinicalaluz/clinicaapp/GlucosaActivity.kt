
package com.clinicalaluz.clinicaapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.clases.ClsTest2
import com.clinicalaluz.clinicaapp.clases.TemplatePDF2
import com.clinicalaluz.clinicaapp.databinding.ActivityGlucosaBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.tapadoo.alerter.Alerter
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.util.*
import kotlin.collections.ArrayList

class GlucosaActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private lateinit var binding : ActivityGlucosaBinding
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
    private var fechasendpost=""
    private var dniuser=""
    var DniPaciente=""
    var DES_AUXILIAR=""
    var NUM_DOC_IDENTIDAD=""
    lateinit var template: TemplatePDF2
    private  lateinit var alert : AlertDialog
    private  lateinit var alert2 : AlertDialog
    private val header = arrayOf("FECHA", "GLUCOSA")
    private val shortext = "REPORTE DE GLUCOSA"
    ///lateinit var dni :String

    var listaTest=ArrayList<ClsTest2>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlucosaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //CAPTURA INICIAL DE LA FECHA Y HORA
        dateTimeDefect()

        //ESCOGER LA FECHA Y HORA
        pickDate()

        binding.saveGlucosa.setOnClickListener {
            sendPost()
        }
        //TERMINAR LA ACCION
        binding.preReturnMenu.setOnClickListener { finish() }

        val singleToneClass: SingleToneClass = SingleToneClass.instance

        val preferences = getSharedPreferences("datosuser", MODE_PRIVATE)
        DES_AUXILIAR = preferences.getString("DES_AUXILIAR", null).toString()
        NUM_DOC_IDENTIDAD = preferences.getString("NUM_DOC_IDENTIDAD", null).toString()

        DniPaciente = NUM_DOC_IDENTIDAD

        binding.btndialogodos.setOnClickListener {
            abriridialogo()
        }
        consultData()
    }

    fun sendPost() {

        val preferences = getSharedPreferences("datosuser", Context.MODE_PRIVATE)
        var   dni = preferences.getString("NUM_DOC_IDENTIDAD", null)
        var   TipoSE = preferences.getString("logueo", null)

        if (TextUtils.isEmpty(binding.gluNew.text)){
            Toast.makeText(applicationContext, "LLenar el azucar", Toast.LENGTH_LONG).show()

        }else{
            if (dni==null ){

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

                // DNI_PACIENTE=dniuser   val singleToneClass: SingleToneClass = SingleToneClass.instance
                //                ///  singleToneClass.data=dniuser
              //  val intent = Intent(this, RegisterGmailActivity::class.java)
              //  startActivity(intent)
            }else{
                val azucar = (binding.gluNew.text.toString()).toInt()
                val peticion = "/Controllers/UsuarioController.php?"
                val stringRequest = object : StringRequest(Request.Method.POST, getString(R.string.URL_BASE)+peticion,
                    Response.Listener { response ->
                        try {
                            val jsonObject = JSONTokener(response).nextValue() as JSONObject
                            val message = jsonObject.getString("message")
                            if (message=="Success"){

                                val imm: InputMethodManager = getSystemService(
                                    INPUT_METHOD_SERVICE
                                ) as InputMethodManager
                                imm.hideSoftInputFromWindow(binding.gluNew.getWindowToken(), 0)
                                simpleAlert("Mensaje","Registrado con exito")
                              //  Toast.makeText(applicationContext ,"Registrado con existo" , Toast.LENGTH_SHORT).show()
                                consultData()
                                binding.gluNew.text.clear()
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
                        params.put("tipo","glucosa")
                        params.put("doc",dni)
                        params.put("fecha",fechasendpost)
                        params.put("azucar",azucar.toString())
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
            .inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_reportedos, null)
        var tiporeporte=false
        dialogBuilder.setView(v)
        btngenerar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.btngenrarpdfss) as Button
        radiogrupoi=v.findViewById(com.clinicalaluz.clinicaapp.R.id.grupocomprobantes)
        layaout=v.findViewById(com.clinicalaluz.clinicaapp.R.id.layoutfechass)
        btnfecha1=v.findViewById(com.clinicalaluz.clinicaapp.R.id.btnfechainicios)
        btnfecha2=v.findViewById(com.clinicalaluz.clinicaapp.R.id.btnfechafins)
        radiuouno=v.findViewById(com.clinicalaluz.clinicaapp.R.id.rbdtodos)

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
            if (checkedId==R.id.rbdtodos){
                layaout.isVisible=false
                tiporeporte=false
            }else if (checkedId==R.id.rbdfechass){
                layaout.isVisible=true
                tiporeporte=true
            }
        }
        alert2= dialogBuilder.create()
        btngenerar.setOnClickListener {
            if (tiporeporte){
                if (true){
                }
            }else{
            }
            alert2.dismiss()
            consultaReportes(tiporeporte,fechasendpostDialogo,fechasendpostDialogo2)
        }
        alert2.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert2.show()
    }

    private fun consultaReportes(tiporeporte: Boolean, fecha1:String,fecha2:String) {

        var tipos =""
        if (tiporeporte){
            tipos ="fechasg"
        }else{
            tipos ="todog"
        }
        var result =when(tiporeporte){
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
                    val REG_AZUCAR = response.getJSONObject(x).getString("REG_AZUCAR")
                    var test =  ClsTest2(fecha,REG_AZUCAR)
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

    private fun genetatepdf() {
        template = TemplatePDF2(this)
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
    fun simpleAlert(title:String,subtile:String) {
        Alerter.create(this@GlucosaActivity)
            .setTitle(title)
            .setText(subtile)
            .setIcon(R.drawable.ic_check)
            .setBackgroundColorRes(R.color.verde)
            .setIconColorFilter(0)
            .show()
    }

    private fun consultData(){
        val preferences = getSharedPreferences("datosuser", Context.MODE_PRIVATE)
        var   dni = preferences.getString("NUM_DOC_IDENTIDAD", null)
        val xvalues = java.util.ArrayList<String>()
        val lineentry = java.util.ArrayList<Entry>()
        var listaTest2 =   ArrayList<ClsTest2>()
        val peticion = "/Controllers/UsuarioController.php?condicion=glucosa&doc=$dni"
        val rq = Volley.newRequestQueue(this)
        val arr = JsonArrayRequest(Request.Method.GET, getString(R.string.URL_BASE)+peticion, null,
            { response ->
                for(x in 0..response.length()-1){
                    val fecha = response.getJSONObject(x).getString("FECHA")
                    xvalues.add(fecha)
                    val presion = response.getJSONObject(x).getString("REG_AZUCAR")
                    val presionf = presion.toFloat()
                    lineentry.add(Entry(presionf, x))
                    var test =  ClsTest2(fecha,presion)
                    listaTest2.add(test)
                }
                val linedataset = LineDataSet(lineentry, "Historial del nivel de azucar")
                linedataset.color = resources.getColor(R.color.purple_200)
                val data = LineData(xvalues, linedataset)
                binding.lineChart.data = data
                binding.lineChart.setBackgroundColor(resources.getColor(R.color.white))
                binding.lineChart.animateXY(3000,3000)

                binding.lineChart.setOnChartValueSelectedListener(object :
                    OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry, dataSetIndex: Int, h: Highlight?) =
                        try {
                            var valor1 =listaTest2.get(e.xIndex).REG_AZUCAR
                            var fechap =listaTest2.get(e.xIndex).FECHA
                            detallePulso("GLUCOSA " +valor1,fechap)
                        }catch (ex:java.lang.Exception){
                        }

                    override fun onNothingSelected() {
                    }

                })
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "ERROR: %s".format(error.toString()), Toast.LENGTH_SHORT).show()
            })

        var socketTimeout =30000
        var policy = DefaultRetryPolicy(socketTimeout,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        arr.setRetryPolicy(policy);
        rq.add(arr)
    }

    private fun detallePulso(GLUCOSA: String, fechap: String) {

        val dialogBuilder = AlertDialog.Builder(this)
        val btncerrar: Button
        val tvglucosa: TextView
        val tvfecha  : TextView
        val tvnombreuser  : TextView

        val v = LayoutInflater.from(applicationContext)
            .inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_detalledos, null)

        dialogBuilder.setView(v)
        btncerrar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.idbtncerrardialogo4) as Button
        tvglucosa=v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvglucosa)
        tvfecha=v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvfechaglucosa)
        tvnombreuser=v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvnombreuserglucosa)

        tvglucosa.text=GLUCOSA
        tvfecha.text=fechap
        tvnombreuser.text=DES_AUXILIAR
        alert = dialogBuilder.create()
        btncerrar.setOnClickListener {
            alert.dismiss()
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()

    }

    private fun dateTimeDefect(){
        val c = Calendar.getInstance()
        val yyyy = c.get(Calendar.YEAR)
        val MM = c.get(Calendar.MONTH)
        val dd = c.get(Calendar.DAY_OF_MONTH)
        val hh = c.get(Calendar.HOUR_OF_DAY)
        val mm = c.get(Calendar.MINUTE)
        val xx:String

        if(c.get(Calendar.AM_PM) == Calendar.AM){
            xx = "am"
            var  mes =MM+1
            var len=mes.toString().length
            if (len==1){
                fechasendpost= String.format("$yyyy-0$mes-$dd $hh:$mm $xx")
            }
            else{
                fechasendpost= String.format("$yyyy-$mes-$dd $hh:$mm $xx")
            }
            binding.gluDate.text = String.format("$dd/${MM+1}/$yyyy $hh:$mm $xx")


        }else if(c.get(Calendar.AM_PM) == Calendar.PM){
            xx = "pm"
            var  mes =MM+1
            var len=mes.toString().length
            if (len==1){
                fechasendpost= String.format("$yyyy-0$mes-$dd $hh:$mm $xx")
            }
            else{
                fechasendpost= String.format("$yyyy-$mes-$dd $hh:$mm $xx")
            }
            binding.gluDate.text = String.format("$dd/${MM+1}/$yyyy $hh:$mm $xx")
           // fechasendpost= String.format("$yyyy -${MM+1}-$dd $hh:$mm $xx")
        }
        //Toast.makeText(this, fechasendpost, Toast.LENGTH_SHORT).show()

    }

    private fun getDateTimeCalendar(){
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    private fun pickDate(){
        binding.gluDate.setOnClickListener {
            getDateTimeCalendar()

            DatePickerDialog(this, this, year, month, day).show()
        }
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

        binding.gluDate.text = String.format("$savedDay/${savedMonth+1}/$savedYear $savedHour:$savedMinute")

        var  mes =savedMonth+1
        var len=mes.toString().length
        if (len==1){
            fechasendpost= String.format("$savedYear-0$mes-$savedDay $savedHour:$savedMinute")
        }
        else{
            fechasendpost= String.format("$savedYear-$mes-$savedDay $savedHour:$savedMinute ")
        }
    }
}