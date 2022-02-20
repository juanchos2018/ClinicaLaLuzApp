package com.clinicalaluz.clinicaapp.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.R
import com.clinicalaluz.clinicaapp.databinding.FragmentPerfilBinding
import org.json.JSONException
import org.json.JSONObject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PerfilFragment : Fragment() {



    private lateinit var binding: FragmentPerfilBinding


    private var param1: String? = null
    private var param2: String? = null

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


        //inflater.inflate(R.layout.fragment_perfil, container, false)
        binding = FragmentPerfilBinding.inflate(inflater, container, false)


        val preferences =  requireActivity().getSharedPreferences("datosuser", Context.MODE_PRIVATE)
        var NUM_DOC_IDENTIDAD = preferences.getString("NUM_DOC_IDENTIDAD", null).toString()
        DatosUser(NUM_DOC_IDENTIDAD)

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private  fun DatosUser(dni:String){
        val url = "http://161.132.198.52:8080/app_laluz/pdoSelectDni.php?doc=$dni"
        val rq = Volley.newRequestQueue(context)
        val jst = JsonObjectRequest(
            Request.Method.GET, url,null,
            { response: JSONObject ->
                val json = response.optJSONArray("usuario")
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
                        var  NUM_EMAIL = jsonObject.optString("NUM_EMAIL")


                        binding.tvnombrescompleto.text =nombres+" "+apellidoPa +" "+apellidoMa
                        binding.tvnumerodocumento.text=doc
                        binding.tvfechanacimiento.text=FEC_NACIMIENTO
                        binding.tvcorreoelectronico.text=NUM_EMAIL

                         if (genero=="FE"){
                            binding.imggenero.setImageResource(R.drawable.famale)
                            // binding.imggenero.
                             binding.tvtipogenero.text="Género Femenino"
                        }else if (genero=="MA"){
                            binding.imggenero.setImageResource(R.drawable.malepn)
                             binding.tvtipogenero.text="Género Masculino"
                        }else{
                           // binding.imgsexo.setImageResource(R.drawable.ic_null)
                             binding.tvtipogenero.text="Género Nulo"
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

}