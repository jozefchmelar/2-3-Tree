package Model

import java.util.*

val ts = 1287913512



object Data {

    var hospitals: List<Hospital> = ArrayList()

    init {
        val rc = Collections.nCopies(100, Any()).mapIndexed { index: Int, _: Any? -> BirthNumber("$index") }
        val pa = rc.map { Patient(it, Name("bla", "la"), Insurance.Vzp, genHospit()) }
        var t = HashMap<BirthNumber, Patient>() // = mutableMapOf()
        pa.forEach {
            t.put(it.birthNumber, it)
        }
        hospitals = listOf(Hospital("test",t),Hospital("test2",t),Hospital("test3",t),Hospital("test4",t))
    }

}

data class BirthNumber(val birthNumber: String)
data class Name(val first: String, val last: String)

data class Hospital(
    val name: String,
    var patients: Map<BirthNumber, Patient>
)

data class Patient(
    val birthNumber: BirthNumber,
    val name: Name,
    val insurance: Insurance,
    val hospitalizations: List<Hospitalization>
)

data class Hospitalization(
    val start: Date,
    val end: Date?,
    val diagnosis: String
)

enum class Insurance(id: Int) {
    Vzp(1) {
        override fun toString() = "Vseobecna zdravotna poistovna"
    }
}

fun genHospit() = Collections.nCopies(100, Any()).mapIndexed { index: Int, _: Any? -> Hospitalization(start = Date((ts + index * 1000).toLong()), end = null, diagnosis = "programator $index") }
