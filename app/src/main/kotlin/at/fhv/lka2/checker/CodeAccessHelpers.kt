package at.fhv.lka2.checker

import com.github.javaparser.Position
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

                    to.line -> line.substring(0, to.column)
                    else -> line
                }
            }
            .joinToString("\n")
    }
}
