package mono.keycommand

import mono.common.Key

/**
 * An enum class to contains all shortcut key command.
 */
enum class KeyCommand(
    vararg val keyCodes: Int,
    private val commandKeyState: MetaKeyState = MetaKeyState.ANY
) {
    IDLE,

    DESELECTION(Key.KEY_ESC),
    DELETE(Key.KEY_BACKSPACE, Key.KEY_DELETE),

    MOVE_LEFT(Key.KEY_ARROW_LEFT),
    MOVE_UP(Key.KEY_ARROW_UP),
    MOVE_RIGHT(Key.KEY_ARROW_RIGHT),
    MOVE_DOWN(Key.KEY_ARROW_DOWN),

    ADD_RECTANGLE(Key.KEY_R),
    ADD_TEXT(Key.KEY_T),
    ADD_LINE(Key.KEY_L),

    ENTER_EDIT_MODE(Key.KEY_ENTER),
    SELECTION_MODE(Key.KEY_V, commandKeyState = MetaKeyState.OFF),

    COPY(Key.KEY_C, commandKeyState = MetaKeyState.ON),
    CUT(Key.KEY_X, commandKeyState = MetaKeyState.ON),
    DUPLICATE(Key.KEY_D, commandKeyState = MetaKeyState.ON),
    ;

    private enum class MetaKeyState {
        ON, OFF, ANY
    }

    companion object {
        private val KEYCODE_TO_COMMAND_MAP: Map<Int, List<KeyCommand>> =
            values().fold(mutableMapOf<Int, MutableList<KeyCommand>>()) { map, type ->
                for (keyCode in type.keyCodes) {
                    map.getOrPut(keyCode) { mutableListOf() }.add(type)
                }
                map
            }

        internal fun getCommandByKey(keyCode: Int, hasCommandKey: Boolean): KeyCommand =
            KEYCODE_TO_COMMAND_MAP[keyCode]
                ?.firstOrNull {
                    when (it.commandKeyState) {
                        MetaKeyState.ANY -> true
                        MetaKeyState.ON -> hasCommandKey
                        MetaKeyState.OFF -> !hasCommandKey
                    }
                }
                ?: IDLE
    }
}
