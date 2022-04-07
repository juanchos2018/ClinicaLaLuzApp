package com.clinicalaluz.clinicaapp.Fragment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import com.clinicalaluz.clinicaapp.Interface.InterfaceCita
import com.clinicalaluz.clinicaapp.R
import com.clinicalaluz.clinicaapp.clases.ClsMisCitas
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class DialogoFragment : BottomSheetDialogFragment() {

    var  interfaceCita: InterfaceCita? = null
    var  nombnredoctor=""
    var  nombreespecialidad=""
    var  fechaAtencion=""
    var  codAtencion=""
    var  cod_medico=""
    var  codEspecialidad=""
    var  desEspecialidad=""
    var  nomSucursal=""
    var  codSucursal=""
    var  idHora=""
    var  desHora=""
    var  bd=""
    var  precio_v=""
    var  codDocumento=""
    var  imptotal=""
    var  cod_bd=""
    var  cod_expediente=""
    var  situacion=""
    var  num_serie=""
    var  num_documento=""


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        @JvmStatic
        open fun newInstance(): DialogoFragment? {
            return DialogoFragment()
        }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.fragment_dialogo, null)
        var tvnamedoctor :TextView =contentView.findViewById(R.id.tvnomvredoctor4)
        var tvfechacita :TextView=contentView.findViewById(R.id.tvfechacita4)
        var tvespecialidad:TextView=contentView.findViewById(R.id.tvespecialidad4)
        var btnReprogramar:Button =contentView.findViewById(R.id.btnreprogramar)
        var btnPagar :Button=contentView.findViewById(R.id.btnPagar)
        var btndescargar:Button=contentView.findViewById(R.id.btndescargar)

        btnReprogramar.setOnClickListener {
            reproFunc()
        }
        btnPagar.setOnClickListener {
            pagarFunc()
        }
        btndescargar.setOnClickListener {
            downladDocument()
        }
        tvnamedoctor.text=nombnredoctor
        tvespecialidad.text=nombreespecialidad
        tvfechacita.text=fechaAtencion

        dialog.setContentView(contentView)
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
    }

   // @RequiresApi(Build.VERSION_CODES.O)
    private fun reproFunc() {
       if (codDocumento=="OS"){
           var mensaje="Necesitas Pagar la Cita"
           interfaceCita?.onCallbackTres(mensaje)
       }else{

           if (situacion=="ATE"){
               var mensaje="YA FUE ATENDIDO"
               interfaceCita?.onCallbackTres(mensaje)
           }else{
               var dia=""
               var mes=""
               var anio=""
               var array =fechaAtencion.split("-")
               dia=array[2]
               mes=array[1]
               anio=array[0]
               var fechacur =String.format(dia+"-"+mes+"-"+anio)
               var horario  = ClsMisCitas(codAtencion,cod_medico,cod_expediente,nombnredoctor,codEspecialidad,codSucursal,idHora,desHora,desEspecialidad,"",fechacur,nomSucursal,bd,"",codDocumento,imptotal,cod_bd,situacion,num_serie,num_documento)
               interfaceCita?.onCallback(horario)
           }
       }
    }

    private  fun downladDocument(){
        var dia=""
        var mes=""
        var anio=""
        var array =fechaAtencion.split("-")
        dia=array[2]
        mes=array[1]
        anio=array[0]
        var fechacur =String.format(dia+"-"+mes+"-"+anio)
        var horario  =  ClsMisCitas(codAtencion,cod_medico,cod_expediente,nombnredoctor,codEspecialidad,codSucursal,idHora,desHora,desEspecialidad,"",fechacur,nomSucursal,bd,precio_v,codDocumento,imptotal,cod_bd,situacion,num_serie,num_documento)

        interfaceCita?.onCallDonwland(horario)
    }

    private fun pagarFunc() {
        if (codDocumento=="OS"){
            var dia=""
            var mes=""
            var anio=""
            var array =fechaAtencion.split("-")
            dia=array[2]
            mes=array[1]
            anio=array[0]
            var fechacur =String.format(dia+"-"+mes+"-"+anio)
            var horario  =  ClsMisCitas(codAtencion,cod_medico,cod_expediente,nombnredoctor,codEspecialidad,codSucursal,idHora,desHora,desEspecialidad,"",fechacur,nomSucursal,bd,precio_v,codDocumento,imptotal,cod_bd,situacion,num_serie,num_documento)
            interfaceCita?.onCallbackDos(horario)
        }else{

            var mensaje="Este cita ya esta pagada"
            interfaceCita?.onCallbackTres(mensaje)
        }
    }
}