package com.clinicalaluz.clinicaapp.clases

import java.io.Serializable

data class ClsMisCitas  (val COD_ATENCION:String, val COD_MEDICO:String, val  COD_EXPEDIENTE:String, val DES_AUXILIAR:String,val COD_ESPECIALIDAD:String,val COD_SUCURSAL:String,val IDE_HORA:String,val DES_HORA:String,val DES_ESPECIALIDAD:String,val FEC_INSERCION:String,val FEC_ATENCION:String,val NOM_SUCURSAL:String,val BD:String, val PRECIO_V:String,val COD_DOCUMENTO:String,val IMP_TOTAL :String,val COD_BD:String) : Serializable
