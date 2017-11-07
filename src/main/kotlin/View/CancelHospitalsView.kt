package app.gui

import Model.Data
import Tree.node.KeyValue
import app.controller.BaseController
import app.controller.CancelHospitalController
import app.controller.HospitalsController
import gui.model.Hospital
import gui.model.HospitalModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.VBox
import org.jetbrains.debugger.MemberFilter
import org.jetbrains.debugger.filterAndSort
import tornadofx.*

class CancelHospitalsView : View() {

    private var model = HospitalModel()
    private val controller: CancelHospitalController by inject()
    override val root = VBox()
    val from = HospitalModel()// by inject()
    val to = HospitalModel()// by inject()
    init {

        title = "Pridat pacienta"
        with(root) {
            borderpane {
                style {
                    padding = box(20.px)
                }
                top =  goHome()
                left = listview(controller.hospitals) {
                    bindSelected(from)
                }

                center = button("--->") {
                    action {
                        controller.cancelHospital(from.item,to.item)
                    }
                }
                right = listview(controller.hospitals) {
                    bindSelected(to)

                }
                bottom = button("refresh"){
                    action {
                        controller.refresh()
                    }
                }

            }

            style {
                padding = box(20.px)
            }
        }

    }
}

class TestController:BaseController()
class TestView :View(){
    private val controller: TestController by inject()

    override val root = VBox()
    init {
        with(root){
            goHome()
            button("Do stuff") {
                action {
//                    val neviem: List<KeyValue<String, Hospital>> =  Data.hospitals.intervalSearch("E","O",{
//                        println(it)
//                    })
//
//                    println(neviem)
                }
            }
        }
    }

}