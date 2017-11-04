package app.controller

import Model.*
import gui.model.Hospital
import tornadofx.*

class HospitalsController : BaseController() {

    fun addHospital(hospitalName: String) {
        Data.insertHospital(Hospital(hospitalName))
        hospitals.clear()
        hospitals.addAll(Data.hospitals.getValuesInorder())
    }

}