package com.worktual.aibot

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView

/**
 * Pre-loads the bot WebView in the background to warm up the cache.
 *
 * Call [preload] early (e.g. in Application.onCreate() or your main Activity).
 * When the user later opens the bot, it loads from cache — nearly instant.
 *
 * Usage:
 * ```kotlin
 * // In Application or main Activity
 * val preloader = WorktualAIBotPreloader(this, "YOUR_WEBCHAT_ID")
 * preloader.preload()
 *
 * // When done (optional)
 * preloader.destroy()
 * ```
 */
class WorktualAIBotPreloader(
    private val context: Context,
    private val config: WorktualAIBotConfig
) {
    private var webView: WebView? = null

    constructor(context: Context, webchatId: String) : this(
        context,
        WorktualAIBotConfig(webchatId = webchatId)
    )

    @SuppressLint("SetJavaScriptEnabled")
    fun preload() {
        if (webView != null) return

        webView = WebView(context).apply {
            // Minimal size — invisible
            layoutParams = android.view.ViewGroup.LayoutParams(0, 0)

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT

            loadUrl(config.buildUrl())
        }
    }

    fun destroy() {
        webView?.stopLoading()
        webView?.destroy()
        webView = null
    }
}
