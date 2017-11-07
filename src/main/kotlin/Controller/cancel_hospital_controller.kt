package app.controller

import Model.*
import gui.model.Hospital
import tornadofx.*

class CancelHospitalController : BaseController() {
    /*
    ata class Hospital(
        val name: String,
        val patients                         : TwoThreeTree<String, Patient> = TwoThreeTree(),
        val patientsFirstName                : TwoThreeTree<String, MutableList<Patient>> = TwoThreeTree(),
        val patientsLastName                 : TwoThreeTree<String, MutableList<Patient>> = TwoThreeTree(),
        val currentHospitalizations          : TwoThreeTree<Patient,Patient> = TwoThreeTree(),
        val currentInsuranceHospitalizations : TwoThreeTree<InsuranceCompany,MutableList<Patient>> = TwoThreeTree()

     */
    fun cancelHospital(from: Hospital, to: Hospital) {
        if(from==to ) return
        val patients = from.patients.insertedKeys.forEach { key ->
            from.patients.get(key)?.let { to.patients.put(key, it) }
        }
        val patientsFirstName = from.patientsFirstName.insertedKeys.forEach { key ->
            from.patientsFirstName.get(key)?.let { to.patientsFirstName.put(key, it) }
        }
        val patientsLastName = from.patientsLastName.insertedKeys.forEach { key ->
            from.patientsLastName.get(key)?.let { to.patientsLastName.put(key, it) }
        }
        val currentHospitalizations = from.currentHospitalizations.insertedKeys.forEach { key ->
            from.currentHospitalizations.get(key)?.let { to.currentHospitalizations.put(key, it) }
        }
        val currentInsuranceHospitalizations = from.currentInsuranceHospitalizations.insertedKeys.forEach { key ->
            from.currentInsuranceHospitalizations.get(key)?.let { to.currentInsuranceHospitalizations.put(key, it) }
        }
        Data.hospitals.delete(from.name)
        hospitals.setAll(Data.hospitals.getValuesInorder())

        status.set("OK")

    }

}