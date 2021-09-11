package com.clinicalaluz.clinicaapp

import java.io.Serializable

class ClsSedes (val COD_SUCURSAL:String, val NOM_SUCURSAL:String) :
    Serializable
{
    override fun toString(): String = NOM_SUCURSAL
}
