package com.worktual.aibot

import android.graphics.Color

/**
 * Configuration for the Worktual AI Bot.
 *
 * @param webchatId Your unique webchat ID provided by Worktual.
 * @param baseUrl Custom bot URL if self-hosted (defaults to Worktual production).
 * @param loadingLogoResId Drawable resource ID for the loading screen logo.
 * @param loadingLogoWidth Logo width in dp (default: 80).
 * @param loadingLogoHeight Logo height in dp (default: 80).
 * @param loadingTitle Title shown on loading screen (default: "AI Assistant").
 * @param loadingSubtitle Subtitle shown on loading screen (default: "Loading your chat...").
 * @param primaryColor Colour for progress bar & spinner (default: #575CFF).
 * @param loadingBackground Loading screen background colour (default: #F8F9FB).
 * @param maxLoadTimeMs Max ms to wait before force-showing chat (default: 6000).
 */
data class WorktualAIBotConfig(
    val webchatId: String,
    val baseUrl: String = DEFAULT_BASE_URL,
    val loadingLogoResId: Int? = null,
    val loadingLogoWidth: Int = 80,
    val loadingLogoHeight: Int = 80,
    val loadingTitle: String = "AI Assistant",
    val loadingSubtitle: String = "Loading your chat...",
    val primaryColor: Int = Color.parseColor("#575CFF"),
    val loadingBackground: Int = Color.parseColor("#F8F9FB"),
    val maxLoadTimeMs: Long = 6000L
) {
    companion object {
        const val DEFAULT_BASE_URL =
            "https://ccaas-storage.worktual.co.uk/chat/ailivebot.html"
    }

    fun buildUrl(): String {
        val sep = if ("?" in baseUrl) "&" else "?"
        return "${baseUrl}${sep}webchatid=${webchatId}&isHeader=1"
    }
}
