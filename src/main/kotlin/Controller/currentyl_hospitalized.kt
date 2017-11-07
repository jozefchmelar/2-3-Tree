package app.controller

import Model.Data
import Model.toMonthYear
import Tree.TwoThreeTree
import Tree.node.Node
import extensions.emptyLinkedList
import gui.model.Hospital
import gui.model.Hospitalization
import gui.model.InsuranceCompany
import gui.model.Patient
import javafx.beans.property.SimpleStringProperty
import org.jetbrains.annotations.Mutable
import java.time.LocalDate
import java.time.MonthDay
import javax.xml.datatype.DatatypeConstants.DAYS

class CurrentlyHospitalizedController : BaseController() {


    fun getHospitalizations(hospital: Hospital) {
        foundPatients.setAll(hospital.currentHospitalizations.getValuesInorder())
    }

    fun getHospitalizations(hospital: Hospital, insuranceCompany: InsuranceCompany) {
        println(insuranceCompany)
        foundPatients.setAll(hospital.currentInsuranceHospitalizations.get(insuranceCompany)?.sortedBy { it.birthNumber.toInt() } ?: emptyList<Patient>())
    }
}

class RangeHospitalizedController : BaseController() {

    fun getPatientsInHospitalFromTo(hospital: Hospital, from: LocalDate, to: LocalDate) {
        val start: List<Patient> = hospital.currentStartDateHospitalization.inInterval(from, to).flatten()
        foundPatients.setAll(start)
    }

}

class InvoiceController : BaseController() {
    /*
    /*
    vytvorenie podkladov pre účtovné oddelenie na tvorbu faktúr pre zdravotné poisťovne
    za zadaný mesiac. Pre každú poisťovňu, ktorej pacient (pacienti) bol v zadaný
    kalendárny mesiac hospitalizovaní aspoň jeden deň je potrebné pripraviť podklady
    obsahujúce:
     kód zdravotnej poisťovne
     počet dní hospitalizácii (za všetkých pacientov – napr. 98 dní)
     výpis hospitalizovaných pacientov v jednotlivé dni mesiaca spolu s diagnózami
     */
     */
    var totalDays = SimpleStringProperty()
    var invoice = SimpleStringProperty()

    fun getHospitalizations(monthYear: LocalDate, insurance: InsuranceCompany) {
        val found = Data.hospitalizationsInMonth.get(monthYear.toMonthYear())
        var days = 0.toLong()
        val asfasf = mutableListOf<Triple<Patient, Hospitalization, LocalDate>>()
        val daysInMonth = monthYear.month.length(monthYear.isLeapYear)
        val daco = TwoThreeTree<Int,MutableList<Pair<Patient,Hospitalization>>>()
        found?.forEach {
            days += java.time.temporal.ChronoUnit.DAYS.between(it.start, it.end ?: LocalDate.of(it.start.year,it.start.month.value,it.start.month.length(it.start.isLeapYear)))
            daco.get(it.start.dayOfMonth)?.add(Pair(it.patient,it)) ?: daco.put(it.start.dayOfMonth, mutableListOf(Pair(it.patient,it)))
        }

        totalDays.set(days.toInt().toString())
        invoice  .set(daco.formated())
        println(daco.formated())
    }

    fun TwoThreeTree<Int,MutableList<Pair<Patient,Hospitalization>>>.formated() : String {
        val sb = StringBuilder()
        val nvm = this.getValuesInorder().flatten()
        nvm.forEach {
            sb.append(it.second.start.dayOfMonth)
            sb.append(" ")
            sb.append(it.first.firstName)
            sb.append(" ")
            sb.append(it.first.lastName)
            sb.append(" ")
            sb.appendln(it.second.diagnosis)
        }
        return sb.toString()
    }
}