/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.nav

import mono.html.Div
import mono.html.Span
import mono.html.SvgIcon
import org.w3c.dom.Element

internal fun Element.WorkingFileToolbar() {
    Div("working-file-container") {
        Div("divider")
        Div("file-info") {
            Span("title", "File name")
            Div("menu-down-icon") {
                SvgIcon(
                    width = 12,
                    height = 12,
                    viewPortWidth = 16,
                    viewPortHeight = 16,
                    "M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z"
                )
            }
        }
    }
}
