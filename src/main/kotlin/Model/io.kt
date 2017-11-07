package Model

import gui.model.Hospital
import gui.model.InsuranceCompany
import gui.model.Patient
import javafx.beans.binding.StringBinding
import java.io.File
import java.sql.Timestamp
import java.time.LocalDate
import java.util.*
import java.time.ZoneId
import java.time.temporal.TemporalQueries.localDate



enum class FileName(val value: String){
    Hospitals("hospitals.csv"),
    Patients("patients.csv"),
    Insurance("insurance.csv")
}

enum class Separator(val sep:String){
    Patient("#PPPPPPPPP#"),
    Hospitalization("#HHHHHHH$"),
    InsuranceComp("#$")
}
fun save(){
    saveInsurance()
    saveHospitals()
    savePatientWithHosp()
}

fun saveHospitals(){
    val hospitals = Data.hospitals.getValuesInorder().joinToString(",") { it.name }
    File(FileName.Hospitals.value).writeText(hospitals)
}

fun saveInsurance(){
    val insurance = Data.insuranceCompanies.sortedBy { it.id }.joinToString(Separator.InsuranceComp.sep) { "${it.id},${it.name}\n" }
    File(FileName.Insurance.value).writeText(insurance)
}
fun savePatientWithHosp(){
    val pat = Data.allPatients.getValuesInorder()
    val sb = StringBuilder()
    pat.forEach {
        val patient  =  "${it.birthNumber},${it.healthInsurance.id},${it.firstName},${it.lastName},${it.birthDate.epoch()}"
        sb.appendln(patient)
        it.hospitalizations.forEach {
            val a = "${it.hospital.name},${it.diagnosis},${it.start.epoch()},${it.end?.epoch()}"
            sb.appendln(a)
        }
        sb.appendln(Separator.Patient.sep)
    }
    File(FileName.Patients.value).writeText(sb.toString())
}

fun load(){
    loadInsurance()
    loadHospitals()
    loadPatientsAndStuff()
}

fun loadHospitals(){
    File(FileName.Hospitals.value)
        .readText()
        .split(",")
        .map { Hospital(it) }
        .forEach { Data.hospitals.put(it.name,it) }
}

fun loadInsurance(){
    File(FileName.Insurance.value)
        .readText()
        .split(Separator.InsuranceComp.sep)
        .map {
            val line = it.split(",")
            InsuranceCompany(line.first().toInt(),line.last())
        }.forEach {
        Data.insuranceCompanies.add(it)
    }
}

fun loadPatientsAndStuff(){
    File(FileName.Patients.value)
        .readText()
        .split(Separator.Patient.sep)
        .forEach {
            val lines = it.split("\n").toMutableList()
            val pl = lines.first()
            val pd = pl.split(",")
            //0,2,Francisco,Wilkerson,1510009200
            val p = Patient(
                birthNumber = pd[0],
                healthInsurance = Data.insuranceCompanies[pd[1].toInt()],
                firstName = pd[2],
                lastName = pd[3],
                birthDate = LocalDate.ofEpochDay(pd[4].toLong())
            )
            lines.removeF


        }
}

fun LocalDate.toDate() = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
var zoneId = ZoneId.systemDefault() // or: ZoneId.of("Europe/Oslo");
fun LocalDate.epoch() =  this.atStartOfDay(zoneId).toEpochSecond()