package app.gui

import app.controller.PatientRecordController
import gui.model.*
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import tornadofx.*
data class lll(val l:String)

class PatienRecordView : View() {

    val controller: PatientRecordController by inject()
    val hospitalModel: HospitalModel by inject()
    val patient: PatientModel by inject()
    var birthNumber: TextField by singleAssign()
    var surname: TextField by singleAssign()
    var name: TextField by singleAssign()
    var hospitals: TableView<Hospital> by singleAssign()


    override val root = VBox()

    init {
        with(root) {
            goHome()

            hbox {
                tableview(controller.hospitals) {
                    smartResize()
                    column("Meno", Hospital::name)
                    bindSelected(hospitalModel)
                }

                vbox {
                    hbox {
                        birthNumber = textfield {
                            promptText = "Rodne cislo"
                        }
                        button("Hladaj") {
                            action {
                                if(hospitalModel.item!=null)
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
                        }
                        button("Hladaj") {
                            action {
                                if(hospitalModel.item !=null)
                                    controller.findPatient(hospitalModel.item, name.text, surname.text)
                                else
                                    controller.findPatient(name.text, surname.text)

                            }
                        }
                    }

                    tableview(controller.foundPatients) {
                        smartResize()
                        column("Rc", Patient::birthNumber)
                        column("Meno", Patient::firstName)
                        column("Priezvisko", Patient::lastName)
                        column("Datum narodenia", Patient::birthDate)
                        column("Poistovna", Patient::healthInsurance)
                        bindSelected(patient)
                        onSelectionChange {
                            if (it != null)
                                controller.getHospitalizations(it)
                        }
                    }
                }
                tableview(controller.hospitalizations) {
                    column("diagnosis", Hospitalization::diagnosis)
                    column("start", Hospitalization::start)
                    column("end", Hospitalization::end)
                }
            }
        }

    }

    private fun clearFields() {
        birthNumber.clear()
        name.clear()
        surname.clear()
    }
}
