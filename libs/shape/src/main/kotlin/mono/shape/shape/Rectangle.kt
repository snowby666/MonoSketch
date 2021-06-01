package mono.shape.shape

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.serialization.AbstractSerializableShape

/**
 * A rectangle shape.
 */
class Rectangle(
    rect: Rect,
    parentId: Int? = null
) : AbstractShape(parentId = parentId) {

    override var bound: Rect = rect
        set(value) = update {
            val isUpdated = field != value
            field = value
            isUpdated
        }

    override var extra: Extra = Extra(FillStyle.STYLE_0_BORDER)
        private set

    /**
     * The content of this shape also includes all vertical/horizontal lines created by [startPoint]
     * and [endPoint].
     */
    constructor(startPoint: Point, endPoint: Point, parentId: Int? = null) : this(
        Rect.byLTRB(startPoint.left, startPoint.top, endPoint.left, endPoint.top),
        parentId
    )

    override fun toSerializableShape(): AbstractSerializableShape {
        TODO("Not yet implemented")
    }

    override fun setBound(newBound: Rect) {
        bound = newBound
    }

    override fun isValid(): Boolean = bound.width > 1 && bound.height > 1

    /**
     * A data class which contains extra information of a rectangle.
     */
    @Serializable
    data class Extra(
        @SerialName("sf")
        val fillStyle: FillStyle
    ) {

        data class Updater(val fillStyle: FillStyle? = null) : ExtraUpdater {
            fun combine(extra: Extra?): Extra =
                extra?.copy(
                    fillStyle = fillStyle ?: extra.fillStyle
                ) ?: Extra(fillStyle ?: FillStyle.STYLE_0_BORDER)
        }

        companion object {
            val DEFAULT = Extra(FillStyle.STYLE_0_BORDER)
        }
    }

    @Serializable
    enum class FillStyle {
        @SerialName("s0f")
        STYLE_0_FILL,

        @SerialName("s0b")
        STYLE_0_BORDER
    }
}
