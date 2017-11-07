package app.gui

import Model.Data
import Model.genDummy
import Model.load
import Model.save
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import tornadofx.*

fun main(args: Array<String>) {
    launch<MyApp>(args)
}

class MyApp : App(MainView::class)

class MainView : View() {

    override val root = BorderPane()

    val topView = find(TopView::class)
    val centerView = find(CenterView::class)

    init {

        reloadStylesheetsOnFocus()

        with(root) {
            top = topView.root
            center = centerView.root
        }
    }
}

fun View.goHome(f: () -> Unit = {}) = hbox {

    button("Back") {
        action {
            f()
            replaceWith(
                CenterView::class,
                ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.RIGHT)
                , true, true
            )
            close()
        }
    }
}

class CenterView : View() {

    var label: Label by singleAssign()
    val menu = listOf(

        Pair("1.Vyhladanie zaznamov pacienta|Zrusenie hospitalizacie", PatienRecordView::class),


        Pair("3.Vykonanie záznamu o zacati   hospitalizácii pacienta", HospitalizationsView::class),


        Pair("5.Pacienti v nemocnici za obdobie  ", RangeHospitalized::class),

        Pair("6.Pridanie pacienta", AddPatientView::class),

        Pair("7.Faktura  ", InvoiceView::class),

        Pair("Hospitalizovani pacienti v nemocnici", CurrentlyHospitalized::class),

        Pair("Pridanie nemocnice", HospitalsView::class),

        Pair("Zrusenie nemocnice", CancelHospitalsView::class),

        Pair("Generator", GeneratorView::class),
        Pair("test", TestView::class)


    )
    override val root = vbox {
        prefWidth = 800.toDouble()
        prefHeight = 600.toDouble()
        style {
            padding = box(20.px)
        }
        menu.forEach {
            hbox {
                button(it.first) {
                    hboxConstraints { margin = Insets(5.0) }
                    action {
                        replaceWith(it.second, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))

                    }
                }
            }
        }

    }
}

class TopView : View() {

    override val root = vbox {
        menubar {
            menu("File") {
                item("Save", "Shortcut+S").action {
                    save()
                }
                item("Load").action {
                    load()
                }

                item("Generate").action {
                    genDummy()
                }

            }
        }

    }
}

class BottomView : View() {
    override val root = Label("Bottom View")
}

