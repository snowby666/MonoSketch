package mono.html.toolbar.view.shapetool

import mono.html.toolbar.ActionManager
import mono.html.toolbar.RetainableActionType
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController.Visibility
import mono.html.toolbar.view.shapetool.TextSectionViewController.TextAlignVisibility
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.combineLiveData
import mono.livedata.map
import mono.shape.ShapeExtraManager
import mono.shape.extra.RectangleExtra
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
import org.w3c.dom.HTMLElement

class ShapeToolViewController(
    lifecycleOwner: LifecycleOwner,
    container: HTMLElement,
    actionManager: ActionManager,
    selectedShapesLiveData: LiveData<Set<AbstractShape>>,
    shapeManagerVersionLiveData: LiveData<Int>
) {
    private val singleShapeLiveData: LiveData<AbstractShape?> =
        combineLiveData(
            selectedShapesLiveData,
            shapeManagerVersionLiveData
        ) { selected, _ -> selected.singleOrNull() }

    private val shapesLiveData: LiveData<Set<AbstractShape>> =
        combineLiveData(
            selectedShapesLiveData,
            shapeManagerVersionLiveData
        ) { selected, _ -> selected }

    private val retainableActionLiveData =
        combineLiveData(
            actionManager.retainableActionLiveData,
            ShapeExtraManager.defaultExtraStateUpdateLiveData
        ) { action, _ -> action }

    init {
        ReorderSectionViewController(
            lifecycleOwner,
            container,
            singleShapeLiveData,
            actionManager::setOneTimeAction
        )
        TransformToolViewController(
            lifecycleOwner,
            container,
            singleShapeLiveData,
            actionManager::setOneTimeAction
        )

        AppearanceSectionViewController(
            lifecycleOwner,
            container,
            fillOptions = getFillOptions(),
            strokeOptions = getBorderOptions(),
            headOptions = getHeadOptions(),
            createFillAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData),
            createBorderAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData),
            createBorderAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData),
            createStartHeadAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData),
            createEndHeadAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData),
            actionManager::setOneTimeAction
        )

        TextSectionViewController(
            lifecycleOwner,
            container,
            createTextAlignLiveData(shapesLiveData, retainableActionLiveData),
            actionManager::setOneTimeAction
        )
    }

    private fun getFillOptions(): List<OptionItem> =
        ShapeExtraManager.getAllPredefinedRectangleFillStyles()
            .map { OptionItem(it.id, it.displayName) }

    private fun getBorderOptions(): List<OptionItem> =
        ShapeExtraManager.getAllPredefinedStrokeStyles()
            .map { OptionItem(it.id, it.displayName) }

    private fun getHeadOptions(): List<OptionItem> =
        ShapeExtraManager.getAllPredefinedAnchorChars()
            .map { OptionItem(it.id, it.displayName) }

    private fun createFillAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<Visibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> Visibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Rectangle -> shape.extra.toFillAppearanceVisibilityState()
                        is Text -> shape.extra.boundExtra.toFillAppearanceVisibilityState()
                        is Group,
                        is Line,
                        is MockShape -> Visibility.Hide
                    }
                }
            }
        }
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map {
            val defaultState = when (it) {
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT ->
                    ShapeExtraManager.defaultRectangleExtra.userSelectedFillStyle
                RetainableActionType.ADD_LINE,
                RetainableActionType.IDLE -> null
            }
            if (defaultState != null) {
                val selectedFillPosition =
                    ShapeExtraManager.getAllPredefinedRectangleFillStyles().indexOf(defaultState)
                Visibility.Visible(
                    ShapeExtraManager.defaultRectangleExtra.isFillEnabled,
                    selectedFillPosition
                )
            } else {
                Visibility.Hide
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createBorderAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<Visibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> Visibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Rectangle -> shape.extra.toBorderAppearanceVisibilityState()
                        is Text -> shape.extra.boundExtra.toBorderAppearanceVisibilityState()
                        is Group,
                        is Line,
                        is MockShape -> Visibility.Hide
                    }
                }
            }
        }
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map {
            val defaultState = when (it) {
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT ->
                    ShapeExtraManager.defaultRectangleExtra.userSelectedBorderStyle
                RetainableActionType.ADD_LINE,
                RetainableActionType.IDLE -> null
            }
            if (defaultState != null) {
                val selectedFillPosition =
                    ShapeExtraManager.getAllPredefinedStrokeStyles().indexOf(defaultState)
                Visibility.Visible(
                    ShapeExtraManager.defaultRectangleExtra.isBorderEnabled,
                    selectedFillPosition
                )
            } else {
                Visibility.Hide
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createStartHeadAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<Visibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> Visibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Line -> shape.toStartHeadAppearanceVisibilityState()
                        is Rectangle,
                        is Text,
                        is Group,
                        is MockShape -> Visibility.Hide
                    }
                }
            }
        }
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map {
            val defaultState = when (it) {
                RetainableActionType.ADD_LINE ->
                    ShapeExtraManager.defaultLineExtra.userSelectedStartAnchor
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT,
                RetainableActionType.IDLE -> null
            }
            if (defaultState != null) {
                val selectedStartHeaderPosition =
                    ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(defaultState)
                Visibility.Visible(
                    ShapeExtraManager.defaultLineExtra.isStartAnchorEnabled,
                    selectedStartHeaderPosition
                )
            } else {
                Visibility.Hide
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createEndHeadAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<Visibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> Visibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Line -> shape.toEndHeadAppearanceVisibilityState()
                        is Rectangle,
                        is Text,
                        is Group,
                        is MockShape -> Visibility.Hide
                    }
                }
            }
        }
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map {
            val defaultState = when (it) {
                RetainableActionType.ADD_LINE ->
                    ShapeExtraManager.defaultLineExtra.userSelectedEndAnchor
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT,
                RetainableActionType.IDLE -> null
            }
            if (defaultState != null) {
                val selectedFillPosition =
                    ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(defaultState)
                Visibility.Visible(
                    ShapeExtraManager.defaultLineExtra.isEndAnchorEnabled,
                    selectedFillPosition
                )
            } else {
                Visibility.Hide
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createAppearanceVisibilityLiveData(
        selectedShapeVisibilityLiveData: LiveData<Visibility?>,
        defaultVisibilityLiveData: LiveData<Visibility>
    ): LiveData<Visibility> = combineLiveData(
        selectedShapeVisibilityLiveData,
        defaultVisibilityLiveData
    ) { selected, default -> selected ?: default }

    private fun RectangleExtra.toFillAppearanceVisibilityState(): Visibility {
        val selectedFillPosition =
            ShapeExtraManager.getAllPredefinedRectangleFillStyles()
                .indexOf(userSelectedFillStyle)
        return Visibility.Visible(isFillEnabled, selectedFillPosition)
    }

    private fun RectangleExtra.toBorderAppearanceVisibilityState(): Visibility {
        val selectedBorderPosition =
            ShapeExtraManager.getAllPredefinedStrokeStyles().indexOf(userSelectedBorderStyle)
        return Visibility.Visible(isBorderEnabled, selectedBorderPosition)
    }

    private fun Line.toStartHeadAppearanceVisibilityState(): Visibility {
        val selectedStartHeadPosition =
            ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(extra.userSelectedStartAnchor)
        return Visibility.Visible(extra.isStartAnchorEnabled, selectedStartHeadPosition)
    }

    private fun Line.toEndHeadAppearanceVisibilityState(): Visibility {
        val selectedEndHeadPosition =
            ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(extra.userSelectedEndAnchor)
        return Visibility.Visible(extra.isEndAnchorEnabled, selectedEndHeadPosition)
    }

    private fun createTextAlignLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<TextAlignVisibility> {
        val selectedTextAlignLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> TextAlignVisibility.Hide
                else -> {
                    val text = it.single() as? Text
                    val editableText = text?.takeIf(Text::isTextEditable)
                    editableText?.extra?.textAlign?.let(TextAlignVisibility::Visible)
                }
            }
        }
        val defaultTextAlignLiveData = retainableActionTypeLiveData.map {
            if (it == RetainableActionType.ADD_TEXT) {
                TextAlignVisibility.Visible(ShapeExtraManager.defaultTextAlign)
            } else {
                TextAlignVisibility.Hide
            }
        }
        return combineLiveData(
            selectedTextAlignLiveData,
            defaultTextAlignLiveData
        ) { selected, default -> selected ?: default }
    }
}
