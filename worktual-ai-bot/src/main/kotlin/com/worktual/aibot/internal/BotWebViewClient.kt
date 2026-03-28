package com.worktual.aibot.internal

import android.webkit.WebView
import android.webkit.WebViewClient

internal class BotWebViewClient(
    private val maxLoadTimeMs: Long
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.evaluateJavascript(buildInjectedJS(), null)
    }

    private fun buildInjectedJS(): String = """
        (function() {
            var t = setInterval(function() {
                var m = document.querySelectorAll(
                    '.message, .chat-message, .msg-content, [class*="message"]'
                );
                var i = document.querySelector('input[placeholder], textarea[placeholder]');
                if (m.length > 0 || i) {
                    clearInterval(t);
                    WorktualBridge.postMessage(
                        JSON.stringify({ type: "webchat_ready" })
                    );
                }
            }, 200);
            setTimeout(function() {
                clearInterval(t);
                WorktualBridge.postMessage(
                    JSON.stringify({ type: "webchat_ready" })
                );
            }, $maxLoadTimeMs);
        })();
    """.trimIndent()
}
