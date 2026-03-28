package com.worktual.aibot

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import com.worktual.aibot.internal.BotWebViewClient
import com.worktual.aibot.internal.LoadingOverlayView
import org.json.JSONObject

/**
 * Worktual AI Bot — drop-in chatbot view for Android.
 *
 * Add this to any layout or Activity. It loads the bot WebView
 * and shows an animated loading overlay until the chat is ready.
 *
 * Usage:
 * ```kotlin
 * val bot = WorktualAIBot(context,
 *     config = WorktualAIBotConfig(webchatId = "YOUR_ID"),
 *     listener = object : WorktualAIBotListener {
 *         override fun onClose() { finish() }
 *     }
 * )
 * setContentView(bot)
 * ```
 */
class WorktualAIBot(
    context: Context,
    private val config: WorktualAIBotConfig,
    private val listener: WorktualAIBotListener
) : FrameLayout(context) {

    private val webView: WebView
    private val loadingOverlay: LoadingOverlayView
    private val handler = Handler(Looper.getMainLooper())
    private var loaderVisible = true

    init {
        // 1. WebView (bottom layer)
        webView = createWebView(context)
        addView(webView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        // 2. Loading overlay (top layer)
        loadingOverlay = LoadingOverlayView(context, config)
        addView(loadingOverlay, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        // Start loading
        loadingOverlay.startAnimations()
        webView.loadUrl(config.buildUrl())
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(context: Context): WebView {
        return WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.mediaPlaybackRequiresUserGesture = false
            settings.allowFileAccess = false

            overScrollMode = OVER_SCROLL_NEVER
            setLayerType(LAYER_TYPE_HARDWARE, null)

            webViewClient = BotWebViewClient(config.maxLoadTimeMs)

            addJavascriptInterface(JsBridge(), "WorktualBridge")
        }
    }

    private fun hideLoader() {
        if (!loaderVisible) return
        loaderVisible = false
        loadingOverlay.completeAndHide {
            listener.onReady()
        }
    }

    /** Clean up WebView resources. Call this in Activity.onDestroy(). */
    fun destroy() {
        handler.removeCallbacksAndMessages(null)
        loadingOverlay.cleanup()
        webView.stopLoading()
        webView.destroy()
    }

    /* ── JS Bridge ── */

    private inner class JsBridge {
        @JavascriptInterface
        fun postMessage(json: String) {
            try {
                val data = JSONObject(json)

                handler.post { listener.onMessage(data) }

                when (data.optString("type")) {
                    "webchat_ready" -> handler.post { hideLoader() }
                    "webchat_end" -> handler.post { listener.onClose() }
                }
            } catch (_: Exception) {
                // Ignore non-JSON messages
            }
        }
    }
}
