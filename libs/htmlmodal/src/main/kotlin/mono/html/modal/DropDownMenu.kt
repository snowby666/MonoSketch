package mono.html.modal

import kotlinx.browser.document
import mono.common.Cancelable
import mono.common.setTimeout
import mono.html.Div
import mono.html.Input
import mono.html.InputType
import mono.html.Li
import mono.html.Ul
import mono.html.setOnClickListener
import mono.html.setOnFocusOut
import org.w3c.dom.Element

/**
 * A modal for dropdown menu
 */
class DropDownMenu(classes: String, items: List<Item>, private val onClickAction: (Item) -> Unit) {
    private val menu: Element?

    private var dismissTimeout: Cancelable? = null

    init {
        menu = document.body?.Div(classes = "drop-down-menu $classes") {
            initItems(items)

            val hiddenInput = Input(inputType = InputType.CHECK_BOX, classes = "hidden-input") {
                setOnFocusOut { dismiss() }
                focus()
            }

            onmousedown = {
                setTimeout(5) {
                    dismissTimeout?.cancel()
                    hiddenInput.focus()
                }
            }
        }
    }

    private fun Element.initItems(items: List<Item>) {
        Ul {
            for (item in items) {
                when (item) {
                    Item.Divider -> Li(classes = "drop-down-divider")
                    is Item.Text -> Li(classes = "drop-down-item") {
                        innerText = item.title
                        setOnClickListener {
                            onClickAction(item)
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun dismiss() {
        dismissTimeout = setTimeout(20) {
            menu?.remove()
        }
    }

    sealed class Item {
        object Divider : Item()
        class Text(val title: String, val key: Any) : Item()
    }
}
