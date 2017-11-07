package Model

import gui.model.Hospital
import gui.model.Hospitalization
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
        val patient  =  "${it.birthNumber},${it.healthInsurance.id},${it.firstName},${it.lastName},${it.birthDate.dayOfWeek.value},${it.birthDate.monthValue},${it.birthDate.year}"
        sb.appendln(patient)
        it.hospitalizations.forEach {
            val a = "${it.hospital.name},${it.diagnosis},${it.start.dayOfWeek.value},${it.start.monthValue},${it.start.year},${it.end?.dayOfWeek?.value},${it.end?.monthValue},${it.end?.year}"
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
        .forEach { Data.insertHospital(it) }
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
            val lines = it.split("\n").filter { it.isNotBlank() }.toMutableList()
            if(lines.isNotEmpty()) {
                val pl = lines.first()
                val pd = pl.split(",")
                //0,2,Francisco,Wilkerson,1510009200
                val p = Patient(
                    birthNumber = pd[0],
                    healthInsurance = Data.insuranceCompanies[pd[1].toInt()],
                    firstName = pd[2],
                    lastName = pd[3], //rok mesiac den ... a ja to mam den4 mesiac5 rok6...fuck  ...
                    birthDate = LocalDate.of(pd[6].toInt(),pd[5].toInt(),pd[4].toInt())
                )
                lines.removeAt(0)
                Data.insertPatient(p)
                //${it.hospital.name},${it.diagnosis},${it.start.epoch()},${it.end?.epoch()}"

                //Tonibu, Ej cis pabolrak wafi maor sor geh ku tetovara ti itukod sehievi lof tesca fic vag.,1120514400,1510009200
                val hosp = lines.map {
                    val l = it.split(",")
                    Hospitalization(
                        hospital = Data.hospitals.get(l[0])!!,
                        diagnosis = l[1],  //den2,mesiac3,rok4 ...
                        start = LocalDate.of(l[4].toInt(),l[3].toInt(),l[2].toInt()),  //den5 mesiac6 rok7
                        end = if(l[5]=="null") null else LocalDate.of(l[7].toInt(),l[6].toInt(),l[5].toInt()),
                        patient = p
                    )
                }.forEach {
                    p.hospitalizations.add(it)
                    Data.insertPatientToHospital(p, it.hospital)
                    if (it.end == null){
                        it.hospital.currentHospitalizations.put(p, p)
                        it.hospital.currentStartDateHospitalization.get(it.start)?.add(p)   ?:  it.hospital.currentStartDateHospitalization.put(it.start,mutableListOf(p))

                    } else{
                        it.hospital.currentEndDateHospitalization.get(it.end)?.add(p)   ?:  it.hospital.currentEndDateHospitalization.put(it.end,mutableListOf(p))
                    }
                    Data.hospitalizationsInMonth.get(it.start.toMonthYear())?.add(it) ?: Data.hospitalizationsInMonth.put(it.start.toMonthYear(), mutableListOf(it))


                }
            }
        }
}
fun LocalDate.toDate() = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
var zoneId = ZoneId.systemDefault() // or: ZoneId.of("Europe/Oslo");
fun LocalDate.epoch() =  this.atStartOfDay(zoneId).toEpochSecond()