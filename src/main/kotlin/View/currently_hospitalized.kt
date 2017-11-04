package app.gui

import app.controller.CurrentlyHospitalizedController
import gui.model.*
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import tornadofx.*

class CurrentlyHospitalized : View() {
    override val root = VBox()
    val controller: CurrentlyHospitalizedController by inject()
    var insurance = SimpleObjectProperty<InsuranceCompany>()
    var hospital  = SimpleObjectProperty<Hospital>()

    init {

        with(root) {
            goHome()
            hbox {
                useMaxWidth = true
                useMaxHeight = true
                tableview(controller.hospitals) {
                    smartResize()
                    column("Nemocnica", Hospital::name)
                    onUserSelect {
                        if(insurance.value==null)
                            controller.getHospitalizations(it)
                        else
                            controller.getHospitalizations(it,insurance.value)

                    }
                }.bindSelected(hospital)
                vbox {
                    combobox<InsuranceCompany> {
                        items = controller.insuranceComp
                        promptText = "Fitler poistovni"
                    }.bind(insurance)


                    tableview(controller.foundPatients) {
                        smartResize()
                        column("Rodne cislo", Patient::birthNumber)
                        column("Meno", Patient::firstName)
                        column("Priezvisko", Patient::lastName)
                        column("Poistovna", Patient::healthInsurance)
                    }
                }

            }
        }

    }

}