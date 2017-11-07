package app.controller

import Model.Data
import Model.endHospitalization
import Model.findPatient
import gui.model.Hospital
import gui.model.Hospitalization
import gui.model.Patient

class PatientRecordController : BaseController() {

    override fun findPatient(birthNumber: String) {
        super.findPatient(birthNumber)
        hospitalizations.clear()
    }

    override fun findPatient(name: String, surname: String) {
        super.findPatient(name, surname)
        hospitalizations.clear()

    }

    override fun findPatient(name: Hospital, birthNumber: String) {
        super.findPatient(name, birthNumber)
        hospitalizations.clear()

    }

    override fun findPatient(hospital: Hospital, firstName: String, lastName: String) {
        super.findPatient(hospital, firstName, lastName)
        hospitalizations.clear()

    }

    fun endHospitalization(hospital: Hospital, patient: Patient, hospitalization: Hospitalization) : Boolean {
         Data.endHospitalization(hospital, patient,hospitalization)
            getHospitalizations(hospital,patient)
        return true
    }

}
