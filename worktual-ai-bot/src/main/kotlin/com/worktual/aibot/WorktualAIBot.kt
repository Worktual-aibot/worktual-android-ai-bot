package com.worktual.aibot

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        // Background colour fills the status bar area behind the notch/cutout
        setBackgroundColor(config.statusBarColor)

        // 1. WebView (bottom layer)
        webView = createWebView(context)
        addView(webView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        // 2. Loading overlay (top layer)
        loadingOverlay = LoadingOverlayView(context, config)
        addView(loadingOverlay, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        // 3. Push content below the status bar so the bot header & close
        //    button are never hidden behind system bars.
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

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
