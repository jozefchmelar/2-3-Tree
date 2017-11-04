package app.gui

import app.controller.AddPatientController
import gui.model.InsuranceCompany
import gui.model.PatientModel
import javafx.scene.layout.VBox
import tornadofx.*

class AddPatientView : View() {

    private val controller: AddPatientController by inject()
    private val patientModel: PatientModel         by inject()

    override val root = VBox()

    init {
        title = "Pridat pacienta"

        with(root) {
            goHome()

            form {
                fieldset("Personal Information") {
                    field("Meno") {
                        textfield().bind(patientModel.firstName)
                    }

                    field("Priezvisko") {
                        textfield().bind(patientModel.lastName)
                    }

                    field("Rodne cislo") {
                        textfield().bind(patientModel.birthNumber)
                    }

                    field("Poistovna") {
                        combobox<InsuranceCompany> {
                            items = controller.insuranceComp
                        }.bind(patientModel.healthInsurance)
                    }
                    field("Datum narodenia") {
                        datepicker().bind(patientModel.birthDate)
                    }


                }
                button("Save") {
                    setOnAction {
                        patientModel.commit()
                        controller.addPatient(patientModel.item)
                    }
                }

            }


        }
    }
}
