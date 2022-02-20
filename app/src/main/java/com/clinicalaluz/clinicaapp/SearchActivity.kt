package com.clinicalaluz.clinicaapp

import android.R
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.Adapters.AdapterGridView
import com.clinicalaluz.clinicaapp.clases.ClsSedes
import com.clinicalaluz.clinicaapp.clases.Medic
import com.clinicalaluz.clinicaapp.databinding.ActivitySearchBinding


class SearchActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySearchBinding
    private  lateinit var adaptergrid : AdapterGridView
    val list = ArrayList<Medic>()

    //private  var adapter2: SpinAdapter2? = null
    var id_bddato=""
    private  lateinit var  adapter2:SpinAdapter2
    val listaSedes2: ArrayList<ClsSedes> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val listSede = resources.getStringArray(R.array.sede)
//        val adapterSede = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listSede)
//        binding.spinnerSede.adapter = adapterSede
//        binding.spinnerSede.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                /*val opS = binding.spinner.selectedItem.toString()
//                if (opS == "Tacna"){
//                    Toast.makeText(this@SearchActivity, "BIENVENIDO", Toast.LENGTH_SHORT).show()
//                }*/
//            }
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("Not yet implemented")
//            }
//        }

        binding.lienarmensaje.setVisibility(View.VISIBLE);

        //loadSpinner("idbde")
        loadSedes()

        binding.spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var op = binding.spinner.selectedItem.toString()
               // Toast.makeText(applicationContext ,op, Toast.LENGTH_SHORT).show()
                var url = "http://161.132.198.52:8080/app_laluz/pdoSelect.php?especialidad=$op&COD_SUCURSAL=$id_bddato"
                showMedics(url, op)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.returnIma.setOnClickListener {
            finish()
        }
        binding.txtbuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // searchPeopleProfile(etbuscarnombre.getText().toString());
                filtrar(binding.txtbuscar.text)
            }
            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.spinnerSede.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override   fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //    makeText(SalonesProfesor.this, spinner.getSelectedItemPosition(), LENGTH_SHORT).show();gStrinf
                val user = adapter2.getItem(position)
                var idsede: String = user.COD_SUCURSAL

                id_bddato=idsede
                // val op = binding.spinnerSede.selectedItem.toString()
               // Toast.makeText(applicationContext , ""+idsede , Toast.LENGTH_SHORT).show()
                loadSpinner(idsede)
            }
            override  fun onNothingSelected(parent: AdapterView<*>?) {}
        })

//    binding.spinnerSede.onItemSelectedListener= object :
//            AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//            ///     var user: ClsSedes = parent.get(position)
//                // .getItem(position)
//                // var positions =listaSedes2.get(position)
//                val user = adapter2!!.getItem(position)
//                var idsede: String = user.COD_SUCURSAL
//           //     val op = binding.spinnerSede.selectedItem.toString()
//                Toast.makeText(applicationContext , ""+idsede , Toast.LENGTH_SHORT).show()
//            //    val url = "http://161.132.198.52:8080/cltacna/pdoSelect.php?especialidad=$op"
//              //  showMedics(url, op)
//            }
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("Not yet implemented")
//            }
//        }

    }

    private fun filtrar(texto: Editable) {
        val filtradatos: ArrayList<Medic> = ArrayList()
        for (item in list) {
            //   if (item.getNombrepaciente().toLowerCase().contains(texto)){
            if (item.nameMedic.toLowerCase().contains(texto)) {
                filtradatos.add(item)
            }
            adaptergrid.filtrar(filtradatos)
        }
    }

    fun loadSedes(){


        val urlSpinner = "http://161.132.198.52:8080/app_laluz/pdoSelectSedes.php"
        val rqSp = Volley.newRequestQueue(this)
        val js = JsonArrayRequest(Request.Method.GET, urlSpinner, null,
            { response ->
                for(x in 0..response.length()-1) {
                    var nombre = response.getJSONObject(x).getString("NOM_SUCURSAL")
                    var code = response.getJSONObject(x).getString("COD_SUCURSAL")
                    var sedes =  ClsSedes(code,nombre,"","","","")
                   listaSedes2.add(sedes)
                }
                adapter2 = SpinAdapter2(this, R.layout.simple_spinner_dropdown_item, listaSedes2)
             //   val adapterSpecialty = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaSedes2)
                binding.spinnerSede.adapter = adapter2
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    /// progres.dismiss()
                 //   binding.simpleProgressBar.setVisibility(View.GONE);
                    Log.e("errror",volleyError.toString() )
                    Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
        rqSp.add(js)
    }

    fun loadSpinner(idbd:String){
        val listEspecialidad = ArrayList<String>()
        listEspecialidad.add("Seleccionar")

        val urlSpinner = "http://161.132.198.52:8080/app_laluz/pdoEspecialidad.php?COD_SUCURSAL=$idbd"
       // val urlSpinner = "http://161.132.198.52:8080/cltacna/pdoEspecialidad.php?COD_SUCURSAL=$idbd"
        val rqSp = Volley.newRequestQueue(this)
        val js = JsonArrayRequest(Request.Method.GET, urlSpinner, null,
            { response ->
                for(x in 0..response.length()-1) {
                    val desc = response.getJSONObject(x).getString("DES_ESPECIALIDAD")
                    listEspecialidad.add(desc.replaceFirstChar { it.uppercase() })
                }
                val adapterSpecialty = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listEspecialidad)
                binding.spinner.adapter = adapterSpecialty
            },
            {
            })
        rqSp.add(js)
    }

    fun showMedics( url:String, op:String){
        list.clear()
      //  url = "http://161.132.198.52:8080/cltacna/pdoSelect.php?especialidad=$op"
        binding.simpleProgressBar.setVisibility(View.VISIBLE);
        var img ="https://clinicalaluz.pe/wp-content/uploads/2021/08/dr-Marco-Antonio-Gomez-Neyra-traumatologia-e1628004376829-500x500.png"
        val rq = Volley.newRequestQueue(this)
        val arr = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                for(x in 0..response.length()-1){
                    val cod = response.getJSONObject(x).getString("COD_AUXILIAR")
                    val name = response.getJSONObject(x).getString("DES_AUXILIAR")
                    val especialidad = response.getJSONObject(x).getString("DES_ESPECIALIDAD")
                    val COD_MEDICO = response.getJSONObject(x).getString("COD_MEDICO")
                    list.add(Medic(cod, name, especialidad,img,COD_MEDICO,""))
                }
                adaptergrid= AdapterGridView(this,list)
                binding.listview.adapter =adaptergrid
                binding.listview.setOnItemClickListener { parent, view, position, id ->
                    val intent = Intent(this, MedicoDetalleActivity::class.java)
                    intent.putExtra("nameMedic", list[position])
                    intent.putExtra("idbd", id_bddato)
                    startActivity(intent)
                }
                binding.simpleProgressBar.setVisibility(View.GONE);
                if (list.size>0){
                    binding.lienarmensaje.setVisibility(View.GONE);
                }else{
                    binding.lienarmensaje.setVisibility(View.VISIBLE);
                }
                ///    binding.listview.adapter = Adapter(this, list)
                /*  binding.listview.setOnItemClickListener { parent, view, position, id ->
                      val intent = Intent(this, DetailActivity::class.java)
                      intent.putExtra("nameMedic", list[position])
                      startActivity(intent)
                  } */
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    /// progres.dismiss()
                    binding.simpleProgressBar.setVisibility(View.GONE);
                    Log.e("errror",volleyError.toString() )
                    Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                }
            })
        rq.add(arr)
    }
}