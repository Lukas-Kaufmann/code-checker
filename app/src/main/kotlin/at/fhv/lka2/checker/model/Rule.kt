package at.fhv.lka2.checker.model

import com.github.javaparser.ast.visitor.VoidVisitorAdapter

abstract class Rule : VoidVisitorAdapter<MutableList<Violation>>()
