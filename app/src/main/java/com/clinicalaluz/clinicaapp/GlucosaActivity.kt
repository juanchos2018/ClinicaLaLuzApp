
package com.clinicalaluz.clinicaapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.databinding.ActivityGlucosaBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.json.JSONException
import java.util.*

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
    ///lateinit var dni :String


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
        binding.gluReturnMenu.setOnClickListener { finish() }

        val singleToneClass: SingleToneClass = SingleToneClass.instance
        DniPaciente = singleToneClass.data.toString()


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
                val peticion = "/pdoInserAzucar.php?"
                val stringRequest = object : StringRequest(Request.Method.POST, getString(R.string.URL_BASE)+peticion,
                    Response.Listener { response ->
                        try {
                            if (response.toString().equals("Succes")){
                                Toast.makeText(applicationContext ,"Registrado con existo" , Toast.LENGTH_SHORT).show()
                                consultData()
                                binding.gluNew.text.clear()
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

    private fun consultData(){
        val preferences = getSharedPreferences("datosuser", Context.MODE_PRIVATE)
        var   dni = preferences.getString("NUM_DOC_IDENTIDAD", null)
        val xvalues = java.util.ArrayList<String>()
        val lineentry = java.util.ArrayList<Entry>()
        val peticion = "/pdoSelectAzucar.php?doc=$dni"
        val rq = Volley.newRequestQueue(this)
        val arr = JsonArrayRequest(Request.Method.GET, getString(R.string.URL_BASE)+peticion, null,
            { response ->

                for(x in 0..response.length()-1){
                    val fecha = response.getJSONObject(x).getString("FECHA")
                    xvalues.add(fecha)
                    val presion = response.getJSONObject(x).getString("REG_AZUCAR")
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
            binding.gluDate.text = String.format("$dd/${MM+1}/$yyyy $hh:$mm $xx")
            fechasendpost= String.format("$yyyy -${MM+1}-$dd $hh:$mm $xx")
        }else if(c.get(Calendar.AM_PM) == Calendar.PM){
            xx = "pm"
            binding.gluDate.text = String.format("$dd/${MM+1}/$yyyy $hh:$mm $xx")
            fechasendpost= String.format("$yyyy -${MM+1}-$dd $hh:$mm $xx")
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
        fechasendpost= String.format("$savedYear-${savedMonth+1}-$savedDay $savedHour:$savedMinute")
    }
}