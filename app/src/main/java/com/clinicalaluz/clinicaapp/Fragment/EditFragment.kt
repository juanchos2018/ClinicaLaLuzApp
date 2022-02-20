package com.clinicalaluz.clinicaapp.Fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.PrincipalActivity
import com.clinicalaluz.clinicaapp.R
import com.clinicalaluz.clinicaapp.SingleToneClass
import com.clinicalaluz.clinicaapp.VolleySingleton
import com.clinicalaluz.clinicaapp.databinding.FragmentEditBinding
import com.clinicalaluz.clinicaapp.databinding.FragmentPerfilBinding
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.text.SimpleDateFormat
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EditFragment : Fragment(), OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener  {


    private lateinit var binding: FragmentEditBinding


    private var param1: String? = null
    private var param2: String? = null
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


    var FECHA_NACIMIENTO=""
    var FECHA_BD=""
    var SEXO=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditBinding.inflate(inflater, container, false)

        val preferences =  requireActivity().getSharedPreferences("datosuser", Context.MODE_PRIVATE)
        var NUM_DOC_IDENTIDAD = preferences.getString("NUM_DOC_IDENTIDAD", null).toString()
        DatosUser(NUM_DOC_IDENTIDAD)

        
        binding.offer.setOnClickListener(this)
        binding.search.setOnClickListener(this)

        pickDate()

        binding.btneditar.setOnClickListener {
            sendPost()
        }

        return binding.root
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.offer -> {
                SEXO="FE"
            }
            R.id.search -> {
                SEXO="MA"
            }
            else -> {
            }
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
        binding.btnfechanacimiento.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        val c = Calendar.getInstance()
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year
        val hh = c.get(Calendar.HOUR_OF_DAY)
        val mm = c.get(Calendar.MINUTE)
        val xx:String
        binding.btnfechanacimiento.text = String.format("$savedDay-${savedMonth+1}-$savedYear")
    //    var  fechasendpost= String.format("$savedYear -${savedMonth+1}-$savedDay")
        FECHA_BD=String.format("$savedYear -${savedMonth+1}-$savedDay")
        FECHA_NACIMIENTO=String.format("$savedDay-${savedMonth+1}-$savedYear")


    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        binding.btnfechanacimiento.text = String.format("$savedDay/${savedMonth+1}/$savedYear")

    }

    private  fun DatosUser(dni:String){
        val peticion = "/pdoSelectDni.php?doc=$dni"
        val rq = Volley.newRequestQueue(context)
        val jst = JsonObjectRequest(
            Request.Method.GET, getString(R.string.URL_BASE)+peticion,null,
            { response: JSONObject ->
                val json = response.optJSONArray("usuario")
                //      progres.dismiss()
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = json.getJSONObject(0)
                    var existe =jsonObject.optString("existe")

                    if (existe.equals("existe")){

                        var  nombres =jsonObject.optString("NOM_PACIENTE")
                        var  apellidoPa =jsonObject.optString("APE_PATERNO")
                        var  apellidoMa =jsonObject.optString("APE_MATERNO")
                        var  doc =jsonObject.optString("NUM_HC")
                        var  genero =jsonObject.optString("DES_GENERO")
                        var  FEC_NACIMIENTO =jsonObject.optString("FEC_NACIMIENTO")
                        var  NUM_TELEFONO =jsonObject.optString("NUM_TELEFONO")
                        var  NUM_EMAIL = jsonObject.optString("NUM_EMAIL")

                       // Log.e("fecha",FEC_NACIMIENTO)
                        if (FEC_NACIMIENTO=="NULO"){
                           binding.btnfechanacimiento.text="Sin Fecha"
                        }else{
                            binding.btnfechanacimiento.text=FEC_NACIMIENTO
                            FECHA_NACIMIENTO=FEC_NACIMIENTO

                           // FECHA_NACIMIENTO=
                            val fromServer = SimpleDateFormat("yyyy-MM-dd")
                        //    val fromServer = SimpleDateFormat("dd-MM-yyyy")
                           ///  FECHA_BD = fromServer.parse(FEC_NACIMIENTO).toString()

                            //Log.e("fecha seteada",FECHA_BD)

                         ///   Toast.makeText(requireContext(), FECHA_BD, Toast.LENGTH_LONG).show()


                        }

                        binding.etnombres.setText(nombres)
                        binding.etapellidoP.setText(apellidoPa)
                        binding.etapellidoM.setText(apellidoMa)
                        binding.etNumerodocumento.setText(doc)
                        binding.etNumerotelefono.setText(NUM_TELEFONO)
                        binding.etcorreo.setText(NUM_EMAIL)


                        if (genero=="FE"){
                            SEXO=genero
                           // binding.offer.isChecked=true
                            binding.offer.isChecked=true
                            binding.tvgenero.text="Género"
                        }else{
                            SEXO=genero
                            binding.search.isChecked=true
                            binding.tvgenero.text="Género"
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    //  progres.dismiss()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    //  Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
        rq.add(jst)
    }


    fun sendPost() {

        var documento  =  binding.etNumerodocumento.text.toString()
        var nombres  =  binding.etnombres.text.toString()
        var apellidoP  =  binding.etapellidoP.text.toString()
        var apellidoM  =  binding.etapellidoM.text.toString()
        var celular  =  binding.etNumerotelefono.text.toString()
        var email  =  binding.etcorreo.text.toString()

        if (documento.length<=7 || documento.length>=9){
            //  Toast.makeText(applicationContext ,"El Numero de Documento con 8 digitos" , Toast.LENGTH_SHORT).show()
            binding.etNumerodocumento.setError("El Numero de Documento con 8 digitos")
        }
        else if ( TextUtils.isEmpty(documento))
        {
            binding.etNumerodocumento.setError("Escribir Documento")

        }else if (TextUtils.isEmpty(nombres)){
            binding.etnombres.setError("Escribir Nombres")
        }
        else if (TextUtils.isEmpty(apellidoP)){
            binding.etapellidoP.setError("Escribir Apellido Paterno")
        }
        else if (TextUtils.isEmpty(apellidoM)){
            binding.etapellidoM.setError("Escribir Apellido Materno")
        }
        else if (TextUtils.isEmpty(email)){
            binding.etcorreo.setError("Escribir Correo")
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etcorreo.setError("Coreo no valido")
        }

        else {

           // Toast.makeText(requireContext(), "enbtra eL ELSE " , Toast.LENGTH_LONG).show()

            var progres = ProgressDialog(requireContext())
            progres.setMessage("Cargando...")
            progres.show()

            val peticion = "/pdoEditarInfo.php?"
            val stringRequest = object : StringRequest(
                Request.Method.POST, getString(R.string.URL_BASE)+peticion, Response.Listener { response ->
                    try {
                        progres.dismiss()

                            Log.e("respuesta",response)

                        val jsonObject = JSONTokener(response).nextValue() as JSONObject
                        val estado = jsonObject.getString("estado")
                        if (estado=="Editado"){
                            men(
                                "Actualizado",
                                "Se ha actualizado sus datos"
                            )
                        }else{
                            Toast.makeText(requireContext(),"Error al Editar", Toast.LENGTH_LONG).show()
                        }

//                        if (response.toString().trim()== "Enviado"){
//                            val preferences: SharedPreferences = requireContext().getSharedPreferences("datosuser",
//                                AppCompatActivity.MODE_PRIVATE
//                            )
//                            val editor = preferences.edit()
//                            editor.putString("nombres", nombres)
//                            editor.putString("dni", documento)
//                            editor.putString("correo", email)
//                            editor.putString("logueo", "dni")
//                            editor.commit()
//                            val singleToneClass: SingleToneClass = SingleToneClass.instance
//                            singleToneClass.data=documento
//                            Toast.makeText(requireContext(), "modificado con exito", Toast.LENGTH_LONG).show()
//
//                        }else{
//                            Toast.makeText(requireContext(), "hubo algun errror", Toast.LENGTH_LONG).show()
//                        }

                    } catch (e: JSONException) {
                        Toast.makeText(requireContext(), "ex :"+e.message.toString()+"", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                        progres.dismiss()
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(volleyError: VolleyError) {
                        Toast.makeText(requireContext(), "error:"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                        progres.dismiss()
                    }
                })
               {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put("doc",documento)
                    params.put("nombres",nombres)
                    params.put("ape_paterno",apellidoP)
                    params.put("ape_materno",apellidoM)
                 //   params.put("fecha",FECHA_BD)
                    params.put("fecha",FECHA_NACIMIENTO)
                    params.put("celular",celular)
                    params.put("email",email)
                    params.put("sexo",SEXO)
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
            VolleySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
        }
    }


    private fun men(mensaje: String,descrpcion:String){
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val btcerrrar: Button
        val tvmensaje: TextView
        var tvedsderpcion :TextView
        val  v = LayoutInflater.from(requireContext()).inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_correo, null)
        val animationView: LottieAnimationView = v.findViewById(com.clinicalaluz.clinicaapp.R.id.animation_viewcheck)
        animationView.playAnimation()
        dialogBuilder.setView(v)
        btcerrrar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.idbtncerrardialogo) as Button
        tvmensaje = v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvmensaje)
        tvedsderpcion=v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvdecripcion)
        tvedsderpcion.text=descrpcion
        tvmensaje.text = mensaje
        val alert = dialogBuilder.create()
        btcerrrar.setOnClickListener {
            alert.dismiss()
           // requireContext().finish()
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}