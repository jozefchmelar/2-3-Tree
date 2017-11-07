package app.gui

import app.controller.InvoiceController
import app.controller.RangeHospitalizedController
import gui.model.*
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*
import java.time.LocalDate



class InvoiceView : View() {
    override val root = VBox()
    val controller: InvoiceController by inject()
    val insurance: InsuranceCompanyModel by inject()
    var insuranceId = SimpleStringProperty()
    var patient = SimpleObjectProperty<Patient>()
    var insru = SimpleObjectProperty<InsuranceCompany>()
    var from = SimpleObjectProperty<LocalDate>()

    init {

        with(root) {
            goHome(controller::clear)
            hbox {
                tableview(controller.insuranceComp) {
                    column("ID", InsuranceCompany::id)
                    column("Nazov", InsuranceCompany::name)
                    onSelectionChange {

                        it?.let {
                            controller.getHospitalizations(from.value,it)
                            insru.set(it)
                        }
                    }
                }
                vbox {
                    button("refresh") {
                        action {
                            controller.getHospitalizations(from.value,insru.value)
                        }
                    }
                    datepicker().bind(from)
                        hbox{
                            label("Pocet dni hospitalizacie ")
                            label(controller.totalDays)

                        }
                    label(controller.invoice){
                        maxHeight= Integer.MAX_VALUE.toDouble()
                    }

                }
                }
            }

        }


    }


