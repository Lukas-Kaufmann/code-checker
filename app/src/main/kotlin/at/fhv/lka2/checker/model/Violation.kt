package at.fhv.lka2.checker.model

import java.io.File

data class Violation(val rule: Rule, val location: Location, val message: String?) {
    data class Location(val file: File, val line: Int)
}
