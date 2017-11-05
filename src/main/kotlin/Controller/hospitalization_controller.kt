package app.controller

import Model.Data
import Model.findPatient
import Model.insertPatientToHospital
import Model.startHospitalization
import gui.model.*
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

class HospitalizationController : BaseController() {

    fun addHospitalization(hospital: Hospital, patient: Patient, hospitalization: Hospitalization) {
        Data.startHospitalization(hospital, patient, hospitalization)
    }
}


