package gui.model

import Tree.TwoThreeTree
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
    val patients: TwoThreeTree<String, Patient> = TwoThreeTree(),
    val patientsFirstName: TwoThreeTree<String, MutableList<Patient>> = TwoThreeTree(),
    val patientsLastName: TwoThreeTree<String, MutableList<Patient>> = TwoThreeTree(),
    val currentHospitalizations: TwoThreeTree<Patient,Patient> = TwoThreeTree(),
    val currentInsuranceHospitalizations: TwoThreeTree<InsuranceCompany,MutableList<Patient>> = TwoThreeTree()

)

class HospitalModel : ItemViewModel<Hospital>() {
    val name = bind(Hospital::name)
    val patients = bind(Hospital::patients)
}
