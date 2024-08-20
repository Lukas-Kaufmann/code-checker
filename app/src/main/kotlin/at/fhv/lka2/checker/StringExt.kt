package at.fhv.lka2.checker

fun String.lowerCaseFirstLetter(): String = replaceFirstChar { it.lowercase() }
