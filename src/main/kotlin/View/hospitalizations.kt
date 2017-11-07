package app.gui


import app.controller.HospitalizationController
import gui.model.*
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*
import java.time.LocalDate

class HospitalizationsView : View() {

    private val controller: HospitalizationController       by inject()

    private var firstNameField: TextField            by singleAssign()
    private var lastNameField: TextField            by singleAssign()
    private var birthNumberField: TextField            by singleAssign()
    private var foundPatients: TableView<Patient>   by singleAssign()

    private val hospitalizationModel: HospitalizationModel by inject()
    private val hospital: HospitalModel        by inject()
    private val patient: PatientModel         by inject()

    override val root = VBox()

    init {
        goHome(controller::clear)
        hbox {
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
            maxWidth = Double.POSITIVE_INFINITY
            vbox {
                vgrow = Priority.ALWAYS
                hgrow = Priority.ALWAYS
                maxWidth = Double.POSITIVE_INFINITY
                label("Nemocnica")
                tableview(controller.hospitals) {
                    vgrow = Priority.ALWAYS
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.POSITIVE_INFINITY
                    column("Nazov", Hospital::name)
//                    bindSelected(hospital)
//                    bindSelected(hospitalizationModel.hospital)
//                    onSelectionChange {
//                        hospital.value = it
//                        hospitalizationModel.value = it
//                    }
                    selectionModel.selectedItemProperty().onChange {
                        hospital.item = it
                    //    hospitalizationModel.item.hospital = it
                    }
                }
            }
            vbox {
                vgrow = Priority.ALWAYS
                hgrow = Priority.ALWAYS
                maxWidth = Double.POSITIVE_INFINITY
                hbox {
                    firstNameField = textfield {
                        promptText = "meno"
                    }
                    lastNameField = textfield {
                        promptText = "priezvisko"
                    }

                    button("Najdi") {
                        action {
                            controller.findPatient(firstNameField.text, lastNameField.text)
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
                    vgrow = Priority.ALWAYS
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.POSITIVE_INFINITY
                    vgrow = Priority.ALWAYS
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.POSITIVE_INFINITY
                    column("Rodne cislo", Patient::birthNumber)
                    column("Meno", Patient::firstName)
                    column("Priezvisko", Patient::lastName)
                    column("Poistovna", Patient::healthInsurance)
                    onUserSelect {
                        controller.getHospitalizations(hospital.item, it)
                    }
                    bindSelected(patient)

                }

            }
            vbox {
                vgrow = Priority.ALWAYS
                hgrow = Priority.ALWAYS
                maxWidth = Double.POSITIVE_INFINITY
                form {

                    fieldset("Pridanie hospitalizacie") {
                        field("Diagnoza") {
                            textfield().bind(hospitalizationModel.diagnosis)
                        }

                        field("Zaciatok") {
                            datepicker {
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
                        hospitalizationModel.hospital.value = hospital.item

                        hospitalizationModel.commit()
                        controller.addHospitalization(hospital.item, patient.item, hospitalizationModel.item)
                    }
                }

            }
        }

    }
}