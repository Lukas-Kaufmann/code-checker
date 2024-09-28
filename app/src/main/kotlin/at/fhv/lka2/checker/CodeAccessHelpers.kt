package at.fhv.lka2.checker

import com.github.javaparser.Position
import org.eclipse.cdt.core.dom.ast.IASTNode
import java.nio.file.Path
import kotlin.io.path.useLines

fun Path.getSourceText(from: Position, to: Position): String {
    return useLines { lines ->
        lines.withIndex()
            .filter { (index, _) -> index + 1 in from.line..to.line }
            .mapIndexed { index, (_, line) ->
                when (index + from.line) {
                    from.line -> {
                        if (from.line == to.line) {
                            line.substring((from.column - 1) until to.column)
                        } else {
                            line.substring(from.column - 1)
                        }
                    }

                    to.line -> if(to.column == 1) "" else line.substring(0, to.column)
                    else -> line
                }
            }
            .joinToString("\n")
    }
}

fun IASTNode.getSourceText(): String {
    val file = Path.of(fileLocation.fileName)
    fileLocation
    return file.getSourceText(
        from = Position(fileLocation.startingLineNumber, 1),
        to = Position(fileLocation.endingLineNumber + 1, 1)
    ).trim()
}
