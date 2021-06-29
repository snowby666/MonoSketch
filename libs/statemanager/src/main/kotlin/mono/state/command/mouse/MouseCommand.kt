package mono.state.command.mouse

import mono.graphics.geo.MousePointer
import mono.state.MainStateManager
import mono.state.command.CommandEnvironment

/**
 * A strategy interface for mouse interaction command happens on [MainStateManager]
 */
internal sealed interface MouseCommand {
    /**
     * CSS mouse cursor value to show during command execution.
     */
    val mouseCursor: String?

    /**
     * Handles mouse events.
     * Returns true when the action finishes.
     */
    fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean
}