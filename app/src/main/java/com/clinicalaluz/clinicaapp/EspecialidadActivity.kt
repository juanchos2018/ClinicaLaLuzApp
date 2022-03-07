package com.clinicalaluz.clinicaapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.Adapters.AdapterGridEspecialidad
import com.clinicalaluz.clinicaapp.Adapters.AdapterGriedViewAlter
import com.clinicalaluz.clinicaapp.clases.ClsEspecialidad
import com.clinicalaluz.clinicaapp.databinding.ActivityEspecialidadBinding

class EspecialidadActivity : AppCompatActivity() {


    private lateinit var binding : ActivityEspecialidadBinding

    val listEpecialidad = ArrayList<ClsEspecialidad>()
    private  lateinit var adapterespecialidad : AdapterGridEspecialidad
    private  lateinit var adapterespecialidad2 : AdapterGriedViewAlter

    var COD_SUCURSAL=""
    var phone=""
    var NOM_SUCURSAL=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEspecialidadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        COD_SUCURSAL = intent.getSerializableExtra("COD_SUCURSAL").toString()
        phone = intent.getSerializableExtra("phone").toString()
        NOM_SUCURSAL=intent.getSerializableExtra("NOM_SUCURSAL").toString()


        val gridLayoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        binding.recyclerespecialiad.layoutManager = gridLayoutManager


        binding.imageViewplaces2.setOnClickListener {
            salir()
        }
        binding.imghpnecall.setOnClickListener {
            callPhone(phone)
        }
        binding.txtbuscarespecialidad.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                filtrar(binding.txtbuscarespecialidad.text)
            }
            override fun afterTextChanged(s: Editable) {

            }
        })
        loadEspecialidades(COD_SUCURSAL)
    }

    private fun salir() {
        finish()
    }
    private fun filtrar(texto: Editable) {
        val filtradatos: ArrayList<ClsEspecialidad> = ArrayList()
        for (item in listEpecialidad) {
            if (item.DES_ESPECIALIDAD .toLowerCase().contains(texto)) {
                filtradatos.add(item)
            }
            adapterespecialidad2.filtrar(filtradatos)
        }
    }
    fun callPhone(number:String){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && checkSelfPermission(Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
        }else{
            if (!TextUtils.isEmpty(number)){
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:" + number)
                startActivity(callIntent)
            }
        }
    }

    fun loadEspecialidades(idbd:String){
        val peticion = "/pdoEspecialidad.php?COD_SUCURSAL=$idbd"
        val rqSp = Volley.newRequestQueue(this)
        val js = JsonArrayRequest(
            Request.Method.GET, getString(R.string.URL_BASE)+peticion, null,
            { response ->
                for(x in 0..response.length()-1) {
                    val DES_ESPECIALIDAD = response.getJSONObject(x).getString("DES_ESPECIALIDAD")
                    val COD_ESPECIALIDAD = response.getJSONObject(x).getString("COD_ESPECIALIDAD")
                    val PRECIO_V = response.getJSONObject(x).getString("PRECIO_V")
                    val IMAGEN = response.getJSONObject(x).getString("IMAGEN")
                    listEpecialidad.add(ClsEspecialidad(DES_ESPECIALIDAD,getString(R.string.URL_BASE)+IMAGEN,COD_ESPECIALIDAD,PRECIO_V))
                }
                adapterespecialidad2= AdapterGriedViewAlter(this,listEpecialidad)
                adapterespecialidad2.NOM_SUCURSAL=NOM_SUCURSAL
                adapterespecialidad2.COD_SUCURSAL=idbd
                binding.recyclerespecialiad.adapter =adapterespecialidad2

               // adapterespecialidad= AdapterGridEspecialidad(this,listEpecialidad)
//                binding.gridespecialidad.adapter =adapterespecialidad//
//                binding.gridespecialidad.setOnItemClickListener { parent, view, position, id ->
//                    val intent = Intent(this, DoctoresActivity::class.java)
//                    intent.putExtra("especialidad", listEpecialidad[position].DES_ESPECIALIDAD)
//                    intent.putExtra("COD_ESPECIALIDAD", listEpecialidad[position].COD_ESPECIALIDAD)
//                    intent.putExtra("PRECIO_V", listEpecialidad[position].PRECIO_V)
//                    intent.putExtra("NOM_SUCURSAL", NOM_SUCURSAL)
//                    intent.putExtra("COD_SUCURSAL", idbd)
//                    startActivity(intent)
//                }
            },
            {
            })
        rqSp.add(js)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 callPhone(phone)
            } else {
                Toast.makeText(this, "Permiso Denegado!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}