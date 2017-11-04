package app.gui


import app.controller.HospitalizationController
import gui.model.*
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import tornadofx.*
import java.time.LocalDate

class HospitalizationsView : View() {

    private val controller : HospitalizationController       by inject()

    private var firstNameField       : TextField            by singleAssign()
    private var lastNameField        : TextField            by singleAssign()
    private var birthNumberField     : TextField            by singleAssign()
    private var foundPatients        : TableView<Patient>   by singleAssign()

    private val hospitalizationModel : HospitalizationModel by inject()
    private val hospital             : HospitalModel        by inject()
    private val patient              : PatientModel         by inject()

    override val root = VBox()

    init {
        goHome()
        hbox {
            vbox {
                label("Nemocnica")
                tableview(controller.hospitals) {
                    column("Nazov", Hospital::name)
                    bindSelected(hospital)
                }
            }
            vbox {
                hbox {
                    firstNameField = textfield {
                        promptText = "meno"

                    }
                    lastNameField = textfield {
                        promptText = "priezvisko"

                    }
                    button("Najdi") {
                        action {
                            controller.findPatient(firstNameField.text,lastNameField.text)
                        }
                    }
                }
                hbox {
                    birthNumberField = textfield {
                        promptText = "rodne cislo"
                    }
                    button("Najdi") {
                        action {
                            controller.findPatient(birthNumberField.text)
                        }
                    }
                }

                foundPatients = tableview(controller.foundPatients) {
                    column("Rodne cislo", Patient::birthNumber)
                    column("Meno", Patient::firstName)
                    column("Priezvisko", Patient::lastName)
                    column("Poistovna", Patient::healthInsurance)

                    bindSelected(patient)

                }

            }
            vbox {
                form {

                    fieldset("Pridanie hospitalizacie") {
                        field("Diagnoza") {
                            textfield().bind(hospitalizationModel.diagnosis)
                        }

                        field("Zaciatok") {
                            datepicker{
                                value = LocalDate.now()
                            }.bind(hospitalizationModel.start)
                        }

                        field("Koniec") {
                            datepicker().bind(hospitalizationModel.end)
                        }
                    }

                }
                button("Pridat hospitalizaciu") {
                    action {
                        hospitalizationModel.patient.value = patient.item
                        hospitalizationModel.commit()
                            controller.addHospitalization(hospital.item, patient.item, hospitalizationModel.item)
                    }
                }

            }
        }
    }
}