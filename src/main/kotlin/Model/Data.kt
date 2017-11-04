package Model

import Tree.TwoThreeTree
import gui.model.Hospital
import gui.model.Hospitalization
import gui.model.InsuranceCompany
import gui.model.Patient
import util.*
import tornadofx.*
import java.time.LocalDate
import java.util.*

object Data {
    val insuranceCompanies = mutableListOf<InsuranceCompany>()
    val allPatients = TwoThreeTree<String, Patient>()
    val allPatientsNames = TwoThreeTree<String, MutableList<Patient>>()
    val allPatientsSurnames = TwoThreeTree<String, MutableList<Patient>>()
    val hospitals = TwoThreeTree<String, Hospital>()
}

fun Data.insertPatient(patient: Patient) {
    allPatients.put(patient.birthNumber, patient)
    allPatientsNames.get(patient.firstName)?.add(patient)   ?: allPatientsNames.put(patient.firstName, mutableListOf(patient))
    allPatientsSurnames.get(patient.lastName)?.add(patient) ?: allPatientsSurnames.put(patient.lastName, mutableListOf(patient))
    println("$patient inserted")

}

fun Data.insertHospital(hospital: Hospital) {
    hospitals.put(hospital.name, hospital)
}

fun Data.startHospitalization(hospital: Hospital,patient: Patient,hospitalization: Hospitalization){
    insertPatientToHospital(patient,hospital)
    hospital.currentHospitalizations.put(patient,patient)
    hospital.currentInsuranceHospitalizations.get(patient.healthInsurance)?.add(patient)?: hospital.currentInsuranceHospitalizations.put(patient.healthInsurance, mutableListOf(patient))
    patient.hospitalizations.add(hospitalization)
}

fun Data.endHospitalization(hospital: Hospital,patient: Patient){
    hospital.currentHospitalizations.delete(patient)
}

fun Data.insertPatientToHospital(patient: Patient, hospital: Hospital) {
    val hos = hospitals.get(hospital.name)
    hos?.apply {
        patients.put(patient.birthNumber, patient)
        patientsFirstName.get(patient.firstName)?.add(patient) ?: patientsFirstName.put(patient.firstName, mutableListOf(patient))
        patientsLastName.get(patient.lastName)?.add(patient) ?: patientsFirstName.put(patient.lastName, mutableListOf(patient))
    }
    println("$patient inserted to $hospital")
}

fun Data.findPatient(birthNumber: String) = allPatients.get(birthNumber)

fun Data.findPatient(name: String,surname: String) = (allPatientsNames.get(name) ?: emptyList<Patient>()) +  (allPatientsSurnames.get(surname)?: emptyList())

fun Data.findPatient(hospital: Hospital, birthNumber: String) = hospitals.get(hospital.name)?.patients?.get(birthNumber)

fun Data.findPatient(hospital: Hospital, name: String, surname: String): List<Patient> {
    val names    = hospital.patientsFirstName.get(name)   ?: emptyList<Patient>()
    val surnames = hospital.patientsLastName.get(surname) ?: emptyList<Patient>()
    return names + surnames
}


fun genDummy() {
    val insuranceCompanies = mutableListOf(
        InsuranceCompany(1, "Vseobecna zdravotna"),
        InsuranceCompany(2, "Dovera"),
        InsuranceCompany(3, "Union"),
        InsuranceCompany(4, "Rick&Morty"))
    insuranceCompanies.forEach { Data.insuranceCompanies.add(it) }

    val patients = (1..500).mapIndexed { index, i -> Patient("$index", randomFirst(), randomLast(), LocalDate.now(), insuranceCompanies[Random().nextInt(insuranceCompanies.size)]) }
    val hospitals = (1..5).map { Hospital(randomName()) }

    patients.forEach {
        Data.insertPatient(it)
    }
    hospitals.forEach {
        Data.insertHospital(it)
    }
}