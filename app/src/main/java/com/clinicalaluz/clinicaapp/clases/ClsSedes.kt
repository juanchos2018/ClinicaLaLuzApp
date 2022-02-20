package com.clinicalaluz.clinicaapp.clases

import java.io.Serializable

class ClsSedes (val COD_SUCURSAL:String, val NOM_SUCURSAL:String,val direccion:String,val telefono:String ,val img:String,val DIRECMAP:String) :
    Serializable
{
    override fun toString(): String = NOM_SUCURSAL
}
