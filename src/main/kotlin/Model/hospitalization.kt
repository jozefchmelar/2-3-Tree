package gui.model

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
    val start: LocalDate
){
    override fun toString() = "${patient.birthNumber} ${patient.firstName} ${patient.lastName} $start $end $diagnosis"
}


class HospitalizationModel : ItemViewModel<Hospitalization>() {
    override fun onCommit() {
        super.onCommit()
        item = Hospitalization(patient.value, diagnosis.value, end.value, start.value)
    }

    val start   = bind(Hospitalization::start)
    val patient = bind(Hospitalization::patient)
    val end     = bind(Hospitalization::end)
    val diagnosis = bind(Hospitalization::diagnosis)
}


