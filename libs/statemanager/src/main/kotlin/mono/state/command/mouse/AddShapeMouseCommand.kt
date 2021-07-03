package mono.state.command.mouse

import mono.common.exhaustive
import mono.common.nullToFalse
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.add
import mono.shape.command.ChangeBound
import mono.shape.remove
import mono.shape.shape.AbstractShape
import mono.shape.shape.Rectangle
import mono.state.command.CommandEnvironment

/**
 * A [MouseCommand] to add new shape.
 */
internal class AddShapeMouseCommand(private val shapeFactory: ShapeFactory) : MouseCommand {
    override val mouseCursor: String = "crosshair"

    private var workingShape: AbstractShape? = null

    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean =
        when (mousePointer) {
            is MousePointer.Down -> {
                val shape =
                    shapeFactory.createShape(mousePointer.point, environment.workingParentGroup.id)
                workingShape = shape
                environment.shapeManager.add(shape)
                environment.clearSelectedShapes()
                false
            }
            is MousePointer.Drag -> {
                environment.changeShapeBound(mousePointer.mouseDownPoint, mousePointer.point)
                false
            }
            is MousePointer.Up -> {
                environment.changeShapeBound(mousePointer.mouseDownPoint, mousePointer.point)
                if (workingShape?.isValid().nullToFalse()) {
                    environment.addSelectedShape(workingShape)
                } else {
                    environment.shapeManager.remove(workingShape)
                }
                workingShape = null
                true
            }

            is MousePointer.Move,
            is MousePointer.Click,
            MousePointer.Idle -> true
        }.exhaustive

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

internal sealed class ShapeFactory {
    abstract fun createShape(position: Point, parentId: Int): AbstractShape

    object RectangleFactory : ShapeFactory() {
        override fun createShape(position: Point, parentId: Int): AbstractShape =
            Rectangle(position, position, parentId = parentId)
    }
}
