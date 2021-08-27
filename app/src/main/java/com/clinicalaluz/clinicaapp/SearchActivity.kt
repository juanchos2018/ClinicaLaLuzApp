package com.clinicalaluz.clinicaapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.databinding.ActivitySearchBinding


class SearchActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySearchBinding

    private  lateinit var adaptergrid :AdapterGridView

    val list = ArrayList<Medic>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listSede = resources.getStringArray(R.array.sede)
        val adapterSede = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, listSede)
        binding.spinnerSede.adapter = adapterSede
        binding.spinnerSede.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                /*val opS = binding.spinner.selectedItem.toString()
                if (opS == "Tacna"){
                    Toast.makeText(this@SearchActivity, "BIENVENIDO", Toast.LENGTH_SHORT).show()
                }*/
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        binding.lienarmensaje.setVisibility(View.VISIBLE);
        loadSpinner()
        binding.spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val op = binding.spinner.selectedItem.toString()
               // Toast.makeText(applicationContext ,op, Toast.LENGTH_SHORT).show()
                val url = "http://161.132.198.52:8080/cltacna/pdoSelect.php?especialidad=$op"
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

    fun loadSpinner(){
        val listEspecialidad = ArrayList<String>()
        listEspecialidad.add("Seleccionar")
        val urlSpinner = "http://161.132.198.52:8080/cltacna/pdoEspecialidad.php"

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

    fun showMedics(url:String, op:String){
        list.clear()
        binding.simpleProgressBar.setVisibility(View.VISIBLE);
        var img ="https://clinicalaluz.pe/wp-content/uploads/2021/08/dr-Marco-Antonio-Gomez-Neyra-traumatologia-e1628004376829-500x500.png"
        val rq = Volley.newRequestQueue(this)
        val arr = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                for(x in 0..response.length()-1){
                    val cod = response.getJSONObject(x).getString("COD_AUXILIAR")
                    val name = response.getJSONObject(x).getString("DES_AUXILIAR")
                    val especialidad = response.getJSONObject(x).getString("DES_ESPECIALIDAD")
                    list.add(Medic(cod, name, especialidad,img))
                }
                adaptergrid= AdapterGridView(this,list)
                binding.listview.adapter =adaptergrid
                binding.listview.setOnItemClickListener { parent, view, position, id ->
                    val intent = Intent(this, MedicoDetalleActivity::class.java)
                    intent.putExtra("nameMedic", list[position])
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
            {
            })
        rq.add(arr)
    }
}