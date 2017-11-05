package app.gui

import Model.genDummy
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import tornadofx.*

fun main(args: Array<String>) {
    genDummy()
    launch<MyApp>(args)
}

class MyApp : App(MainView::class)

class MainView : View() {

    override val root = BorderPane()

    val topView    = find(TopView::class)
    val centerView = find(CenterView::class)

    init {
        reloadStylesheetsOnFocus()

        with(root) {
            top    = topView.root
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
            )
        }
    }
}

class CenterView : View() {

    var label: Label by singleAssign()
    val menu = listOf(

        Pair("1.Vyhladanie zaznamov pacienta", PatienRecordView::class),

        Pair("2.Vyhľadanie záznamov pacienta/ov v zadanej nemocnici", AddPatientView::class),

        Pair("3.Vykonanie záznamu o zacati   hospitalizácii pacienta", HospitalizationsView::class),

        Pair("4.Vykonanie záznamu o ukonceni hospitalizácie pacienta", AddPatientView::class),

        Pair("5.Pacienti v nemocnici za obdobie", AddPatientView::class),

        Pair("6.Pridanie pacienta", AddPatientView::class),

        Pair("7.Faktura", AddPatientView::class),

        Pair("8,9,10,Hospitalizovani pacienti v nemocnici", CurrentlyHospitalized::class),

        Pair("11. Pridanie nemocnice", HospitalsView::class),

        Pair("11,12,13 Nemocnice", HospitalsView::class),

        Pair("11,12,13 Nemocnice", HospitalsView::class)

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
                    println("Saving!")
                }
                item("Load").action {
                    println("Loading!")
                }
                item("Clear").action {
                    println("Clear!")
                }

            }
        }

    }
}

class BottomView : View() {
    override val root = Label("Bottom View")
}

