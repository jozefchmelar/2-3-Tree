package app.gui


import app.controller.HospitalsController
import gui.model.HospitalModel
import javafx.scene.layout.VBox
import tornadofx.*



class HospitalsView : View() {

    private var model = HospitalModel()
    private val controller: HospitalsController by inject()
    override val root = VBox()

    init {
        title = "Pridat pacienta"
        with(root) {
            borderpane {
                style {
                    padding = box(20.px)
                }
                top = goHome()
                left = form {
                    useMaxSize = true
                    useMaxHeight = true
                    useMaxWidth = true
                    fieldset("Pridanie nemocnice") {
                        field("Name") {
                            textfield().bind(model.name)
                        }


                    }
                    button("Save") {
                        setOnAction {
                            model.commit()
                            controller.addHospital(model.name.value)
                        }
                    }

                }

                center = vbox {
                    label("Nemocnice")
                    listview(controller.hospitals)
                }

            }

            style {
                padding = box(20.px)
            }
        }

    }
}