package app.gui

import app.controller.GeneratorController
import javafx.scene.layout.VBox
import org.controlsfx.control.Notifications
import tornadofx.*

data class Generator(
    val numberOfHospitals: Int,
    val numberOFPatients: Int,
    val numberOFHospitalizedPatients: Int,
    val numberOfInsurance: Int

)

class GeneratorModel : ItemViewModel<Generator>() {
    val numberOfHospitals = bind(Generator::numberOfHospitals)
    val numberOFPatients = bind(Generator::numberOFPatients)
    val numberOFHospitalizedPatients = bind(Generator::numberOFHospitalizedPatients)
    val numberOfInsurance = bind(Generator::numberOfInsurance)

    override fun onCommit() {
        super.onCommit()
        item = Generator(
            numberOfHospitals = numberOfHospitals.value.toInt(),
            numberOFPatients = numberOFPatients.value.toInt(),
            numberOfInsurance = numberOfInsurance.value.toInt(),
            numberOFHospitalizedPatients = numberOFHospitalizedPatients.value.toInt()
        )
    }
}


class GeneratorView : View() {

    private val controller: GeneratorController  by inject()
    val generatorData: GeneratorModel by inject()
    override val root = VBox()

    init {
        with(root) {
            goHome()
            form {
                label("number of hospitals")
                textfield("number of hospitals").bind(generatorData.numberOfHospitals)
                label("number of patients")
                textfield("number of patients").bind(generatorData.numberOFPatients)
                label("number of hospitalized patients")
                textfield("number of patients").bind(generatorData.numberOFHospitalizedPatients)
                label("number of insurance companies")
                textfield("number of insurance companies").bind(generatorData.numberOfInsurance)


                button("Save") {
                    setOnAction {

                        if (generatorData.commit()) {
                            val g = generatorData.item
                            controller.generate(g)

                        }
                    }


                }
            }
        }

    }
}

