package com.worktual.aibot

import org.json.JSONObject

/**
 * Listener for Worktual AI Bot events.
 */
interface WorktualAIBotListener {
    /** Called when the chat content is fully loaded and visible. */
    fun onReady() {}

    /** Called when the user closes the chat. */
    fun onClose()

    /** Called on every message received from the chat WebView. */
    fun onMessage(data: JSONObject) {}
}
