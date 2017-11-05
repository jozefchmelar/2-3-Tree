package app.gui

import app.controller.CurrentlyHospitalizedController
import gui.model.*
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*

class CurrentlyHospitalized : View() {
    override val root = VBox()
    val controller: CurrentlyHospitalizedController by inject()
    var insurance = SimpleObjectProperty<InsuranceCompany>()
    var hospital = SimpleObjectProperty<Hospital>()

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
                    onUserSelect {
                        if (insurance.value == null)
                            controller.getHospitalizations(it)
                        else
                            controller.getHospitalizations(it, insurance.value)
                    }
                    onUserDelete {
                        
                    }
                }.bindSelected(hospital)

                vbox {
                    vgrow = Priority.ALWAYS
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.POSITIVE_INFINITY

                    combobox<InsuranceCompany> {
                        items = controller.insuranceComp
                        promptText = "Fitler poistovni"
                        onDoubleClick {
                            insurance.set(null)
                            controller.getHospitalizations(hospital.value)

                        }
                    }.bind(insurance)


                    tableview(controller.foundPatients) {
                        vgrow = Priority.ALWAYS
                        hgrow = Priority.ALWAYS
                        smartResize()
                        column("Rodne cislo", Patient::birthNumber)
                        column("Meno", Patient::firstName)
                        column("Priezvisko", Patient::lastName)
                        column("Poistovna", Patient::healthInsurance)
                    }
                }

            }
            style {
                padding = box(20.px)
            }
        }

    }

}