package app.gui

import app.controller.PatientRecordController
import com.intellij.openapi.editor.SelectionModel
import gui.model.*
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.jdesktop.swingx.calendar.DateSelectionModel
import tornadofx.*


class PatienRecordView : View() {

    val controller: PatientRecordController by inject()
    val hospitalModel: HospitalModel by inject()
    val hospitalalization: HospitalizationModel by inject()
    val patient: PatientModel by inject()
    var birthNumber: TextField by singleAssign()
    var surname: TextField by singleAssign()
    var name: TextField by singleAssign()
    val test = SimpleBooleanProperty(false)

    override val root = VBox()

    init {
        with(root) {
            goHome(controller::clear)

            hbox {
                vgrow = Priority.ALWAYS
                hgrow = Priority.ALWAYS
                maxWidth = Double.POSITIVE_INFINITY
                tableview(controller.hospitals) {
                    smartResize()
                    column("Meno", Hospital::name)
                    bindSelected(hospitalModel)


                    onSelectionChange { hospital ->
                        if(hospital != null && patient.item!=null)
                            controller.findPatient(hospital,name.text,surname.text,birthNumber.text)
                    }
                }

                vbox {
                    vgrow = Priority.ALWAYS
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.POSITIVE_INFINITY
                    hbox {
                        birthNumber = textfield {
                            promptText = "Rodne cislo"
                        }
                        button("Hladaj") {
                            action {
                                if (hospitalModel.item != null)
                                    controller.findPatient(hospitalModel.item, birthNumber.text)
                                else
                                    controller.findPatient(birthNumber.text)
                                clearFields()

                            }
                        }
                    }
                    vbox {
                        hbox {
                            name = textfield {
                                promptText = "meno"
                            }

                            surname = textfield {
                                promptText = "priezvisko"
                            }
                            button("Hladaj") {
                                action {
                                    if (hospitalModel.item != null)
                                        controller.findPatient(hospitalModel.item, name.text, surname.text)
                                    else
                                        controller.findPatient(name.text, surname.text)

                                }
                            }
                        }

                    }

                    tableview(controller.foundPatients) {
                        vgrow = Priority.ALWAYS
                        hgrow = Priority.ALWAYS
                        maxWidth = Double.POSITIVE_INFINITY
                        smartResize()
                        column("Rc", Patient::birthNumber)
                        column("Meno", Patient::firstName)
                        column("Priezvisko", Patient::lastName)
                        column("Datum narodenia", Patient::birthDate)
                        column("Poistovna", Patient::healthInsurance)
                        bindSelected(patient)
                        onSelectionChange {
                            it?.let { controller.getHospitalizations(it) }
                        }
                    }
                }

                hbox {
                    tableview(controller.hospitalizations) {
                        vgrow = Priority.ALWAYS
                        hgrow = Priority.ALWAYS
                        maxWidth = Double.POSITIVE_INFINITY
                        column("diagnosis", Hospitalization::diagnosis)
                        column("start", Hospitalization::start)
                        column("end", Hospitalization::end)
                        column("nemocnica", Hospitalization::hospital)
                        onSelectionChange {
                            test.set(it?.end==null)
                        }
                    }.bindSelected(hospitalalization)
                    button("Ukonci hospitalizaciu"){
                        enableWhen(test)
                        action {
                            test.set(controller.endHospitalization(hospitalModel.item, patient.item, hospitalalization.item))
                        }
                    }
                }
            }
            style {
                padding = box(20.px)
            }
        }

    }

    private fun clearFields() {
        birthNumber.clear()
        name.clear()
        surname.clear()
    }
}
