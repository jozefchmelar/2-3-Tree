package app.controller

import Model.Data
import Model.findPatient
import gui.model.Hospital

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
}
