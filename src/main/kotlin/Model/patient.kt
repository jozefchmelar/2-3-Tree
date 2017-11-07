package gui.model

import extensions.emptyLinkedList
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import java.time.LocalDate
import java.util.*

data class InsuranceCompany(val id: Int, val name: String) : Comparable<InsuranceCompany>{

    override fun compareTo(other: InsuranceCompany): Int = id.compareTo(other.id)
    override fun equals(other: Any?) = other is InsuranceCompany && other.id == id
    override fun toString() = name
}

class InsuranceCompanyModel : ItemViewModel<InsuranceCompany>() {
    val id = bind(InsuranceCompany::id)
    val name = bind(InsuranceCompany::name)
}

data class Patient(
    val birthNumber: String,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val healthInsurance: InsuranceCompany,
    val hospitalizations: MutableList<Hospitalization> = emptyLinkedList()
) : Comparable<Patient> {

    override fun compareTo(other: Patient): Int = birthNumber.compareTo(other.birthNumber)
    override fun equals(other: Any?) = other is Patient && other.birthNumber==birthNumber
}


class PatientModel : ItemViewModel<Patient>() {
    override fun onCommit() {
        super.onCommit()
        item = Patient(
            birthNumber = birthNumber.value,
            firstName = firstName.value,
            lastName = lastName.value,
            birthDate = birthDate.value,
            healthInsurance = healthInsurance.value
        )
    }

    val birthNumber = bind(Patient::birthNumber)
    val firstName = bind(Patient::firstName)
    val lastName = bind(Patient::lastName)
    val birthDate = bind(Patient::birthDate)
    val healthInsurance = bind(Patient::healthInsurance)

}
