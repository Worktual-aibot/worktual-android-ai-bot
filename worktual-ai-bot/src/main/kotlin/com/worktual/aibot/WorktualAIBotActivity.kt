package com.worktual.aibot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

/**
 * Ready-to-use Activity that wraps [WorktualAIBot].
 *
 * Launch with one line:
 * ```kotlin
 * WorktualAIBotActivity.launch(context, "YOUR_WEBCHAT_ID")
 * ```
 */
class WorktualAIBotActivity : AppCompatActivity() {

    private var bot: WorktualAIBot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webchatId = intent.getStringExtra(EXTRA_WEBCHAT_ID) ?: run {
            finish()
            return
        }
        val baseUrl = intent.getStringExtra(EXTRA_BASE_URL)

        val config = WorktualAIBotConfig(
            webchatId = webchatId,
            baseUrl = baseUrl ?: WorktualAIBotConfig.DEFAULT_BASE_URL
        )

        bot = WorktualAIBot(this, config, object : WorktualAIBotListener {
            override fun onClose() {
                finish()
            }

            override fun onReady() {}

            override fun onMessage(data: JSONObject) {}
        })

        setContentView(bot)
    }

    override fun onDestroy() {
        bot?.destroy()
        bot = null
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_WEBCHAT_ID = "worktual_webchat_id"
        private const val EXTRA_BASE_URL = "worktual_base_url"

        /**
         * Launch the bot as a full-screen Activity.
         *
         * @param context Android context.
         * @param webchatId Your webchat ID from Worktual.
         * @param baseUrl Optional custom base URL.
         */
        @JvmStatic
        @JvmOverloads
        fun launch(context: Context, webchatId: String, baseUrl: String? = null) {
            val intent = Intent(context, WorktualAIBotActivity::class.java).apply {
                putExtra(EXTRA_WEBCHAT_ID, webchatId)
                baseUrl?.let { putExtra(EXTRA_BASE_URL, it) }
            }
            context.startActivity(intent)
        }
    }
}
