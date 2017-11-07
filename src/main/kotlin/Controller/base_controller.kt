package app.controller

import Model.Data
import Model.findPatient
 import gui.model.Hospital
import gui.model.Hospitalization
import gui.model.Patient
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

open class BaseController : Controller() {
    val status = SimpleStringProperty()
    val insuranceComp by lazy { Data.insuranceCompanies.observable() }
    val hospitals     by lazy { Data.hospitals.getValuesInorder().observable() }

    val foundPatients    = mutableListOf<Patient>().observable()
    val hospitalizations = mutableListOf<Hospitalization>().observable()

    fun refresh(){
        insuranceComp.setAll(Data.insuranceCompanies)
        hospitals.setAll(Data.hospitals.getValuesInorder())
    }

    open fun clear(){
        foundPatients.clear()
        hospitalizations.clear()
    }
    open fun findPatient(birthNumber: String) {
        foundPatients.clear()
        Data.findPatient(birthNumber)?.let { foundPatients.add(it) }
    }

    open fun findPatient(name: String, surname: String) {
        foundPatients.clear()
        Data.findPatient(name, surname).let { foundPatients.setAll(it) }
    }

    open fun findPatient(name: Hospital, birthNumber: String) {
        foundPatients.clear()
        Data.findPatient(name, birthNumber)?.let { foundPatients.setAll(it) }
    }

    open fun findPatient(hospital: Hospital, firstName: String, lastName: String) {
        Data.findPatient(hospital, firstName, lastName).let {
            foundPatients.setAll(it)
        }
    }

    open  fun getHospitalizations(patient: Patient) {
        hospitalizations.clear()
        hospitalizations.setAll(patient.hospitalizations)
    }

    open fun findPatient(hospital: Hospital, name: String?, surname: String?, birthNumber: String?) {
        val results = mutableListOf<Patient>()
        birthNumber?.let { Data.findPatient(hospital, it)?.let { results.add(it) } }
        Data.findPatient(hospital, name ?: "", surname ?: "").let { results += it }
        foundPatients.setAll(results)
    }

    open fun findPatient(name: String?, surname: String?, birthNumber: String?) {
        val results = mutableListOf<Patient>()
        birthNumber?.let { Data.findPatient(it)?.let { results.add(it) } }
        Data.findPatient(name ?: "", surname ?: "").let { results += it }
        foundPatients.setAll(results)
    }


    open fun getHospitalizations(hospital: Hospital, patient: Patient) {
       val patientInHospital =  hospital.currentHospitalizations.get(patient) //?.hospitalizations?.let { hospitalizations.setAll(it) }
        if(patientInHospital!=null){
            patientInHospital.hospitalizations.let { hospitalizations.setAll(it) }
        }else{
            hospitalizations.clear()
        }
    }
}
