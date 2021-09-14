package mono.state.command.mouse

import mono.common.exhaustive
import mono.common.nullToFalse
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.command.ChangeBound
import mono.shape.command.UpdateTextEditingMode
import mono.shape.shape.Text
import mono.state.command.CommandEnvironment
import mono.state.command.text.EditTextShapeHelper

/**
 * A [MouseCommand] to add new text shape.
 * This command does two jobs:
 * 1. Identify the initial bound for the text shape
 * 2. Open a modal for entering text content when mouse up.
 */
internal class AddTextMouseCommand(private val isTextEditable: Boolean) : MouseCommand {
    override val mouseCursor: String = "crosshair"

    private var workingShape: Text? = null
    override fun execute(
        environment: CommandEnvironment,
        mousePointer: MousePointer
    ): MouseCommand.CommandResultType =
        when (mousePointer) {
            is MousePointer.Down -> {
                val shape = Text(
                    mousePointer.point,
                    mousePointer.point,
                    parentId = environment.workingParentGroup.id,
                    isTextEditable = isTextEditable
                )
                workingShape = shape
                environment.addShape(shape)
                environment.clearSelectedShapes()
                MouseCommand.CommandResultType.WORKING
            }
            is MousePointer.Drag -> {
                environment.changeShapeBound(mousePointer.mouseDownPoint, mousePointer.point)
                MouseCommand.CommandResultType.WORKING
            }
            is MousePointer.Up -> {
                onMouseUp(environment, mousePointer)
                MouseCommand.CommandResultType.WORKING_PHASE2
            }

            is MousePointer.Move,
            is MousePointer.Click,
            is MousePointer.DoubleClick,
            MousePointer.Idle -> MouseCommand.CommandResultType.UNKNOWN
        }.exhaustive

    private fun onMouseUp(environment: CommandEnvironment, mousePointer: MousePointer.Up) {
        val text = workingShape ?: return
        environment.changeShapeBound(mousePointer.mouseDownPoint, mousePointer.point)

        if (!text.isBoundValid()) {
            workingShape = null
            return
        }

        environment.addSelectedShape(text)

        if (isTextEditable) {
            environment.enterEditingMode()
            environment.shapeManager.execute(UpdateTextEditingMode(text, true))
            EditTextShapeHelper.showEditTextDialog(environment, text) {
                environment.shapeManager.execute(UpdateTextEditingMode(text, false))
                environment.exitEditingMode(it.isNotEmpty())
                workingShape = null
            }
        }
    }

    private fun CommandEnvironment.changeShapeBound(point1: Point, point2: Point) {
        val currentShape = workingShape ?: return
        val rect = Rect.byLTRB(
            left = point1.left,
            top = point1.top,
            right = point2.left,
            bottom = point2.top
        )

        shapeManager.execute(ChangeBound(currentShape, rect))
    }
}
