package com.clinicalaluz.clinicaapp.clases

import java.io.Serializable

data class ClsMisCitas  (val COD_ATENCION:String, val COD_MEDICO:String,val DES_AUXILIAR:String,val COD_ESPECIALIDAD:String,val COD_SUCURSAL:String,val DES_HORA:String,val DES_ESPECIALIDAD:String,val FEC_INSERCION:String,val FEC_ATENCION:String,val NOM_SUCURSAL:String) : Serializable
