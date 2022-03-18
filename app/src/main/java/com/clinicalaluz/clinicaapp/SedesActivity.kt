package com.clinicalaluz.clinicaapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.Adapters.AdapterSedes
import com.clinicalaluz.clinicaapp.clases.ClsSedes
import com.clinicalaluz.clinicaapp.databinding.ActivitySedesBinding


class SedesActivity : AppCompatActivity() {

    private lateinit var  binding :ActivitySedesBinding

    lateinit var adapter: AdapterSedes
    val listSedes: ArrayList<ClsSedes> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_sedes)
        binding = ActivitySedesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val linearLayoutManager2 = LinearLayoutManager(this)
        linearLayoutManager2.reverseLayout = true
        linearLayoutManager2.stackFromEnd = true

        binding.recylcersedes.layoutManager = linearLayoutManager2

        binding.txtbuscarsedes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                filtrar(binding.txtbuscarsedes.text)
            }
            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.imageViewplaces1.setOnClickListener {
            salir()
         }
        ListaSedes()
    }

    private fun salir() {
        finish()
    }

    private fun filtrar(texto: Editable) {
        val filtradatos: ArrayList<ClsSedes> = ArrayList()
        for (item in listSedes) {
            if (item.NOM_SUCURSAL.toLowerCase().contains(texto)) {
                filtradatos.add(item)
            }
            adapter.filtrar(filtradatos)
        }
    }
    override fun onStart() {
        super.onStart()
    }
    fun  ListaSedes(){
        binding.simpleProgressBar7.visibility= View.VISIBLE
        var peticion ="/Controllers/SedesController.php?tipo=listsedes"
        val rqSp = Volley.newRequestQueue(this)
        val js = JsonArrayRequest(
            Request.Method.GET, getString(R.string.URL_BASE)+peticion, null,
            { response ->
                for(x in 0..response.length()-1) {
                    var nombre = response.getJSONObject(x).getString("NOM_SUCURSAL")
                    var code = response.getJSONObject(x).getString("COD_SUCURSAL")
                    var img =response.getJSONObject(x).getString("IMAGEN")
                    var direccion = response.getJSONObject(x).getString("direccion")
                    var telefono = response.getJSONObject(x).getString("telefono")
                    var direcMapa = response.getJSONObject(x).getString("direcMapa")
                    var sedes =  ClsSedes(code,nombre,direccion,telefono,getString(R.string.URL_BASE)+img,direcMapa)
                    listSedes.add(sedes)
                }

               adapter = AdapterSedes(this, listSedes)
               binding.recylcersedes.adapter=adapter
               binding.simpleProgressBar7.visibility=View.GONE

//                adapter.setOnClickListener { v: View? ->
//                    val intent = Intent(this, EspecialidadActivity::class.java)
//                    intent.putExtra("COD_SUCURSAL", listSedes.get(binding.recylcersedes.getChildAdapterPosition(v!!)).COD_SUCURSAL)
//                    intent.putExtra("phone", listSedes.get(binding.recylcersedes.getChildAdapterPosition(v!!)).telefono)
//                    intent.putExtra("NOM_SUCURSAL", listSedes.get(binding.recylcersedes.getChildAdapterPosition(v!!)).NOM_SUCURSAL)
//                    startActivity(intent)
//                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    binding.simpleProgressBar7.visibility=View.GONE
                    Log.e("errror",volleyError.toString() )
                    Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
        rqSp.add(js)

    }
}