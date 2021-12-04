@file:Suppress("FunctionName")

package mono.export

import kotlinx.browser.document
import mono.html.Div
import mono.html.Pre
import mono.html.Span
import mono.html.Svg
import mono.html.SvgPath
import mono.html.TextArea
import mono.html.setAttributes
import mono.html.setOnClickListener
import org.w3c.dom.Element

/**
 * A modal which is for showing the board rendering selected shapes and allowing users to copy as
 * text.
 */
internal class ExportShapesModal {
    private var root: Element? = null
    fun show(content: String) {
        root = document.body?.Div(classes = "export-text") {
            Div(classes = "export-text__modal") {
                CloseButton()
                Span(text = "Export", classes = "export-text__title")
                Content(content)

                setOnClickListener {
                    it.stopPropagation()
                }
            }

            setOnClickListener { dismiss() }
        }
    }

    private fun Element.CloseButton() {
        Span(classes = "export-text__close") {
            SvgIcon(16) {
                /* ktlint-disable max-line-length */
                SvgPath("M13.854 2.146a.5.5 0 0 1 0 .708l-11 11a.5.5 0 0 1-.708-.708l11-11a.5.5 0 0 1 .708 0Z")
                SvgPath("M2.146 2.146a.5.5 0 0 0 0 .708l11 11a.5.5 0 0 0 .708-.708l-11-11a.5.5 0 0 0-.708 0Z")
                /* ktlint-enable max-line-length */
            }

            setOnClickListener { dismiss() }
        }
    }

    private fun Element.Content(content: String) {
        Div(classes = "export-text__content") {
            val textContent = Pre(text = content) {
                setAttributes("contenteditable" to true)
            }
            val textBox = TextArea(classes = "hidden", content = content)
            CopyButton {
                textBox.value = textContent.innerText
                textBox.select()
                document.execCommand("copy")
            }
        }
    }

    private fun Element.CopyButton(copyContent: () -> Unit) {
        Span(classes = "export-text__copy") {
            SvgIcon(24, 24) {
                /* ktlint-disable max-line-length */
                SvgPath("M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z")
                /* ktlint-enable max-line-length */
            }

            setAttributes("title" to "Copy")
            setOnClickListener { copyContent() }
        }
    }

    private fun Element.SvgIcon(size: Int, pathBlock: Element.() -> Unit = {}) =
        SvgIcon(size, size, pathBlock)

    private fun Element.SvgIcon(width: Int, height: Int, pathBlock: Element.() -> Unit) {
        Svg {
            setAttributes(
                "width" to width,
                "height" to height,
                "fill" to "currentColor",
                "viewBox" to "0 0 $width $height"
            )

            pathBlock()
        }
    }

    private fun dismiss() {
        root?.remove()
    }
}
