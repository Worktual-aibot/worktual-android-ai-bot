package com.worktual.aibot

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import org.json.JSONObject

/**
 * Singleton manager for instant bot loading.
 *
 * Preloads the bot WebView once, keeps it alive, and shows/hides instantly.
 * Same approach as the React Native "always mounted hidden" pattern.
 *
 * Usage:
 * ```kotlin
 * // 1. Preload early (e.g. in Application.onCreate() or main Activity)
 * WorktualAIBotManager.preload(activity, "YOUR_WEBCHAT_ID")
 *
 * // 2. Show instantly when user taps button (no loading screen!)
 * WorktualAIBotManager.show(activity)
 *
 * // 3. Hide when user closes (bot stays loaded for next open)
 * WorktualAIBotManager.hide()
 *
 * // 4. Clean up when app is done
 * WorktualAIBotManager.destroy()
 * ```
 */
object WorktualAIBotManager {

    private var bot: WorktualAIBot? = null
    private var container: FrameLayout? = null
    private var isShowing = false
    private var externalListener: WorktualAIBotListener? = null

    /**
     * Preload the bot in the background. Call this early in your app lifecycle.
     * The bot WebView loads hidden — when you call [show], it appears instantly.
     *
     * @param activity The Activity to attach the hidden bot to.
     * @param webchatId Your webchat ID from Worktual.
     * @param config Optional full configuration.
     */
    @JvmStatic
    @JvmOverloads
    fun preload(
        activity: Activity,
        webchatId: String,
        config: WorktualAIBotConfig? = null
    ) {
        if (bot != null) return // Already preloaded

        val botConfig = config ?: WorktualAIBotConfig(webchatId = webchatId)

        val botView = WorktualAIBot(activity, botConfig, object : WorktualAIBotListener {
            override fun onClose() {
                hide()
                externalListener?.onClose()
            }

            override fun onReady() {
                externalListener?.onReady()
            }

            override fun onMessage(data: JSONObject) {
                externalListener?.onMessage(data)
            }
        })

        // Wrap in a container for show/hide control
        val wrapper = FrameLayout(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // Start hidden
            visibility = View.GONE
        }
        wrapper.addView(
            botView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        // Add to the Activity's root view
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(wrapper)

        bot = botView
        container = wrapper
    }

    /**
     * Show the bot instantly. Must call [preload] first.
     *
     * @param activity The current Activity (used to re-attach if needed).
     * @param listener Optional listener for bot events.
     */
    @JvmStatic
    @JvmOverloads
    fun show(activity: Activity? = null, listener: WorktualAIBotListener? = null) {
        externalListener = listener

        // Re-attach to new Activity if needed (e.g. after config change)
        if (activity != null && container?.parent == null) {
            val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
            rootView.addView(container)
        }

        container?.visibility = View.VISIBLE
        // Ensure window insets are applied so the bot sits below the status bar
        bot?.let { ViewCompat.requestApplyInsets(it) }
        isShowing = true
    }

    /**
     * Hide the bot. The WebView stays alive for instant re-open.
     */
    @JvmStatic
    fun hide() {
        container?.visibility = View.GONE
        isShowing = false
    }

    /**
     * Whether the bot is currently visible.
     */
    @JvmStatic
    fun isVisible(): Boolean = isShowing

    /**
     * Destroy the bot and free all resources.
     * Call this when the app is shutting down.
     */
    @JvmStatic
    fun destroy() {
        hide()
        container?.let { wrapper ->
            (wrapper.parent as? ViewGroup)?.removeView(wrapper)
        }
        bot?.destroy()
        bot = null
        container = null
        externalListener = null
    }
}
