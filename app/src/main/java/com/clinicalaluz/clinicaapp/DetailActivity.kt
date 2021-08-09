package com.clinicalaluz.clinicaapp


import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

import com.clinicalaluz.clinicaapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //empaquetando los datos para ser usado luego
        val medic = intent.getSerializableExtra("nameMedic") as Medic

        val medicD = medic.nameMedic
        val specialtyD = medic.nameSpecialty

        //pintado de el campo detailNameMedic
        binding.detailNameMedic.text = medicD
        if(medicD == "LABORATORIO CLINICO" || medicD == "CENTRO DE VACUNACION"){
            binding.detailMedic.text = String.format("El $medicD tiene fijado el siguiente horario:" )
        }else{
            binding.detailMedic.text = String.format("Nuestro médico  Dr(a). $medicD especialista " +
                    "en $specialtyD  tiene fijado el siguiente horario de atencion :" )
        }

        //captura de cod_auxiliar para las consultas necesarias
        val codClave = medic.codMedic

        val url = "http://192.168.3.233:8080/cltacna/pdoHorarios.php?codAuxiliar=$codClave"

        showHorario(url)

        binding.returnImaDetail.setOnClickListener {
            finish()
        }

        binding.wsp.setOnClickListener {
            val textWsp: String = if (medicD == "LABORATORIO CLINICO" || medicD == "CENTRO DE VACUNACION")
                String.format("Hola!, quisiera hacer una reservacion de una cita para el $specialtyD")
            else
                String.format("Hola!, quisiera hacer una reservacion de una cita para $specialtyD" +
                        " con el Dr(a) $medicD")
            val sendIntent = Intent(Intent.ACTION_VIEW)
            val urlWsp = "whatsapp://send?phone=+51940440123&text=$textWsp"
            sendIntent.data = Uri.parse((urlWsp))
            try{
                startActivity(sendIntent)
            }catch(e: PackageManager.NameNotFoundException){
                Toast.makeText(this, "Instale WhatsApp en su movil", Toast.LENGTH_SHORT).show()
            }

        }

    }
    private fun showHorario(url:String){
        val list = ArrayList<Horario>()
        val rq = Volley.newRequestQueue(this)
        val arr = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                for(x in 0..response.length()-1){
                    val dayH = response.getJSONObject(x).getString("DIA")
                    val dateH = response.getJSONObject(x).getString("FECHA")
                    val startH = response.getJSONObject(x).getString("DES_HORA_I")
                    val endH = response.getJSONObject(x).getString("DES_HORA_F")
                    list.add(Horario(dayH, dateH, startH, endH))
                }
                binding.detailListview.adapter = HorarioAdapter(this, list)
            },
            {
            })
        rq.add(arr)
    }

}