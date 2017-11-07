package app.controller

import Model.genDummy
import app.gui.Generator
import app.gui.GeneratorModel
import tornadofx.*

class GeneratorController : Controller() {

    fun generate(generatorModel: Generator) {
        genDummy(generatorModel)
    }

}