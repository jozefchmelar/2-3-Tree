package app.gui

import app.controller.RangeHospitalizedController
import gui.model.Hospital
import gui.model.Hospitalization
import gui.model.Patient
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*
import java.time.LocalDate

class RangeHospitalized : View() {
    override val root = VBox()
    val controller: RangeHospitalizedController by inject()
    var hospital = SimpleObjectProperty<Hospital>()
    var patient = SimpleObjectProperty<Patient>()
    var from = SimpleObjectProperty<LocalDate>()
    var to  = SimpleObjectProperty<LocalDate>()
    init {


        with(root) {

            goHome(controller::clear)


            hbox {
                vgrow = Priority.ALWAYS
                hgrow = Priority.ALWAYS
                maxWidth = Double.POSITIVE_INFINITY
                tableview(controller.hospitals) {
                    smartResize()
                    column("Nemocnica", Hospital::name)
                }.bindSelected(hospital)

                vbox {
                    vgrow = Priority.ALWAYS
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.POSITIVE_INFINITY
                    hbox{
                        vbox {
                            label("Od")
                            datepicker().bind(from)
                        }
                        vbox {
                            label("do")
                            datepicker().bind(to)
                        }
                        button("Hladaj"){
                            action{
                            controller.getPatientsInHospitalFromTo(hospital.value,from.value,to.value)
                                controller.clear()
                            }

                        }
                    }
                    tableview(controller.foundPatients) {
                        vgrow = Priority.ALWAYS
                        hgrow = Priority.ALWAYS
                        smartResize()
                        column("Rodne cislo", Patient::birthNumber).setComparator { s, sa ->s.toInt().compareTo(sa.toInt())  }
                        column("Meno", Patient::firstName)
                        column("Priezvisko", Patient::lastName)
                        column("Poistovna", Patient::healthInsurance)

                        onSelectionChange {
                            it?.let{patient.set(it)}
                            controller.getHospitalizations(hospital.value,patient.value)
                        }
                    }//.bindSelected(patient)

                    label("            ")
                    tableview(controller.hospitalizations) {
                        vgrow = Priority.ALWAYS
                        hgrow = Priority.ALWAYS
                        maxWidth = Double.POSITIVE_INFINITY
                        column("diagnosis", Hospitalization::diagnosis)
                        column("start", Hospitalization::start)
                        column("end", Hospitalization::end)
                        column("nemocnica", Hospitalization::hospital)
                        onSelectionChange {
                         //   test.set(it?.end==null)
                        }
                    }//.bindSelected(hospitalalization)
                }

            }
            style {
                padding = box(20.px)
            }
        }

    }

}