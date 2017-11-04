package app.controller

import Model.Data
import Model.insertPatient
import gui.model.Patient
import gui.model.PatientModel
import tornadofx.*

class AddPatientController : BaseController() {

    fun addPatient(p: Patient) {
        val patient = Patient(
            birthNumber = p.birthNumber,
            firstName = p.firstName,
            lastName = p.lastName,
            birthDate = p.birthDate,
            healthInsurance = p.healthInsurance
        )

        Data.insertPatient(patient)
    }

}