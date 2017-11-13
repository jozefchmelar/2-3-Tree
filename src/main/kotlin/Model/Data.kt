package Model

import Tree.TwoThreeTree
import app.gui.Generator
import gui.model.Hospital
import gui.model.Hospitalization
import gui.model.InsuranceCompany
import gui.model.Patient
import org.jetbrains.concurrency.runAsync
import util.*
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
    val hospitalizationsInMonth = TwoThreeTree<MontYear,MutableList<Hospitalization>>()
}

data class MontYear(var month: Int, var year: Int) : Comparable<MontYear> {
    override fun compareTo(other: MontYear): Int {
        return when {
            year > other.year -> 1
            year < other.year -> -1
            year == other.year -> month.compareTo(other.month)
            else -> TODO()
        }
    }
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
    hospital.currentStartDateHospitalization.get(hospitalization.start)?.add(patient)      ?:  hospital.currentStartDateHospitalization.put(hospitalization.start,mutableListOf(patient))
    hospital.currentInsuranceHospitalizations .get(patient.healthInsurance)?.add(patient)  ?: hospital.currentInsuranceHospitalizations.put(patient.healthInsurance, mutableListOf(patient))
    hospitalizationsInMonth.get(hospitalization.start.toMonthYear())?.add(hospitalization) ?: hospitalizationsInMonth.put(hospitalization.start.toMonthYear(), mutableListOf(hospitalization))
    patient.hospitalizations.add(hospitalization)
}

fun Data.endHospitalization(hospital: Hospital,patient: Patient ,hospitalization: Hospitalization) : Boolean{
    patient.hospitalizations.remove(hospitalization)
    patient.hospitalizations.add(hospitalization.copy(end = LocalDate.now()))
    hospital.currentHospitalizations.delete(patient)
    if(hospitalization.end!=null)
        hospital.currentEndDateHospitalization.get(hospitalization.end)  ?.add(patient) ?: hospital.currentEndDateHospitalization.put(hospitalization.end, mutableListOf(patient))
    hospital.currentInsuranceHospitalizations.get(patient.healthInsurance)?.remove(patient)
    return true
}

fun Data.insertPatientToHospital(patient: Patient, hospital: Hospital) {
    val hos = hospitals.get(hospital.name)
    hos?.apply {
        patients           .put(patient.birthNumber, patient)
        patientsFirstName  .get(patient.firstName)?.add(patient)  ?: patientsFirstName.put(patient.firstName, mutableListOf(patient))
        patientsLastName   . get(patient.lastName)?.add(patient)  ?: patientsFirstName.put(patient.lastName , mutableListOf(patient))
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

val gen = Generator(10,5000,4000,5)

fun genDummy(g: Generator=gen) {
    println("Generating data...")
    val insuranceCompanies = (0..g.numberOfInsurance).map { InsuranceCompany(it, randomName()) }

    insuranceCompanies.forEach { Data.insuranceCompanies.add(it) }
    val pocetPacientov = g.numberOFPatients
    val patients = (1..pocetPacientov).mapIndexed { index, i -> Patient("$index", randomFirst(), randomLast(), LocalDate.now(), insuranceCompanies[Random().nextInt(insuranceCompanies.size)]) }
    val hospitals = (1..g.numberOfHospitals).mapIndexed { index, i -> Hospital("${randomName()} $i ") }
    fun randomHospital() = hospitals[rnd().nextInt(hospitals.size)]

    hospitals.forEach {
        Data.insertHospital(it)
    }
    patients.forEachIndexed { index, patient ->
        if (index % 500 == 0) println("${index.toDouble() / pocetPacientov.toDouble() * 100} %")
      //  println("insertPatient")
        Data.insertPatient(patient)
        if (patient.birthNumber.toInt() < g.numberOFHospitalizedPatients) {
            for (i in 1..3) {
                maybe {
                    val h = randomHospital()
                    val hosp = Hospitalization(patient, randomSentence(), null, rndLocalDate(), h)
                  //  println("startHospitalization")

                    Data.endHospitalization(h, patient, hosp)
                //    println("insertPatient")
                    Data.endHospitalization(h, patient, hosp)
                }
             }
            maybe {
                val h = randomHospital()
                val hosp = Hospitalization(patient, randomSentence(), null, rndLocalDate(), h)
                Data.startHospitalization(h, patient, hosp)
            }
        }
    }
    println("100%")
}

//year month day
fun rnd(r: Long? = null) = if (r == null) Random() else Random(r)

fun rndLocalDate() = LocalDate.of(2010+rnd().nextInt(7),rnd().nextInt(11)+1,rnd().nextInt(27)+1)
fun maybe(f:()->Unit) = if(rnd().nextBoolean()) f() else {}

fun LocalDate.toMonthYear() = MontYear(month=this.monthValue,year = this.year)

interface CSVable{
    fun toCsv() :String
}

inline fun testFun(name: String,crossinline f: () -> Unit) = {
    println(name)
    f()
    println(name)
}


