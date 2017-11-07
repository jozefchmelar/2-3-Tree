package gui.model

import Model.CSVable
import Tree.TwoThreeTree
import java.time.LocalDate
import extensions.emptyLinkedList
import gui.model.Hospitalization
import gui.model.Patient
import tornadofx.*

/*
Pre každú nemocnicu evidujte:
 názov nemocnice
 záznamy o všetkých hospitalizáciách
 */
data class Hospital(
    val name: String,
    val patients                         : TwoThreeTree<String, Patient> = TwoThreeTree(),
    val patientsFirstName                : TwoThreeTree<String, MutableList<Patient>> = TwoThreeTree(),
    val patientsLastName                 : TwoThreeTree<String, MutableList<Patient>> = TwoThreeTree(),
    val currentHospitalizations          : TwoThreeTree<Patient,Patient> = TwoThreeTree(),
    val currentInsuranceHospitalizations : TwoThreeTree<InsuranceCompany,MutableList<Patient>> = TwoThreeTree(),
    val currentStartDateHospitalization  : TwoThreeTree<LocalDate,MutableList<Patient>> = TwoThreeTree(),
    val currentEndDateHospitalization    : TwoThreeTree<LocalDate,MutableList<Patient>> = TwoThreeTree()


):CSVable {
    override fun toCsv() = listOf(name,patients,patientsFirstName,patientsLastName,currentHospitalizations,currentInsuranceHospitalizations).joinToString(",")
    override fun toString(): String = name
    override fun equals(other: Any?)= other is Hospital && other.name.toLowerCase() ==  name.toLowerCase()
}

class HospitalModel : ItemViewModel<Hospital>() {
    val name     = bind(Hospital::name)
    val patients = bind(Hospital::patients)
}
