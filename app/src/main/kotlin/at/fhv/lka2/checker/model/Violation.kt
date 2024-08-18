package at.fhv.lka2.checker.model

data class Violation(val rule: Rule, val location: Location) {
    data class Location(val file: String, val line: Int)
}
