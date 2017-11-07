package gui.model

import Model.CSVable
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.time.LocalDate
import java.util.*
import tornadofx.getValue
import tornadofx.setValue

 data class Hospitalization(
    val patient: Patient,
    val diagnosis: String,
    val end: LocalDate? = null,
    val start: LocalDate,
    val hospital:Hospital

): CSVable {
     override fun toCsv() = listOf(patient,diagnosis,end,start,hospital).joinToString(",")
     override fun toString() = "${patient.birthNumber} ${patient.firstName} ${patient.lastName} $start $end $diagnosis $hospital"

}


class HospitalizationModel : ItemViewModel<Hospitalization>() {
    override fun onCommit() {
        super.onCommit()
        item = Hospitalization(patient.value, diagnosis.value,end.value, start.value,hospital.value)
    }

    val start   = bind(Hospitalization::start)
    val patient = bind(Hospitalization::patient)
    val end     =  bind(Hospitalization::end)
    val diagnosis = bind(Hospitalization::diagnosis)
    val hospital = bind(Hospitalization::hospital)
}


