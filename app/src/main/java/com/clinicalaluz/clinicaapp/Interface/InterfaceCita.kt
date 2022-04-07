package com.clinicalaluz.clinicaapp.Interface

import com.clinicalaluz.clinicaapp.clases.ClsMisCitas
import com.clinicalaluz.clinicaapp.clases.Horario

interface InterfaceCita {
    fun onCallback(value: ClsMisCitas?)
    fun onCallbackDos(value: ClsMisCitas?)
    fun onCallbackTres(value: String)
    fun onCallDonwland(value:ClsMisCitas?)

}