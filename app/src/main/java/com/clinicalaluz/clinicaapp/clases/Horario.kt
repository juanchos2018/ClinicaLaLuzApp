package com.clinicalaluz.clinicaapp.clases

import java.io.Serializable

data class Horario(val COD_DOCUMENTO:String, val IDE_HORA:String, val DES_HORA:String, val COD_ESPECIALIDAD:String,val COD_AUXILIAR:String) : Serializable