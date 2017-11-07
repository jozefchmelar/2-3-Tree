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
val logEnabled = false

fun log(string:String) = if(logEnabled) println(string) else {}

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
    log("$patient inserted")
}

fun Data.insertHospital(hospital: Hospital) {
    hospitals.put(hospital.name, hospital)
}

fun Data.startHospitalization(hospital: Hospital,patient: Patient,hospitalization: Hospitalization){
    insertPatientToHospital(patient,hospital)
    hospital.currentHospitalizations.put(patient,patient)// ?:  hospital.currentHospitalizations.put(patient,mutableListOf(patient))

    hospital.currentInsuranceHospitalizations.get(patient.healthInsurance)?.add(patient)?: hospital.currentInsuranceHospitalizations.put(patient.healthInsurance, mutableListOf(patient))
    patient.hospitalizations.add(hospitalization)
}

fun Data.endHospitalization(hospital: Hospital,patient: Patient ,hospitalization: Hospitalization) : Boolean{
    patient.hospitalizations.remove(hospitalization)
    patient.hospitalizations.add(hospitalization.copy(end = LocalDate.now()))
        hospital.currentHospitalizations.delete(patient)

    hospital.currentInsuranceHospitalizations.get(patient.healthInsurance)?.remove(patient)
    return true

}

fun Data.insertPatientToHospital(patient: Patient, hospital: Hospital) {
    val hos = hospitals.get(hospital.name)
    hos?.apply {
        patients.put(patient.birthNumber, patient)
        patientsFirstName.get(patient.firstName)?.add(patient) ?: patientsFirstName.put(patient.firstName, mutableListOf(patient))
        patientsLastName. get(patient.lastName)?.add(patient)  ?: patientsFirstName.put(patient.lastName , mutableListOf(patient))
    }
    log("$patient inserted to $hospital")
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
    println("Generating data...")
    val insuranceCompanies = mutableListOf(
        InsuranceCompany(1, "Vseobecna zdravotna"),
        InsuranceCompany(2, "Dovera"),
        InsuranceCompany(3, "Union"),
        InsuranceCompany(4, "Rick&Morty"))

    insuranceCompanies.forEach { Data.insuranceCompanies.add(it) }
    val pocetPacientov = 2000
    val patients = (1..pocetPacientov).mapIndexed { index, i -> Patient("$index", randomFirst(), randomLast(), LocalDate.now(), insuranceCompanies[Random().nextInt(insuranceCompanies.size)]) }
    val hospitals = (1..10).map { Hospital(randomName()) }
    fun randomHospital() = hospitals[rnd().nextInt(hospitals.size)]

    hospitals.forEach {
        Data.insertHospital(it)
    }

    patients.forEachIndexed { index, patient ->
     //   if(index%500==0) println("${ index.toDouble() / pocetPacientov.toDouble() *100} %")
        Data.insertPatient(patient)
        if (patient.birthNumber.toInt() < 4000) {
            for(i in 1..10) {
                maybe{
                    val h = randomHospital()
                    val hosp = Hospitalization(patient, randomSentence(),  null, rndLocalDate(), h)
                    Data.startHospitalization(h, patient, hosp)
                    Data.endHospitalization  (h, patient, hosp)
                }
            }
            maybe {
                val h = randomHospital()
                val hosp = Hospitalization(patient, randomSentence(),  null, rndLocalDate(), h)
                Data.startHospitalization(h, patient, hosp)
            }
        }
    }



}
//year month day
fun rnd()= Random(5)
fun rndLocalDate() = LocalDate.of(1980+rnd().nextInt(37),rnd().nextInt(11)+1,rnd().nextInt(27)+1)
fun maybe(f:()->Unit) = if(rnd().nextBoolean()) f() else {}