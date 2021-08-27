package com.clinicalaluz.clinicaapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.databinding.ActivityPresionBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.json.JSONException
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

    private var fechasendpost=""
    var DniPaciente=""

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
        }
        //TERMINAR LA ACCION
        binding.preReturnMenu.setOnClickListener { finish() }

        val singleToneClass: SingleToneClass = SingleToneClass.instance
        DniPaciente = singleToneClass.data.toString()

       binding.newSistolica.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (  binding.newSistolica.length()==3){
                  binding.newDiastolica.requestFocus()
            }
        }
        override fun afterTextChanged(s: Editable) {
        }
       })
        binding.newDiastolica.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (  binding.newDiastolica.length()==3){
                       binding.newPulso.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
        consultData()
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
            var   dni = preferences.getString("dni", null)
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
               // val intent = Intent(this, RegisterGmailActivity::class.java)
               // startActivity(intent)
            }else{

                val newSistolica = (binding.newSistolica.text.toString()).toInt()
                val newDiastolica = (binding.newDiastolica.text.toString()).toInt()
                var  newPulso =(binding.newPulso.text.toString()).toInt()

                val url = "http://161.132.198.52:8080/app_laluz/pdoInsertPresion.php?"

                val stringRequest = object : StringRequest(Request.Method.POST, url,
                    Response.Listener { response ->
                        try {
                            if (response.toString().trim()=="Succes"){
                                Toast.makeText(applicationContext ,"Registrado con exito" , Toast.LENGTH_SHORT).show()
                                consultData()
                                binding.newPulso.text.clear()
                                binding.newSistolica.text.clear()
                                binding.newDiastolica.text.clear()
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
        val lineentry = ArrayList<Entry>()
        val url = "http://161.132.198.52:8080/app_laluz/pdoSelectPresion.php?doc=$DniPaciente"
        val rq = Volley.newRequestQueue(this)
        val arr = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                for(x in 0..response.length()-1){
                    val fecha = response.getJSONObject(x).getString("FECHA")
                    xvalues.add(fecha)
                    val presion = response.getJSONObject(x).getString("REG_PULSO")
                    val presionf = presion.toFloat()
                    lineentry.add(Entry(presionf, x))
                }
                val linedataset = LineDataSet(lineentry, "Historial del nivel de azucar")
                linedataset.color = resources.getColor(R.color.purple_200)
                val data = LineData(xvalues, linedataset)
                binding.lineChart.data = data
                binding.lineChart.setBackgroundColor(resources.getColor(R.color.white))
                binding.lineChart.animateXY(3000,3000)
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

        if(c.get(Calendar.AM_PM) == Calendar.AM){
            xx = "am"
            binding.preDate.text = String.format("$dd-${MM+1}-$yyyy $hh:$mm $xx")
            fechasendpost= String.format("$yyyy -${MM+1}-$dd $hh:$mm $xx")
        }else if(c.get(Calendar.AM_PM) == Calendar.PM){
            xx = "pm"
            fechasendpost= String.format("$yyyy -${MM+1}-$dd $hh:$mm $xx")
            binding.preDate.text = String.format("$dd-${MM+1}-$yyyy $hh:$mm $xx")
        }
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
        binding.preDate.setOnClickListener {
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

        binding.preDate.text = String.format("$savedDay/${savedMonth+1}/$savedYear $savedHour:$savedMinute")
    }
}