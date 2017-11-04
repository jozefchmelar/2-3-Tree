package app.controller

import Model.Data
import Model.findPatient
import extensions.emptyLinkedList
import gui.model.Hospital
import gui.model.Hospitalization
import gui.model.Patient
import tornadofx.*

abstract class BaseController : Controller() {

    val insuranceComp by lazy { Data.insuranceCompanies.observable() }
    val hospitals     by lazy { Data.hospitals.getValuesInorder().observable() }

    val foundPatients    = mutableListOf<Patient>().observable()
    val hospitalizations = mutableListOf<Hospitalization>().observable()

    fun findPatient(birthNumber: String) {
        foundPatients.clear()
        Data.findPatient(birthNumber)?.let { foundPatients.add(it) }
    }

    fun findPatient(name: String,surname:String) {
        foundPatients.clear()
        Data.findPatient(name,surname).let { foundPatients.setAll(it) }
    }

    fun findPatient(name: Hospital,birthNumber: String) {
        foundPatients.clear()
        Data.findPatient(name,birthNumber).let { foundPatients.setAll(it) }
    }

    fun findPatient(hospital: Hospital, firstName: String, lastName: String) {
        Data.findPatient(hospital,firstName,lastName).let {
            foundPatients.setAll(it)
        }
    }

    fun getHospitalizations(patient: Patient){
        hospitalizations.clear()
        hospitalizations.setAll(patient.hospitalizations)
    }


}
