package mono.state

import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.html.canvas.CanvasViewController
import mono.html.canvas.CanvasViewController.BoundType
import mono.shape.ShapeManager
import mono.shape.command.ChangeBound
import mono.shape.remove
import mono.shape.shape.AbstractShape

/**
 * A model class to manage selected shapes and render the selection bound.
 */
class SelectedShapeManager(
    private val shapeManager: ShapeManager,
    private val canvasManager: CanvasViewController,
    private val requestRedraw: () -> Unit
) {
    var selectedShapes: Set<AbstractShape> = emptySet()
        private set

    fun set(vararg shapes: AbstractShape?) {
        selectedShapes = shapes.filterNotNull().toSet()
        updateInteractionBound()
    }

    fun toggleSelection(shape: AbstractShape) {
        if (shape in selectedShapes) {
            selectedShapes -= shape
        } else {
            selectedShapes += shape
        }
        updateInteractionBound()
    }

    fun deleteSelectedShapes() {
        for (shape in selectedShapes) {
            shapeManager.remove(shape)
        }
        selectedShapes = emptySet()
        updateInteractionBound()
    }

    fun moveSelectedShape(offsetRow: Int, offsetCol: Int) {
        for (shape in selectedShapes) {
            val bound = shape.bound
            val newPosition = Point(bound.left + offsetCol, bound.top + offsetRow)
            val newBound = shape.bound.copy(position = newPosition)
            shapeManager.execute(ChangeBound(shape, newBound))
        }

        updateInteractionBound()
    }

    private fun updateInteractionBound() {
        val bound = if (selectedShapes.isNotEmpty()) {
            Rect.byLTRB(
                selectedShapes.minOf { it.bound.left },
                selectedShapes.minOf { it.bound.top },
                selectedShapes.maxOf { it.bound.right },
                selectedShapes.maxOf { it.bound.bottom }
            )
        } else {
            null
        }
        canvasManager.drawInteractionBound(bound, BoundType.NINE_DOTS)
        requestRedraw()
    }
}
